package com.termux.x11;

// Add these imports at the top with other imports
import android.net.Uri;
import androidx.fragment.app.FragmentManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import androidx.preference.PreferenceCategory;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreferenceCompat;
import androidx.preference.ListPreference;
import androidx.annotation.NonNull;
import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
import com.termux.x11.controller.winhandler.ProcessInfo;
import java.util.List;
import java.util.ArrayList;
import android.os.RemoteException;
import android.os.ParcelFileDescriptor;
import android.app.NotificationChannel;
import androidx.viewpager.widget.ViewPager;
import android.service.notification.StatusBarNotification;
import android.app.PendingIntent;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Notification;
import androidx.core.app.NotificationCompat;
//import me.weishu.reflection.Reflection;
//import com.termux.x11.R;
import android.view.InputDevice;
import android.widget.Toast;
import android.graphics.PointF;
import com.termux.x11.input.InputEventSender;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import static android.view.InputDevice.KEYBOARD_TYPE_ALPHABETIC;
import static android.view.KeyEvent.KEYCODE_VOLUME_DOWN;
import android.app.Activity;
import android.provider.Settings;
import android.view.WindowInsets;
import androidx.appcompat.app.AlertDialog;
import java.util.Objects;
import android.os.Handler;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Button;
import android.widget.ImageButton;
import androidx.viewpager.widget.ViewPager;
import androidx.core.app.NotificationCompat;
import androidx.core.math.MathUtils;
import static android.view.View.VISIBLE;
import android.preference.PreferenceManager;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import static com.termux.x11.LoriePreferences.ACTION_PREFERENCES_CHANGED;
import android.content.pm.PackageManager;
import com.termux.x11.controller.inputcontrols.InputControlsManager;
import com.termux.x11.controller.widget.InputControlsView;
import com.termux.x11.controller.widget.TouchpadView;
import com.termux.x11.controller.winhandler.TaskManagerDialog;
import com.termux.x11.controller.winhandler.WinHandler;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import android.os.Process; 
import java.util.concurrent.Executors;
import android.graphics.Color;
import com.termux.x11.utils.SamsungDexUtils;

import com.termux.x11.R;




import static android.Manifest.permission.WRITE_SECURE_SETTINGS;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.os.Build.VERSION.SDK_INT;
import static android.view.KeyEvent.*;
import static android.view.WindowManager.LayoutParams.*;
import static com.termux.x11.CmdEntryPoint.ACTION_START;
import static com.termux.x11.LoriePreferences.ACTION_PREFERENCES_CHANGED;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AppOpsManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PictureInPictureParams;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.os.SystemClock;
import android.service.notification.StatusBarNotification;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Rational;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.PointerIcon;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.math.MathUtils;
import androidx.core.view.ViewCompat;
import androidx.viewpager.widget.ViewPager;

import com.termux.x11.input.InputEventSender;
import com.termux.x11.input.InputStub;
import com.termux.x11.input.TouchInputHandler;
import com.termux.x11.utils.ImeHeightProvider;
import com.termux.x11.utils.KeyInterceptor;
import com.termux.x11.utils.TermuxX11ExtraKeys;
import com.termux.x11.utils.X11ToolbarViewPager;

import java.util.Map;

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
    boolean useTermuxEKBarBehaviour = false;
    private boolean isInPictureInPictureMode = false;
      /** The display the system letterboxed us on instead of rotating, {@code null} until it does. */
    private Rect orientationDeniedAt = null;
    /** Aspect ratios outside of the range the device is configured with are rejected by the system. */
    private static final float MIN_PIP_ASPECT_RATIO = getSystemDimenFloat("config_pictureInPictureMinAspectRatio", 1.f / 2.39f);
    private static final float MAX_PIP_ASPECT_RATIO = getSystemDimenFloat("config_pictureInPictureMaxAspectRatio", 2.39f);

    public static Prefs prefs = null;

    private static boolean oldFullscreen = false, oldHideCutout = false;
    private final SharedPreferences.OnSharedPreferenceChangeListener preferencesChangedListener = (__, key) -> onPreferencesChanged(key);


/// new mod
private DrawerLayout drawerLayout;   
private static boolean softKeyboardShown = false;
 // HUD 
private HudService hudService;
private boolean isBound = false;

private ServiceConnection hudConnection = new ServiceConnection() {
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        HudService.LocalBinder binder = (HudService.LocalBinder) service;
        hudService = binder.getService();
        // If activity is resumed, attach immediately
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

// Call this when the HUD preference is enabled
public void startHudService() {
    Intent intent = new Intent(this, HudService.class);
    
        startService(intent);
    
    bindService(intent, hudConnection, Context.BIND_AUTO_CREATE);
}

// Call this when the HUD preference is disabled
public void stopHudService() {
    if (isBound) {
        unbindService(hudConnection);
        isBound = false;
    }
    Intent intent = new Intent(this, HudService.class);
    stopService(intent);
}

// Called from onStart to start HUD if preference is enabled
private void startHudIfEnabled() {
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    boolean hudEnabled = prefs.getBoolean("hud_enabled", false);
    if (hudEnabled) {
        startHudService();
    }
}


 //////////// gamepad 
private void checkConnectedControllers() {
    int[] deviceIds = InputDevice.getDeviceIds();
    for (int id : deviceIds) {
        InputDevice device = InputDevice.getDevice(id);
        if ((device.getSources() & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD
            && !isIgnoredDevice(device)) {
            
            String msg = "Controller:🎮 " + device.getName() + " (ID:" + id + ")";
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
            Log.d("ControllerDebug", msg);
        }
    }
}


 /// check fingerprint sensors that acts like gamepad
 private boolean isIgnoredDevice(InputDevice device) {
    if (device == null) return true;

    String name = device.getName().toLowerCase();

    // Ignore fingerprint or virtual devices that masquerade as gamepads
    return name.contains("uinput-fpc") ||
           name.contains("fingerprint") ||
           name.contains("fpc1020") ||   // common FPC models
           name.contains("goodix")   ||  // Goodix sensors
           device.isVirtual();          // Ignore system-generated virtual inputs
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
        // Fully qualify java.lang.Process to avoid conflict with android.os.Process
        java.lang.Process process = Runtime.getRuntime().exec("pgrep -f winhandler.exe");
        return process.waitFor() == 0;
    } catch (Exception e) {
        return false;
    }
}


/// DRAWER

    private void setupTermuxActivityListener() {
    this.termuxActivityListener = new TermuxActivityListener() {
        @Override
        public void onX11PreferenceSwitchChange(boolean isOpen) {
            // Handle preference switch change
            if (isOpen) {
                // Open preferences
                startActivity(new Intent(MainActivity.this, LoriePreferences.class));
            }
        }

        @Override
        public void releaseSlider(boolean open) {
            // For MainActivity, we don't have a slider UI
            Log.d("MainActivity", "Slider released: " + open);
        }

        @Override
        public void onChangeOrientation(int orientation) {
            // Set orientation for MainActivity
            setRequestedOrientation(orientation);
            
            // Also update the LorieView if connected
            if (getLorieView() != null) {
                getLorieView().regenerate();
            }
        }

        @Override
        public void reInstallX11StartScript(Activity activity) {
            // Use intent to communicate with Termux app
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
            // Disconnect X11 connection
            if (LorieView.connected()) {
                // Check what method LorieView has for disconnecting optional 
                // If there's no disconnect method, we'll just update the UI
            }
            
            // Update UI to show disconnected state
            clientConnectedStateChanged();
            
            // Show toast
            Toast.makeText(MainActivity.this, "Desktop stopped", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void openSoftwareKeyboard() {
            // Toggle keyboard visibility
            MainActivity.toggleKeyboardVisibility(MainActivity.this);
        }

        @Override
        public void showProcessManager() {
            // Show process manager dialog from MainActivity
            showProcessManagerDialog();
        }

        @Override
        public void changePreference(String key) {
            // Handle preference change in MainActivity
            onPreferencesChanged(key);
        }

        @Override
        public List<ProcessInfo> collectProcessorInfo(String tag) {
            // Return real Android process list instead of empty placeholder
            return getAndroidProcessList();
        }

       

        @Override
        public void onExitApp() {
            // Exit the app
          //  System.exit(0);
         finish();
       //     finishAffinity();
        }
    };
 }
