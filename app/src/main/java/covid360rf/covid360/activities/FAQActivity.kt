package covid360rf.covid360.activities

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import covid360rf.covid360.R
import covid360rf.covid360.adapters.FAQListAdapter
import covid360rf.covid360.databinding.ActivityFaqactivityBinding
import covid360rf.covid360.model.FAQ

class FAQActivity : BaseActivity() {
    private lateinit var binding : ActivityFaqactivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFaqactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpActionBar(this, findViewById(R.id.toolbar), getString(R.string.faq))
        setUpListInUI()
    }

    private fun setUpListInUI(){
        val mFaqList : ArrayList<FAQ> = ArrayList()

        mFaqList.add(FAQ(getString(R.string.question_one), getString(R.string.answer_one)))
        mFaqList.add(FAQ(getString(R.string.question_two), getString(R.string.answer_two)))
        mFaqList.add(FAQ(getString(R.string.question_three), getString(R.string.answer_three)))
        mFaqList.add(FAQ(getString(R.string.question_four), getString(R.string.answer_four)))
        mFaqList.add(FAQ(getString(R.string.question_five), getString(R.string.answer_five)))
        mFaqList.add(FAQ(getString(R.string.question_six), getString(R.string.answer_six)))
        mFaqList.add(FAQ(getString(R.string.question_seven), getString(R.string.answer_seven)))
        mFaqList.add(FAQ(getString(R.string.question_eight), getString(R.string.answer_eight)))
        mFaqList.add(FAQ(getString(R.string.question_nine), getString(R.string.answer_nine)))
        mFaqList.add(FAQ(getString(R.string.question_ten), getString(R.string.answer_ten)))
        mFaqList.add(FAQ(getString(R.string.question_eleven), getString(R.string.answer_eleven)))
        mFaqList.add(FAQ(getString(R.string.question_twelve), getString(R.string.answer_twelve)))
        mFaqList.add(FAQ(getString(R.string.question_thirteen), getString(R.string.answer_thirteen)))
        mFaqList.add(FAQ(getString(R.string.question_fourteen), getString(R.string.answer_fourteen)))
        mFaqList.add(FAQ(getString(R.string.question_fifteen), getString(R.string.answer_fifteen)))

        binding.rvFaqList.layoutManager = LinearLayoutManager(this)
        binding.rvFaqList.setHasFixedSize(true)
        val adapter = FAQListAdapter(this,mFaqList)
        binding.rvFaqList.adapter = adapter
    }
}