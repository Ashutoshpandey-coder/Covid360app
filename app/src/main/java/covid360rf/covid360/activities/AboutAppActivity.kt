package covid360rf.covid360.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import covid360rf.covid360.R
import covid360rf.covid360.databinding.ActivityAbout360rfBinding
import covid360rf.covid360.databinding.ActivityAboutAppBinding
import covid360rf.covid360.utils.Constants
import covid360rf.covid360.utils.startAction

class AboutAppActivity : BaseActivity() {

    private lateinit var binding : ActivityAboutAppBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutAppBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpActionBar(this, findViewById(R.id.toolbar),getString(R.string.nav_about_covid360))

        binding.textAppDevelopers.text = getString(R.string.app_devs, Constants.APP_DEVELOPER)
        binding.textAppDevelopers.setOnClickListener {
            startAction(Intent.ACTION_VIEW, Uri.parse(Constants.FOUNDATION_URL))
        }
    }
}