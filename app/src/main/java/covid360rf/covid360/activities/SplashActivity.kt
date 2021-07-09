package covid360rf.covid360.activities

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowInsets
import android.view.WindowManager
import com.google.firebase.auth.FirebaseAuth
import covid360rf.covid360.databinding.ActivitySplashBinding
import covid360rf.covid360.utils.Constants
import covid360rf.covid360.utils.start

class SplashActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        }else{
            @Suppress("DEPRECATION")
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        Handler(Looper.myLooper()!!).postDelayed({
            if (FirebaseAuth.getInstance().currentUser == null) {
                start(IntroActivity::class.java)
            }else{
                start(MainActivity::class.java)
            }

            //start(MainActivity::class.java)

            /**
             * On development, Navigate directly to MainActivity
             * ...instead of authenticating phone
             * start(MainActivity::class.java)
             */
            finish()
        }, Constants.SPLASH_WAIT_TIME)
    }
}