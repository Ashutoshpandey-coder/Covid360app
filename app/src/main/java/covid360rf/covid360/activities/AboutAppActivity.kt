package covid360rf.covid360.activities


import android.os.Bundle
import covid360rf.covid360.R
import covid360rf.covid360.databinding.ActivityAboutAppBinding

class AboutAppActivity : BaseActivity() {
    private lateinit var binding : ActivityAboutAppBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutAppBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpActionBar(this,binding.toolbarAboutAppActivity,getString(R.string.nav_about_covid360))
    }
}