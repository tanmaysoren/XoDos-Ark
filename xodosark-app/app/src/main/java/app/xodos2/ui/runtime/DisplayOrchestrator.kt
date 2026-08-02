package app.xodos2.ui.runtime

import android.content.Context
import android.content.SharedPreferences
import android.os.Handler
import android.util.Log
import app.xodos2.NativeBridge
import app.xodos2.TerminalSessionIds
import app.xodos2.WaylandBridge
import app.xodos2.ui.prefs.AppPrefs
import com.termux.x11.controller.core.GPUInformation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

object DisplayOrchestrator {
    private const val HEADLESS_X11_INJECT_DELAY_MS = 400L
    private const val X11_SOCKET_WAIT_POLL_MS = 120L
    private const val X11_SOCKET_WAIT_MAX_POLLS = 120 // ~14s

    data class WaylandEnvState(
        val hiddenInjectedKey: String,
    )

    fun prepareWaylandRuntimeAndStartServer(context: Context, waylandRuntimeDir: String): Boolean {
        val keymapTarget = File(waylandRuntimeDir, "keymap_us.xkb")
        if (!keymapTarget.exists()) {
            try {
                context.assets.open("keymap_us.xkb").use { input ->
                    keymapTarget.outputStream().use { out ->
                        input.copyTo(out)
                    }
                }
            } catch (_: Throwable) {
                return false
            }
        }
        return try {
            WaylandBridge.nativeStartServer(waylandRuntimeDir)
            true
        } catch (_: Throwable) {
            false
        }
    }

    fun ensureArchWaylandDisplaySession() {
        if (!NativeBridge.isSessionAlive(TerminalSessionIds.ARCH_WAYLAND_DISPLAY)) {
            NativeBridge.spawnSession(TerminalSessionIds.ARCH_WAYLAND_DISPLAY, 24, 80)
        }
    }

    fun ensureDebianX11DisplaySession(hasDebianRootfs: Boolean): Boolean {
        if (!hasDebianRootfs) return false
        if (NativeBridge.isSessionAlive(TerminalSessionIds.DEBIAN_X11_DISPLAY)) return true
        return NativeBridge.spawnSessionInRootfs(
            TerminalSessionIds.DEBIAN_X11_DISPLAY,
            24, 80,
            TerminalSessionIds.rootfsKindForNativeId(TerminalSessionIds.DEBIAN_X11_DISPLAY),
        )
    }

    fun runWineX11DesktopStartupScript(
        context: Context,
        prefs: SharedPreferences,
        headlessInjectHandler: Handler,
        hasWineRootfs: Boolean,
    ) {
        if (!hasWineRootfs) return
        updateContainersSystemEnvironment(context, prefs)
        if (!NativeBridge.isSessionAlive(TerminalSessionIds.WINE_X11_DISPLAY)) {
            if (!NativeBridge.spawnSessionInRootfs(
                    TerminalSessionIds.WINE_X11_DISPLAY,
                    24, 80,
                    TerminalSessionIds.rootfsKindForNativeId(TerminalSessionIds.WINE_X11_DISPLAY),
                )
            ) return
        }
        val targetId = TerminalSessionIds.WINE_X11_DISPLAY
        val user = (prefs.getString("wine_x11_startup_script", "") ?: "").trim()
        val graphicsEnv = buildSystemGraphicsEnv(prefs, context)
val payload = buildString {
    graphicsEnv.lines()
        .filter { it.isNotBlank() }
        .forEach { line ->
            append(line)          // line already contains "export ..." or "unset ..."
            append('\n')
        }
    append(AppPrefs.buildDebianX11ImplicitEnvSnippet())
    if (user.isNotEmpty()) {
        append(user)
        if (!user.endsWith('\n')) append('\n')
    }
}
        if (payload.isEmpty()) return
        val bytes = payload.toByteArray(Charsets.UTF_8)
        val x0 = File(context.filesDir, "usr/tmp/.X11-unix/X0")
        var polls = 0
        val inject = {
            headlessInjectHandler.postDelayed(
                { NativeBridge.writeInput(targetId, bytes) },
                HEADLESS_X11_INJECT_DELAY_MS,
            )
        }
        val waiter = object : Runnable {
            override fun run() {
                polls += 1
                if (x0.exists() || polls >= X11_SOCKET_WAIT_MAX_POLLS) {
                    inject()
                    return
                }
                headlessInjectHandler.postDelayed(this, X11_SOCKET_WAIT_POLL_MS)
            }
        }
        headlessInjectHandler.post(waiter)
    }

    fun ensureArchX11DisplaySession(): Boolean {
        if (NativeBridge.isSessionAlive(TerminalSessionIds.ARCH_X11_DISPLAY)) return true
        return NativeBridge.spawnSessionInRootfs(
            TerminalSessionIds.ARCH_X11_DISPLAY,
            24, 80,
            TerminalSessionIds.rootfsKindForNativeId(TerminalSessionIds.ARCH_X11_DISPLAY),
        )
    }

    fun runArchX11DesktopStartupScript(
        context: Context,
        prefs: SharedPreferences,
        headlessInjectHandler: Handler,
        hasArchRootfs: Boolean,
    ) {
        if (!hasArchRootfs) return
        updateContainersSystemEnvironment(context, prefs)
        if (!ensureArchX11DisplaySession()) return
        val targetId = TerminalSessionIds.ARCH_X11_DISPLAY
        val user = AppPrefs.readArchX11DesktopStartupScript(prefs).trim()
        val graphicsEnv = buildSystemGraphicsEnv(prefs, context)
val payload = buildString {
    graphicsEnv.lines()
        .filter { it.isNotBlank() }
        .forEach { line ->
            append(line)          // line already contains "export ..." or "unset ..."
            append('\n')
        }
    append(AppPrefs.buildDebianX11ImplicitEnvSnippet())
    if (user.isNotEmpty()) {
        append(user)
        if (!user.endsWith('\n')) append('\n')
    }
}
        if (payload.isEmpty()) return
        val bytes = payload.toByteArray(Charsets.UTF_8)
        val x0 = File(context.filesDir, "usr/tmp/.X11-unix/X0")
        var polls = 0
        val inject = {
            headlessInjectHandler.postDelayed(
                { NativeBridge.writeInput(targetId, bytes) },
                HEADLESS_X11_INJECT_DELAY_MS,
            )
        }
        val waiter = object : Runnable {
            override fun run() {
                polls += 1
                if (x0.exists() || polls >= X11_SOCKET_WAIT_MAX_POLLS) {
                    inject()
                    return
                }
                headlessInjectHandler.postDelayed(this, X11_SOCKET_WAIT_POLL_MS)
            }
        }
        headlessInjectHandler.post(waiter)
    }

    fun runDebianX11DesktopStartupScript(
        context: Context,
        prefs: SharedPreferences,
        headlessInjectHandler: Handler,
        hasDebianRootfs: Boolean,
    ) {
        if (!hasDebianRootfs) return
        updateContainersSystemEnvironment(context, prefs)
        if (!ensureDebianX11DisplaySession(hasDebianRootfs)) return
        val targetId = TerminalSessionIds.DEBIAN_X11_DISPLAY
        val user = AppPrefs.readDebianDesktopStartupScript(prefs).trim()
        val graphicsEnv = buildSystemGraphicsEnv(prefs, context)
val payload = buildString {
    graphicsEnv.lines()
        .filter { it.isNotBlank() }
        .forEach { line ->
            append(line)          // line already contains "export ..." or "unset ..."
            append('\n')
        }
    append(AppPrefs.buildDebianX11ImplicitEnvSnippet())
    if (user.isNotEmpty()) {
        append(user)
        if (!user.endsWith('\n')) append('\n')
    }
}
        if (payload.isEmpty()) return
        val bytes = payload.toByteArray(Charsets.UTF_8)
        val x0 = File(context.filesDir, "usr/tmp/.X11-unix/X0")
        var polls = 0
        val inject = {
            headlessInjectHandler.postDelayed(
                { NativeBridge.writeInput(targetId, bytes) },
                HEADLESS_X11_INJECT_DELAY_MS,
            )
        }
        val waiter = object : Runnable {
            override fun run() {
                polls += 1
                if (x0.exists() || polls >= X11_SOCKET_WAIT_MAX_POLLS) {
                    inject()
                    return
                }
                headlessInjectHandler.postDelayed(this, X11_SOCKET_WAIT_POLL_MS)
            }
        }
        headlessInjectHandler.post(waiter)
    }

    fun buildWaylandAndGraphicsEnvSnippet(
        socketName: String,
        vulkanMode: String,
        openGLMode: String,
        context: Context? = null,
    ): String {
        val prefs = context?.getSharedPreferences("xodos2_prefs", Context.MODE_PRIVATE)
        val isMtkOrMali = context?.let { GPUInformation.isMali(it) || GPUInformation.isMediaTek(it) }
            ?: (GPUInformation.isMali(null) || GPUInformation.isMediaTek(null))
        val b = StringBuilder()
        b.append("export WAYLAND_DISPLAY=").append(socketName).append("\n")
        when (openGLMode) {
            "VIRGL" -> {
                b.append("unset VK_ICD_FILENAMES MESA_VK_WSI_PRESENT_MODE MESA_LOADER_DRIVER_OVERRIDE VKD3D_FEATURE_LEVEL VK_DRIVER_FILES VN_DEBUG || true\n")
                b.append("export GALLIUM_DRIVER=virpipe\n")
                b.append("export MESA_LOADER_DRIVER_OVERRIDE=virpipe\n")
                b.append("export LIBGL_ALWAYS_SOFTWARE=0\n")
                b.append("export VTEST_SOCKET_NAME=/run/xodos2-virgl/vtest.sock\n")
                b.append("export VTEST_RENDERER_SOCKET_NAME=/run/xodos2-virgl/vtest.sock\n")
                if (isMtkOrMali) {
                    b.append("export VIRGL_NO_10BIT=1\n")
                    b.append("export MESA_GL_VERSION_OVERRIDE=3.3\n")
                    b.append("export MESA_GLSL_VERSION_OVERRIDE=330\n")
                    b.append("export MESA_GLES_VERSION_OVERRIDE=3.2\n")
                    b.append("export MESA_EXTENSION_OVERRIDE=\"-GL_ARB_gpu_shader5 -GL_ARB_geometry_shader4 -GL_ARB_transform_feedback2\"\n")
                    b.append("export ANGLE_DEFAULT_INITIALIZATION_PLATFORM=gl\n")
                    b.append("export ANGLE_FEATURE_OVERRIDES_ENABLED=loseContextOnOutOfMemory\n")
                }
            }
            "ZINK" -> {
                b.append("export VKD3D_FEATURE_LEVEL=12_0\n")
                b.append("export MESA_LOADER_DRIVER_OVERRIDE=zink\n")
                b.append("export MESA_VK_WSI_PRESENT_MODE=${if (isMtkOrMali) "fifo" else "mailbox"}\n")
                b.append("export LIBGL_ALWAYS_SOFTWARE=0\n")
                if (isMtkOrMali) {
                    b.append("export MESA_GL_VERSION_OVERRIDE=3.3\n")
                    b.append("export MESA_GLSL_VERSION_OVERRIDE=330\n")
                }
            }
            "GL4ES" -> {
                b.append("export MESA_GL_VERSION_OVERRIDE=2.1 \n")
                b.append("export LIBGL_FB=3\n")
                b.append("export MESA_VK_WSI_PRESENT_MODE=${if (isMtkOrMali) "fifo" else "mailbox"}\n")
                b.append("export LIBGL_ALWAYS_SOFTWARE=0\n")
                b.append("export LD_LIBRARY_PATH=/usr/lib/aarch64-linux-gnu/gl4es:\$LD_LIBRARY_PATH\n")
            }
            "LLVMPIPE" -> {
                b.append("unset MESA_GL_VERSION_OVERRIDE LIBGL_FB VK_ICD_FILENAMES MESA_VK_WSI_PRESENT_MODE MESA_LOADER_DRIVER_OVERRIDE VKD3D_FEATURE_LEVEL VK_DRIVER_FILES VN_DEBUG || true\n")             
                b.append("export GALLIUM_DRIVER=llvmpipe\n")
                b.append("export MESA_LOADER_DRIVER_OVERRIDE=llvmpipe\n")
                b.append("export LIBGL_ALWAYS_SOFTWARE=1\n")
            }
            else -> {
                val customPath = AppPrefs.getCustomDriverFilePath(prefs, openGLMode)
                val driverLower = openGLMode.lowercase()
                b.append("unset MESA_GL_VERSION_OVERRIDE LIBGL_FB VK_ICD_FILENAMES MESA_VK_WSI_PRESENT_MODE MESA_LOADER_DRIVER_OVERRIDE VKD3D_FEATURE_LEVEL VK_DRIVER_FILES VN_DEBUG || true\n")             
                b.append("export GALLIUM_DRIVER=\"$driverLower\"\n")
                b.append("export MESA_LOADER_DRIVER_OVERRIDE=\"$driverLower\"\n")
                b.append("export LIBGL_ALWAYS_SOFTWARE=0\n")
                if (!customPath.isNullOrBlank()) {
                    val dir = File(customPath).parent
                    if (!dir.isNullOrBlank()) {
                        b.append("export LD_LIBRARY_PATH=\"$dir:\$LD_LIBRARY_PATH\"\n")
                        b.append("export MESA_DRIVER_PATH=\"$dir\"\n")
                    }
                }
            }
        }
        when (vulkanMode) {
            "VENUS" -> {
                b.append("export VK_ICD_FILENAMES=/usr/share/vulkan/icd.d/virtio_icd.json\n")
                b.append("export VK_DRIVER_FILES=/usr/share/vulkan/icd.d/virtio_icd.json\n")
                b.append("export VN_DEBUG=vtest\n")
                b.append("export VTEST_SOCKET_NAME=/run/xodos2-virgl/venus.sock\n")
                b.append("export VTEST_RENDERER_SOCKET_NAME=/run/xodos2-virgl/venus.sock\n")
                if (isMtkOrMali) {
                    b.append("export MESA_VK_WSI_PRESENT_MODE=fifo\n")
                    b.append("export VIRGL_NO_10BIT=1\n")
                    b.append("export MESA_VK_ABORT_ON_ERROR=0\n")
                }
            }
            "TURNIP" -> {
                b.append("export VKD3D_FEATURE_LEVEL=12_0\n")
                b.append("export MESA_VK_WSI_PRESENT_MODE=${if (isMtkOrMali) "fifo" else "mailbox"}\n")
                b.append("export VK_ICD_FILENAMES=/usr/share/vulkan/icd.d/freedreno_icd.aarch64.json\n")
                b.append("export VK_DRIVER_FILES=/usr/share/vulkan/icd.d/freedreno_icd.aarch64.json\n")
                b.append("export TU_DEBUG=noconform\n")
            }
            "LLVMPIPE" -> {
                b.append("unset VK_ICD_FILENAMES MESA_VK_WSI_PRESENT_MODE VK_DRIVER_FILES VN_DEBUG || true\n")
                b.append("export VK_ICD_FILENAMES=/dev/null\n")
            }
            else -> {
                val customVkPath = AppPrefs.getCustomDriverFilePath(prefs, vulkanMode)
                if (!customVkPath.isNullOrBlank()) {
                    b.append("export VK_ICD_FILENAMES=\"$customVkPath\"\n")
                    b.append("export VK_DRIVER_FILES=\"$customVkPath\"\n")
                    if (customVkPath.endsWith(".so")) {
                        b.append("export LD_PRELOAD=\"$customVkPath:\$LD_PRELOAD\"\n")
                    }
                    b.append("export VKD3D_FEATURE_LEVEL=12_0\n")
                    b.append("export TU_DEBUG=noconform\n")
                } else {
                    val vkLower = vulkanMode.lowercase()
                    b.append("export VK_ICD_FILENAMES=\"/usr/share/vulkan/icd.d/${vkLower}_icd.aarch64.json\"\n")
                    b.append("export VK_DRIVER_FILES=\"/usr/share/vulkan/icd.d/${vkLower}_icd.aarch64.json\"\n")
                }
            }
        }
        return b.toString()
    }

    fun buildSystemGraphicsEnv(prefs: SharedPreferences, context: Context? = null): String {
        val vulkan = prefs.getString("desktop_vulkan_mode", "LLVMPIPE") ?: "LLVMPIPE"
        val openGL = prefs.getString("desktop_opengl_mode", "LLVMPIPE") ?: "LLVMPIPE"
        val isMtkOrMali = context?.let { GPUInformation.isMali(it) || GPUInformation.isMediaTek(it) }
            ?: (GPUInformation.isMali(null) || GPUInformation.isMediaTek(null))
        val sb = StringBuilder()
        sb.append("export DISPLAY=:0\n")
        when (openGL) {
            "VIRGL" -> {
                sb.append("unset VK_ICD_FILENAMES MESA_VK_WSI_PRESENT_MODE MESA_LOADER_DRIVER_OVERRIDE VKD3D_FEATURE_LEVEL VK_DRIVER_FILES VN_DEBUG || true\n")
                sb.append("export GALLIUM_DRIVER=virpipe\n")
                sb.append("export MESA_LOADER_DRIVER_OVERRIDE=virpipe\n")
                sb.append("export LIBGL_ALWAYS_SOFTWARE=0\n")
                sb.append("export VTEST_SOCKET_NAME=/run/xodos2-virgl/vtest.sock\n")
                sb.append("export VTEST_RENDERER_SOCKET_NAME=/run/xodos2-virgl/vtest.sock\n")
                if (isMtkOrMali) {
                    sb.append("export VIRGL_NO_10BIT=1\n")
                    sb.append("export MESA_GL_VERSION_OVERRIDE=3.3\n")
                    sb.append("export MESA_GLSL_VERSION_OVERRIDE=330\n")
                    sb.append("export MESA_GLES_VERSION_OVERRIDE=3.2\n")
                    sb.append("export MESA_EXTENSION_OVERRIDE=\"-GL_ARB_gpu_shader5 -GL_ARB_geometry_shader4 -GL_ARB_transform_feedback2\"\n")
                    sb.append("export ANGLE_DEFAULT_INITIALIZATION_PLATFORM=gl\n")
                    sb.append("export ANGLE_FEATURE_OVERRIDES_ENABLED=loseContextOnOutOfMemory\n")
                }
            }
            "ZINK" -> {
                sb.append("export VKD3D_FEATURE_LEVEL=12_0\n")
                sb.append("export MESA_LOADER_DRIVER_OVERRIDE=zink\n")               
                sb.append("export GALLIUM_DRIVER=zink\n")
                sb.append("export MESA_VK_WSI_PRESENT_MODE=${if (isMtkOrMali) "fifo" else "mailbox"}\n")
                sb.append("export LIBGL_ALWAYS_SOFTWARE=0\n")
                if (isMtkOrMali) {
                    sb.append("export MESA_GL_VERSION_OVERRIDE=3.3\n")
                    sb.append("export MESA_GLSL_VERSION_OVERRIDE=330\n")
                }
            }
            "GL4ES" -> {
                sb.append("export MESA_GL_VERSION_OVERRIDE=2.1 \n")
                sb.append("export LIBGL_FB=3\n")
                sb.append("export MESA_VK_WSI_PRESENT_MODE=${if (isMtkOrMali) "fifo" else "mailbox"}\n")
                sb.append("export LIBGL_ALWAYS_SOFTWARE=0\n")
                sb.append("export LD_LIBRARY_PATH=/usr/lib/aarch64-linux-gnu/gl4es:\$LD_LIBRARY_PATH\n")
            }
            "LLVMPIPE" -> {
                sb.append("unset MESA_GL_VERSION_OVERRIDE LIBGL_FB VK_ICD_FILENAMES MESA_VK_WSI_PRESENT_MODE MESA_LOADER_DRIVER_OVERRIDE VKD3D_FEATURE_LEVEL VK_DRIVER_FILES VN_DEBUG || true\n")
                sb.append("export GALLIUM_DRIVER=llvmpipe\n")
                sb.append("export MESA_LOADER_DRIVER_OVERRIDE=llvmpipe\n")
                sb.append("export LIBGL_ALWAYS_SOFTWARE=1\n")
            }
            else -> {
                val customPath = AppPrefs.getCustomDriverFilePath(prefs, openGL)
                val driverLower = openGL.lowercase()
                sb.append("unset MESA_GL_VERSION_OVERRIDE LIBGL_FB VK_ICD_FILENAMES MESA_VK_WSI_PRESENT_MODE MESA_LOADER_DRIVER_OVERRIDE VKD3D_FEATURE_LEVEL VK_DRIVER_FILES VN_DEBUG || true\n")
                sb.append("export GALLIUM_DRIVER=\"$driverLower\"\n")
                sb.append("export MESA_LOADER_DRIVER_OVERRIDE=\"$driverLower\"\n")
                sb.append("export LIBGL_ALWAYS_SOFTWARE=0\n")
                if (!customPath.isNullOrBlank()) {
                    val dir = File(customPath).parent
                    if (!dir.isNullOrBlank()) {
                        sb.append("export LD_LIBRARY_PATH=\"$dir:\$LD_LIBRARY_PATH\"\n")
                        sb.append("export MESA_DRIVER_PATH=\"$dir\"\n")
                    }
                }
            }
        }
        when (vulkan) {
            "VENUS" -> {
                sb.append("export MESA_VK_WSI_PRESENT_MODE=${if (isMtkOrMali) "fifo" else "mailbox"}\n")
                sb.append("export TU_DEBUG=noconform\n")
                sb.append("export VK_ICD_FILENAMES=/usr/share/vulkan/icd.d/virtio_icd.json\n")
                sb.append("export VK_DRIVER_FILES=/usr/share/vulkan/icd.d/virtio_icd.json\n")
                sb.append("export VN_DEBUG=vtest\n")
                sb.append("export VTEST_SOCKET_NAME=/run/xodos2-virgl/venus.sock\n")
                sb.append("export VTEST_RENDERER_SOCKET_NAME=/run/xodos2-virgl/venus.sock\n")
                if (isMtkOrMali) {
                    sb.append("export VIRGL_NO_10BIT=1\n")
                    sb.append("export MESA_VK_ABORT_ON_ERROR=0\n")
                }
            }
            "TURNIP" -> {
                sb.append("export MESA_VK_WSI_PRESENT_MODE=${if (isMtkOrMali) "fifo" else "mailbox"}\n")
                sb.append("export TU_DEBUG=noconform\n")             
                sb.append("export VK_ICD_FILENAMES=/usr/share/vulkan/icd.d/freedreno_icd.aarch64.json\n")
                sb.append("export VK_DRIVER_FILES=/usr/share/vulkan/icd.d/freedreno_icd.aarch64.json\n")
            }
            "LLVMPIPE" -> {
                sb.append("unset VK_ICD_FILENAMES MESA_VK_WSI_PRESENT_MODE VK_DRIVER_FILES VN_DEBUG || true\n")
                sb.append("export VK_ICD_FILENAMES=/dev/null\n")
            }
            else -> {
                val customVkPath = AppPrefs.getCustomDriverFilePath(prefs, vulkan)
                if (!customVkPath.isNullOrBlank()) {
                    sb.append("export VK_ICD_FILENAMES=\"$customVkPath\"\n")
                    sb.append("export VK_DRIVER_FILES=\"$customVkPath\"\n")
                    if (customVkPath.endsWith(".so")) {
                        sb.append("export LD_PRELOAD=\"$customVkPath:\$LD_PRELOAD\"\n")
                    }
                    sb.append("export VKD3D_FEATURE_LEVEL=12_0\n")
                    sb.append("export TU_DEBUG=noconform\n")
                } else {
                    val vkLower = vulkan.lowercase()
                    sb.append("export VK_ICD_FILENAMES=\"/usr/share/vulkan/icd.d/${vkLower}_icd.aarch64.json\"\n")
                    sb.append("export VK_DRIVER_FILES=\"/usr/share/vulkan/icd.d/${vkLower}_icd.aarch64.json\"\n")
                }
            }
        }
        return sb.toString()
    }

    fun updateContainersSystemEnvironment(context: Context, prefs: SharedPreferences) {
        val envContent = buildSystemGraphicsEnv(prefs, context)
        for (id in 1..3) {
            val containerDir = NativeInstallCoordinator.containerPath(context, id)
            if (!containerDir.isDirectory) continue
            val etcDir = File(containerDir, "etc")
            etcDir.mkdirs()

            // 1. Write /etc/environment with clean key-value pairs
            val envFile = File(etcDir, "environment")
            val cleanEnvLines = envContent.lines()
                .filter { it.trim().startsWith("export ") }
                .map { line ->
                    val raw = line.trim().removePrefix("export ").trim()
                    if (raw.contains("=")) {
                        val parts = raw.split("=", limit = 2)
                        val k = parts[0].trim()
                        val v = parts[1].trim().removeSurrounding("\"").removeSurrounding("'")
                        "$k=\"$v\""
                    } else ""
                }
                .filter { it.isNotBlank() }
            envFile.writeText(cleanEnvLines.joinToString("\n") + "\n")

            // 2. Write /etc/profile.d/xodos_graphics.sh
            val profileDir = File(etcDir, "profile.d")
            profileDir.mkdirs()
            val scriptFile = File(profileDir, "xodos_graphics.sh")
            scriptFile.writeText("#!/bin/sh\n" + envContent)
            try { scriptFile.setExecutable(true, false) } catch (_: Throwable) {}

            val sourceLine = "[ -f /etc/profile.d/xodos_graphics.sh ] && . /etc/profile.d/xodos_graphics.sh"

            // 3. Ensure /etc/bash.bashrc sources /etc/profile.d/xodos_graphics.sh instead of /etc/environment
            val bashrcFile = File(etcDir, "bash.bashrc")
            if (bashrcFile.exists()) {
                var existing = bashrcFile.readText()
                if (existing.contains("source /etc/environment")) {
                    existing = existing.replace("source /etc/environment", sourceLine)
                    bashrcFile.writeText(existing)
                } else if (!existing.contains("xodos_graphics.sh")) {
                    bashrcFile.appendText("\n" + sourceLine + "\n")
                }
            } else {
                bashrcFile.writeText(sourceLine + "\n")
            }

            // 4. Ensure /root/.bashrc also sources /etc/profile.d/xodos_graphics.sh
            val rootDir = File(containerDir, "root")
            if (rootDir.isDirectory) {
                val rootBashrc = File(rootDir, ".bashrc")
                if (rootBashrc.exists()) {
                    var existing = rootBashrc.readText()
                    if (existing.contains("source /etc/environment")) {
                        existing = existing.replace("source /etc/environment", sourceLine)
                        rootBashrc.writeText(existing)
                    } else if (!existing.contains("xodos_graphics.sh")) {
                        rootBashrc.appendText("\n" + sourceLine + "\n")
                    }
                } else {
                    rootBashrc.writeText(sourceLine + "\n")
                }
            }
        }
    }

    // ─── Turnip driver helpers ──────────────────────────────────

    fun getContainerDistroType(context: Context, containerId: Int): String? {
        val prefs = context.getSharedPreferences("xodos2_containers", Context.MODE_PRIVATE)
        return prefs.getString("container_distro_$containerId", null)?.lowercase()
    }

    fun turnipAssetPattern(distroType: String): String {
    // Normalise distro type
    val t = distroType.lowercase()
    return when {
        // Arch family
        t == "archlinux" -> "debian_trixie"
        t == "artix"     -> "debian_trixie"
        t == "manjaro"   -> "debian_trixie"   // treat as Arch if no dedicated asset

        // Debian family – everything goes to the same `debian` driver
        t == "debian"       -> "debian_trixie"
        t == "ubuntu"       -> "debian_trixie"
        t == "trisquel"     -> "debian_trixie"
        t == "deepin"       -> "debian_trixie"
        t == "kali"         -> "debian_trixie"  
        t == "raspbian"     -> "debian_trixie"

        // RPM family 
        t == "fedora"       -> "fedora_43"
        t == "almalinux"    -> "fedora_43"
        t == "rocky"        -> "fedora_43"

        // Alpine
        t == "alpine"       -> "debian_trixie"

        // Void
        t == "void"         -> "void"

        // Fallback 
        else -> "debian_trixie"   
    }
}

    fun hasTurnipTarball(context: Context, distroType: String): Boolean {
        val pattern = turnipAssetPattern(distroType)
        val driversDir = File(context.filesDir, "drivers")
        if (!driversDir.exists()) return false
        val files = driversDir.listFiles { f ->
            f.name.startsWith("turnip_") &&
            f.name.contains(pattern) &&
            f.name.endsWith(".tar.gz") &&
            !f.name.endsWith(".tmp")
        }
        return files != null && files.isNotEmpty()
    }

    /**
     * Extracts a Turnip driver tarball (.tar.gz) into the container rootfs.
     */
/**
 * Extracts a Turnip driver tarball (.tar.gz) into the container rootfs.
 */
suspend fun extractTurnipDriver(context: Context, containerId: Int, distroType: String): Boolean =
    withContext(Dispatchers.IO) {
        val pattern = turnipAssetPattern(distroType)
        val driversDir = File(context.filesDir, "drivers")
        val tarball = driversDir.listFiles { f ->
            f.name.startsWith("turnip_") && f.name.contains(pattern) && f.name.endsWith(".tar.gz")
        }?.firstOrNull() ?: return@withContext false

        val rootfs = NativeInstallCoordinator.containerPath(context, containerId)
        if (!rootfs.isDirectory) return@withContext false

        // Use the same environment that works in the terminal
        val env = mutableMapOf<String, String>()
        env["PATH"] = "/data/data/app.xodos2/files/usr/bin:${System.getenv("PATH") ?: "/system/bin"}"
        env["LD_LIBRARY_PATH"] = "/data/data/app.xodos2/files/usr/lib:${System.getenv("LD_LIBRARY_PATH") ?: ""}"

        val tarFlag = when {
            tarball.name.endsWith(".tar.gz") -> "z"
            tarball.name.endsWith(".tar.xz") -> "J"
            tarball.name.endsWith(".tar") -> ""
            else -> return@withContext false
        }

        val tarExe = File(context.filesDir, "usr/bin/tar")
        val cmd = arrayOf(
            tarExe.absolutePath,
            "-x${tarFlag}f", tarball.absolutePath,
            "-C", rootfs.absolutePath,
            "--exclude=system", "--exclude=apex", "--exclude=data",
            "--exclude=sdcard", "--exclude=storage"
        )
        val pb = ProcessBuilder(*cmd)
            .directory(rootfs)
            .redirectErrorStream(true)
        pb.environment().putAll(env)

        val process = pb.start()
        val exitCode = process.waitFor()

        if (exitCode == 0) {
            val marker = File(rootfs, "etc/.xodos2_turnip_driver_installed")
            marker.parentFile?.mkdirs()
            marker.createNewFile()
            true
        } else false
    }

/**
 * Generic extraction of a driver tarball into a container rootfs.
 * Handles .tar.gz and .tar.xz automatically.
 */
fun extractDriverTarball(context: Context, containerId: Int, tarball: File) {
    val rootfs = NativeInstallCoordinator.containerPath(context, containerId)
    if (!rootfs.isDirectory || !tarball.exists()) return

    val env = mutableMapOf<String, String>()
    env["PATH"] = "/data/data/app.xodos2/files/usr/bin:${System.getenv("PATH") ?: "/system/bin"}"
    env["LD_LIBRARY_PATH"] = "/data/data/app.xodos2/files/usr/lib:${System.getenv("LD_LIBRARY_PATH") ?: ""}"

    val tarFlag = when {
        tarball.name.endsWith(".tar.gz") -> "z"
        tarball.name.endsWith(".tar.xz") -> "J"
        tarball.name.endsWith(".tar") -> ""
        else -> return
    }

    val tarExe = File(context.filesDir, "usr/bin/tar")
    val cmd = arrayOf(
        tarExe.absolutePath,
        "-x${tarFlag}f", tarball.absolutePath,
        "-C", rootfs.absolutePath,
        "--exclude=system", "--exclude=apex", "--exclude=data",
        "--exclude=sdcard", "--exclude=storage"
    )
    try {
        val pb = ProcessBuilder(*cmd)
            .directory(rootfs)
            .redirectErrorStream(true)
        pb.environment().putAll(env)
        val process = pb.start()
        process.waitFor()
    } catch (e: Exception) {
        Log.e("DisplayOrchestrator", "Failed to extract ${tarball.name}", e)
    }
}
    fun isTurnipDriverInstalled(context: Context, containerId: Int): Boolean {
        val rootfs = NativeInstallCoordinator.containerPath(context, containerId)
        val marker = File(rootfs, "etc/.xodos2_turnip_driver_installed")
        return marker.exists()
    }

    fun runArchWaylandStartupScriptIfNeeded(
        prefs: SharedPreferences,
        desktopSocketName: String,
        vulkanMode: String,
        openGLMode: String,
        currentHiddenInjectedKey: String,
    ): WaylandEnvState {
        val hasClients = try {
            WaylandBridge.nativeHasActiveClients()
        } catch (_: Throwable) {
            false
        }
        val hiddenKey = "$desktopSocketName|$vulkanMode|$openGLMode"
        ensureArchWaylandDisplaySession()
        if (currentHiddenInjectedKey != hiddenKey) {
            NativeBridge.writeInput(
                TerminalSessionIds.ARCH_WAYLAND_DISPLAY,
                buildWaylandAndGraphicsEnvSnippet(desktopSocketName, vulkanMode, openGLMode)
                    .toByteArray(Charsets.UTF_8)
            )
        }
        if (!hasClients) {
            val script = prefs.getString("desktop_startup_script", "")?.trim()
            if (!script.isNullOrEmpty()) {
                ensureArchWaylandDisplaySession()
                NativeBridge.writeInput(
                    TerminalSessionIds.ARCH_WAYLAND_DISPLAY,
                    (script + "\n").toByteArray(Charsets.UTF_8)
                )
            }
        }
        return WaylandEnvState(hiddenInjectedKey = hiddenKey)
    }
}