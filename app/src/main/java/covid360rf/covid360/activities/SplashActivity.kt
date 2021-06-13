package covid360rf.covid360.activities

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowInsets
import android.view.WindowManager
import covid360rf.covid360.R
import covid360rf.covid360.databinding.ActivitySplashBinding

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

            if (RegisterActivity().getCurrentUserId().isEmpty()) {
                startActivity(Intent(this@SplashActivity, IntroActivity::class.java))
            }else{
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))

            }
            finish()
        },2000)
    }
}