package ink.momoyu.runtime

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.view.KeyEvent
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.androidgamesdk.GameActivity

class MainActivity : GameActivity() {

    companion object {
        init {
            // Load the native library.
            System.loadLibrary("moyu")
        }
    }

    private fun hideSystemUI() {
        // This will put the game behind any cutouts and waterfalls on devices which have
        // them, so the corresponding insets will be non-zero.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS
        }
        // From API 30 onwards, this is the recommended way to hide the system UI, rather than
        // using View.setSystemUiVisibility.
        val decorView = window.decorView
        val controller = WindowInsetsControllerCompat(window, decorView)
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.hide(WindowInsetsCompat.Type.displayCutout())
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // When true, the app will fit inside any system UI windows.
        // When false, we render behind any system UI windows.
        WindowCompat.setDecorFitsSystemWindows(window, false)
        hideSystemUI()
        // You can set IME fields here or in native code using GameActivity_setImeEditorInfoFields.
        // We set the fields in native_engine.cpp.
        super.onCreate(savedInstanceState)
        installBackHandler()
    }

    override fun onResume() {
        super.onResume()
        hideSystemUI()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Ensure the process is killed when the activity is destroyed, to prevent it from being
        // killed in the background and then restarted by the system with a new activity instance.
        // See https://developer.android.com/guide/components/activities/activity-lifecycle#onstop
        System.exit(0)
    }

    private fun installBackHandler() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requestExitFromBack()
            }
        })
    }

    private fun requestExitFromBack() {
        dispatchSyntheticBackKey()
    }

    private fun dispatchSyntheticBackKey() {
        val now = SystemClock.uptimeMillis()
        val down = KeyEvent(now, now, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK, 0)
        val up = KeyEvent(now, now, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK, 0)
        onKeyDown(KeyEvent.KEYCODE_BACK, down)
        onKeyUp(KeyEvent.KEYCODE_BACK, up)
    }

    fun isGooglePlayGames(): Boolean {
        return packageManager.hasSystemFeature("com.google.android.play.feature.HPE_EXPERIENCE")
    }
}
