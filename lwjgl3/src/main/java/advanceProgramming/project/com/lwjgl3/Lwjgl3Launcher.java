package advanceProgramming.project.com.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import advanceProgramming.project.com.Main;

/** Launches the desktop (LWJGL3) application. */
public class Lwjgl3Launcher {
    public static void main(String[] args) {
        if (StartupHelper.startNewJvmIfRequired()) return; // This handles macOS support and helps on Windows.
        createApplication();
    }

    private static Lwjgl3Application createApplication() {
        return new Lwjgl3Application(new Main(), getDefaultConfiguration());
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("HollowNight");

        //// Vsync limits the frames per second to what your hardware can display...
        configuration.useVsync(true);
        //// Limits FPS to the refresh rate of the currently active monitor...
        configuration.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate + 1);

        configuration.setMaximized(true);
        configuration.setWindowIcon("libgdx.png", "libgdx.png", "libgdx.png", "libgdx.png");

        // ==========================================
        // کدی که باید اضافه کنید:
        // تخصیص ۸ بیت حافظه برای Stencil Buffer جهت برش دایره‌ای ماسک‌ها
        // ==========================================
        configuration.setBackBufferConfig(8, 8, 8, 8, 16, 8, 0);

        return configuration;
    }}
