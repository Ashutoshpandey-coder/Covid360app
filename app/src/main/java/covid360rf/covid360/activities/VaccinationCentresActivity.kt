package covid360rf.covid360.activities


import android.os.Bundle
import covid360rf.covid360.R
import covid360rf.covid360.databinding.ActivityVaccinationCentresBinding


class VaccinationCentresActivity : BaseActivity() {
    private lateinit var binding : ActivityVaccinationCentresBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVaccinationCentresBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpActionBar(this,binding.toolbarVaccinationCentresActivity,getString(R.string.nav_vaccination_info))
    }
}