package covid360rf.covid360.activities

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import covid360rf.covid360.databinding.ActivityIntroBinding
import covid360rf.covid360.utils.start


class IntroActivity : AppCompatActivity() {
    
    private lateinit var binding : ActivityIntroBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        }else{
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        binding.btnRegisterIntro.setOnClickListener {
            start(RegisterActivity::class.java)
            finish()
        }
    }
}