package covid360rf.covid360.activities

import android.os.Bundle
import covid360rf.covid360.R
import covid360rf.covid360.databinding.ActivitySelfDiagnosisBinding
import covid360rf.covid360.utils.start

class SelfDiagnosisActivity : BaseActivity() {

    private lateinit var binding : ActivitySelfDiagnosisBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelfDiagnosisBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpActionBar(this, findViewById(R.id.toolbar), getString(R.string.nav_self_diagnosis))

        binding.btnGetdtarted.setOnClickListener {
            start(QuestionActivity::class.java)
            finish()
        }
    }
}
