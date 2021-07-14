package covid360rf.covid360.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import covid360rf.covid360.databinding.ActivityResultBinding
import covid360rf.covid360.utils.setData
import covid360rf.covid360.utils.start

class ResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val score = intent.getStringExtra(setData.score.toString())

        if (score != null) {
            if (score < 5.toString()) {
                binding.congo.text = "minor symptoms "

                binding.idquote.text = "take the vaccine and stay at home"
            } else {
                binding.idquote.text =
                    "It's Dangerous!! you have severe symptoms of covid-19 Consult with the docter do take care of yourself  "
                binding.congo.text = "Major symptoms"

            }
            if (score < 3.toString()) {
                binding.congo.text = "congratulations"
                binding.idquote.text = "Don't worry!! you have minor symptoms but stay safe"
            }
        }

        binding.button.setOnClickListener {
            start(MainActivity::class.java)
        }
    }
}






