package covid360rf.covid360.activities

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import covid360rf.covid360.R
import covid360rf.covid360.adapters.PrecautionsAdapter
import covid360rf.covid360.utils.Constants

class PrecautionsActivity : BaseActivity() {
    private lateinit var precautionsRecyclerView: RecyclerView
    private lateinit var precautionImagesList: ArrayList<String>

    private lateinit var runnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_precautions)

        setUpActionBar(this, findViewById(R.id.toolbar), getString(R.string.nav_precautions))

        precautionsRecyclerView = findViewById(R.id.recycler_view_precautions)
        precautionsRecyclerView.setHasFixedSize(true)
        precautionsRecyclerView.layoutManager = LinearLayoutManager(
            this,LinearLayoutManager.HORIZONTAL, false)

        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(precautionsRecyclerView)

        precautionImagesList = ArrayList()

        precautionImagesList.add("https://media.giphy.com/media/S2weoc58ZPQSVlyupE/giphy.gif")
        precautionImagesList.add("https://media.giphy.com/media/jqHJghF4K6Vog60fCB/giphy.gif")
        precautionImagesList.add("https://media.giphy.com/media/ZazvVSkRzwyponrIA2/giphy.gif")
        precautionImagesList.add("https://media.giphy.com/media/fAV06DMoZf1CVuqIeF/giphy.gif")
        precautionImagesList.add("https://media.giphy.com/media/xfmZ9ugALcuemVQuF2/giphy.gif")

        val precautionsAdapter = PrecautionsAdapter(this, precautionImagesList)
        precautionsRecyclerView.adapter = precautionsAdapter

        precautionsRecyclerView.post {
            var page = 0

            val handler = Handler(Looper.myLooper()!!)
            runnable = Runnable {
                if(page < precautionsAdapter.itemCount){
                    if(page == precautionsAdapter.itemCount - 1){
                        page--
                    }else{
                        page++
                    }
                    precautionsRecyclerView.smoothScrollToPosition(page)
                    handler.postDelayed(runnable , Constants.RECYCLER_ITEM_SCROLL_WAIT_TIME)
                }
            }
            handler.postDelayed(runnable, Constants.RECYCLER_ITEM_SCROLL_WAIT_TIME)
        }
    }
}