package com.termux.x11;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AppOpsManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PictureInPictureParams;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.os.Process;
import android.os.RemoteException;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.service.notification.StatusBarNotification;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Rational;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.Display;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.PointerIcon;
import android.view.Surface;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.math.MathUtils;
import androidx.core.view.ViewCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.termux.x11.controller.inputcontrols.InputControlsManager;
import com.termux.x11.controller.widget.InputControlsView;
import com.termux.x11.controller.widget.TouchpadView;
import com.termux.x11.controller.winhandler.ProcessInfo;
import com.termux.x11.controller.winhandler.TaskManagerDialog;
import com.termux.x11.controller.winhandler.WinHandler;
import com.termux.x11.input.InputEventSender;
import com.termux.x11.input.InputStub;
import com.termux.x11.input.TouchInputHandler;
import com.termux.x11.utils.ImeHeightProvider;
import com.termux.x11.utils.KeyInterceptor;
import com.termux.x11.utils.SamsungDexUtils;
import com.termux.x11.utils.TermuxX11ExtraKeys;
import com.termux.x11.utils.X11ToolbarViewPager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import static android.Manifest.permission.WRITE_SECURE_SETTINGS;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.os.Build.VERSION.SDK_INT;
import static android.view.InputDevice.KEYBOARD_TYPE_ALPHABETIC;
import static android.view.KeyEvent.KEYCODE_VOLUME_DOWN;
import static android.view.KeyEvent.*;
import static android.view.View.VISIBLE;
import static android.view.WindowManager.LayoutParams.*;
import static com.termux.x11.CmdEntryPoint.ACTION_START;
import static com.termux.x11.LoriePreferences.ACTION_PREFERENCES_CHANGED;

@SuppressLint("ApplySharedPref")
@SuppressWarnings({"deprecation", "unused"})
public class MainActivity extends LoriePreferences {
    public static final String ACTION_STOP = "com.termux.x11.ACTION_STOP";
    public static final String ACTION_CUSTOM = "com.termux.x11.ACTION_CUSTOM";

    public static Handler handler = new Handler();
    FrameLayout frm;

    protected View lorieContentView;

    private TouchInputHandler mInputHandler;
    protected ICmdEntryInterface service = null;
    public TermuxX11ExtraKeys mExtraKeys;
    private Notification mNotification;
    private final int mNotificationId = 7892;
    NotificationManager mNotificationManager;
    static InputMethodManager inputMethodManager;
    private static boolean showIMEWhileExternalConnected = true;
    private static boolean externalKeyboardConnected = false;
    private View.OnKeyListener mLorieKeyListener;
    private boolean filterOutWinKey = false;
    public boolean useTermuxEKBarBehaviour = false;
    private boolean isInPictureInPictureMode = false;
    private Rect orientationDeniedAt = null;
    private static final float MIN_PIP_ASPECT_RATIO = getSystemDimenFloat("config_pictureInPictureMinAspectRatio", 1.f / 2.39f);
    private static final float MAX_PIP_ASPECT_RATIO = getSystemDimenFloat("config_pictureInPictureMaxAspectRatio", 2.39f);

    public static Prefs prefs = null;

    private static boolean oldFullscreen = false, oldHideCutout = false;
    private final SharedPreferences.OnSharedPreferenceChangeListener preferencesChangedListener = (__, key) -> onPreferencesChanged(key);

    private DrawerLayout drawerLayout;
    private static boolean softKeyboardShown = false;
    private HudService hudService;
    private boolean isBound = false;

    private ServiceConnection hudConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            HudService.LocalBinder binder = (HudService.LocalBinder) service;
            hudService = binder.getService();
            if (isResumed) {
                hudService.attachToActivity(MainActivity.this);
            }
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
            hudService = null;
        }
    };

    private boolean isResumed = false;

    public void startHudService() {
        Intent intent = new Intent(this, HudService.class);
        startService(intent);
        bindService(intent, hudConnection, Context.BIND_AUTO_CREATE);
    }

    public void stopHudService() {
        if (isBound) {
            unbindService(hudConnection);
            isBound = false;
        }
        Intent intent = new Intent(this, HudService.class);
        stopService(intent);
    }

    private void startHudIfEnabled() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean hudEnabled = prefs.getBoolean("hud_enabled", false);
        if (hudEnabled) {
            startHudService();
        }
    }

    private void checkConnectedControllers() {
        int[] deviceIds = InputDevice.getDeviceIds();
        for (int id : deviceIds) {
            InputDevice device = InputDevice.getDevice(id);
            if ((device.getSources() & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD
                    && !isIgnoredDevice(device)) {
                String msg = "Controller:\uD83C\uDFAE " + device.getName() + " (ID:" + id + ")";
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                Log.d("ControllerDebug", msg);
            }
        }
    }

    private boolean isIgnoredDevice(InputDevice device) {
        if (device == null) return true;
        String name = device.getName().toLowerCase();
        return name.contains("uinput-fpc") ||
                name.contains("fingerprint") ||
                name.contains("fpc1020") ||
                name.contains("goodix") ||
                device.isVirtual();
    }

    private boolean isGamepadConnected() {
        int[] deviceIds = InputDevice.getDeviceIds();
        for (int id : deviceIds) {
            InputDevice device = InputDevice.getDevice(id);
            if (device == null) continue;
            if (isIgnoredDevice(device)) continue;
            if ((device.getSources() & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD ||
                    (device.getSources() & InputDevice.SOURCE_JOYSTICK) == InputDevice.SOURCE_JOYSTICK) {
                return true;
            }
        }
        return false;
    }

    public boolean isWineRunning() {
        try {
            java.lang.Process process = Runtime.getRuntime().exec("pgrep -f winhandler.exe");
            return process.waitFor() == 0;
        } catch (Exception e) {
            return false;
        }
    }

    private void setupTermuxActivityListener() {
        this.termuxActivityListener = new TermuxActivityListener() {
            @Override
            public void onX11PreferenceSwitchChange(boolean isOpen) {
                if (isOpen) {
                    startActivity(new Intent(MainActivity.this, LoriePreferences.class));
                }
            }

            @Override
            public void releaseSlider(boolean open) {
                Log.d("MainActivity", "Slider released: " + open);
            }

            @Override
            public void onChangeOrientation(int orientation) {
                setRequestedOrientation(orientation);
                if (getLorieView() != null) {
                    getLorieView().regenerate();
                }
            }

            @Override
            public void reInstallX11StartScript(Activity activity) {
                Intent intent = new Intent();
                intent.setAction("com.termux.action.INSTALL_X11");
                intent.setPackage("com.xodos");
                try {
                    activity.startActivity(intent);
                } catch (Exception e) {
                    Log.e("MainActivity", "Failed to launch Termux installer", e);
                    Toast.makeText(activity, "Please install Termux app first", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void stopDesktop() {
                if (LorieView.connected()) {
                    // Disconnect logic
                }
                clientConnectedStateChanged();
                Toast.makeText(MainActivity.this, "Desktop stopped", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void openSoftwareKeyboard() {
                MainActivity.toggleKeyboardVisibility(MainActivity.this);
            }

            @Override
            public void showProcessManager() {
                showProcessManagerDialog();
            }

            @Override
            public void changePreference(String key) {
                onPreferencesChanged(key);
            }

            @Override
            public List<ProcessInfo> collectProcessorInfo(String tag) {
                return getAndroidProcessList();
            }

            @Override
            public void onExitApp() {
                finish();
            }
        };
    }

    // --- BEGIN COMPATIBILITY LAYER ---
    // Provide the legacy API surface expected by other classes so the project compiles.

    private static MainActivity sInstance = null;
    private LorieView mLorieView = null;
    private static boolean sCapturingEnabled = true;

    /** Legacy accessor used across the codebase */
    public static MainActivity getInstance() { 
        return sInstance; 
    }

    /** Legacy prefs accessor */
    public static Prefs getPrefs() { 
        return prefs; 
    }

    /** Legacy LorieView accessor (may be null until view is created) */
    public LorieView getLorieView() { 
        return mLorieView; 
    }

    /** Set the LorieView reference */
    public void setLorieView(LorieView view) {
        mLorieView = view;
    }

    /** Legacy capturing control used by InputEventSender */
    public static void setCapturingEnabled(boolean enabled) { 
        sCapturingEnabled = enabled; 
    }

    public static boolean isCapturingEnabled() { 
        return sCapturingEnabled; 
    }

    /** Legacy toggle keyboard method (many call sites expect this signature) */
    public static void toggleKeyboardVisibility(MainActivity activity) {
        if (activity == null) return;
        try {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm == null) return;
            View v = activity.getCurrentFocus();
            if (v == null) v = activity.getWindow().getDecorView();

            // Best-effort toggle: hide if active otherwise show
            if (imm.isAcceptingText()) {
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            } else {
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }
        } catch (Throwable t) {
            Log.w("MainActivity", "toggleKeyboardVisibility failed", t);
        }
    }

    /* No-op stubs for instance methods expected by other components. Implement real behaviour as needed. */
    public void toggleMouseAuxButtons() { /* no-op compatibility for now */ }
    public void toggleStylusAuxButtons() { /* no-op compatibility for now */ }
    public void clientConnectedStateChanged() { /* no-op compatibility for now */ }
    public void tryConnect() { /* no-op compatibility for now */ }

    /** Legacy handler used by LorieView: keep signature and return type to satisfy callers. */
    public boolean handleKey(KeyEvent event) { 
        return false; 
    }
    // --- END COMPATIBILITY LAYER ---
}
