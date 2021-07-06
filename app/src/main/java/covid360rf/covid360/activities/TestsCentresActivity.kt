package covid360rf.covid360.activities

import android.os.Bundle
import covid360rf.covid360.R
import covid360rf.covid360.databinding.ActivityTestsCentresBinding

class TestsCentresActivity : BaseActivity() {

    private lateinit var binding : ActivityTestsCentresBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestsCentresBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpActionBar(this, findViewById(R.id.toolbar), getString(R.string.nav_test_centres))
    }
}