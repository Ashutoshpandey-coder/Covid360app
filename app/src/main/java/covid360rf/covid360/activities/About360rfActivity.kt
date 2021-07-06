package covid360rf.covid360.activities

import android.os.Bundle
import covid360rf.covid360.R
import covid360rf.covid360.databinding.ActivityAbout360rfBinding

class About360rfActivity : BaseActivity() {

    private lateinit var binding : ActivityAbout360rfBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAbout360rfBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpActionBar(this,findViewById(R.id.toolbar),getString(R.string.nav_about_360rf))

    }
}