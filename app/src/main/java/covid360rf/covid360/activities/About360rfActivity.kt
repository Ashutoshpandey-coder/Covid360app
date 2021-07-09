package covid360rf.covid360.activities

import android.os.Bundle
import covid360rf.covid360.R
import covid360rf.covid360.databinding.ActivityAboutFoundationBinding

class About360rfActivity : BaseActivity() {

    private lateinit var binding : ActivityAboutFoundationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutFoundationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpActionBar(this,findViewById(R.id.toolbar),getString(R.string.nav_about_360rf))

    }
}