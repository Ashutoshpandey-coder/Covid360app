package covid360rf.covid360.activities

import android.app.AlertDialog
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.widget.EditText
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.core.widget.NestedScrollView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import covid360rf.covid360.R
import covid360rf.covid360.adapters.AllStatesCovidAdapter
import covid360rf.covid360.model.CovidInfo
import covid360rf.covid360.utils.formatLargeNumber
import covid360rf.covid360.utils.start
import covid360rf.covid360.utils.toast
import org.eazegraph.lib.charts.PieChart
import org.eazegraph.lib.models.PieModel
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener , SwipeRefreshLayout.OnRefreshListener{
    private lateinit var mConfirmedCases : TextView
    private lateinit var mActiveCases : TextView
    private lateinit var mRecoveredCases : TextView
    private lateinit var mDeaths : TextView
    private lateinit var editSearch : EditText
    private lateinit var mPieChart: PieChart
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var mNavigationView: NavigationView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private val stateWiseCovidInfoList = ArrayList<CovidInfo>()
    private lateinit var mSharedPreferences : SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private var adapter: AllStatesCovidAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val linearLayout : CoordinatorLayout = findViewById(R.id.linearLayout)
        val view : NestedScrollView = linearLayout.findViewById(R.id.main_content)

        mConfirmedCases = view.findViewById(R.id.tv_confirmedCases)
        mActiveCases = view.findViewById(R.id.activeCases)
        mRecoveredCases = view.findViewById(R.id.recoveredCases)
        mDeaths = view.findViewById(R.id.deaths)
        mPieChart = view.findViewById(R.id.pieChart)
        mRecyclerView = view.findViewById(R.id.rv_states_data)
        editSearch = view.findViewById(R.id.et_search)
        mNavigationView = findViewById(R.id.nav_view)
        mDrawerLayout = findViewById(R.id.drawer_layout)


        setUpActionBar()

        swipeRefreshLayout  = linearLayout.findViewById(R.id.swipeRefreshLayout)
        swipeRefreshLayout.setOnRefreshListener(this)

        mSharedPreferences = getSharedPreferences("stored previous data", MODE_PRIVATE)!!

        val confirmedCases = mSharedPreferences.getString("confirmed","0")
        val recoveredCases = mSharedPreferences.getString("recovered","0")
        val deathCases = mSharedPreferences.getString("deaths","0")
        val activeCases = mSharedPreferences.getString("active","0")

        mConfirmedCases.text = confirmedCases
        mRecoveredCases.text = recoveredCases
        mDeaths.text = deathCases
        mActiveCases.text = activeCases

        if (!(confirmedCases.isNullOrEmpty() && recoveredCases.isNullOrEmpty() && deathCases.isNullOrEmpty() && activeCases.isNullOrEmpty())){
        mPieChart.addPieSlice(PieModel("Confirmed", confirmedCases?.toFloat()!!, Color.parseColor("#FFA726")))
        mPieChart.addPieSlice(PieModel("Recovered", recoveredCases?.toFloat()!!, Color.parseColor("#66BB6A")))
        mPieChart.addPieSlice(PieModel("Deaths", deathCases?.toFloat()!!, Color.parseColor("#EF5350")))
        mPieChart.addPieSlice(PieModel("Active", activeCases?.toFloat()!!, Color.parseColor("#29B6F6")))
            }

        mPieChart.startAnimation()

        mNavigationView.setNavigationItemSelectedListener(this)

        showProgressDialog(getString(R.string.please_wait))
        fetchDataAllOverIndia()
        fetchDataAsPerState()

        editSearch.addTextChangedListener(object  : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter!!.filter.filter(s)
                adapter!!.notifyDataSetChanged()
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
    }
    private fun setUpActionBar(){
        val toolbar : androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_action_navigation_menu)
        supportActionBar?.title = getString(R.string.app_name)

        toolbar.setNavigationOnClickListener {
            toggleDrawer()
        }
    }

    private fun toggleDrawer(){
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)){
            mDrawerLayout.closeDrawer(GravityCompat.START)
        }else{
            mDrawerLayout.openDrawer(GravityCompat.START)
        }
    }

    override fun onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)){
            mDrawerLayout.closeDrawer(GravityCompat.START)
        }else{
            doubleBackToExit()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_home ->{ }
            R.id.nav_precaution ->{
                toast("Precautions")
            }
            R.id.nav_test_centres ->{
                start(TestsCentresActivity::class.java)
            }
            R.id.nav_vaccination_info ->{
                start(VaccinationCentresActivity::class.java)
            }
            R.id.nav_self_diagnosis ->{
                start(SelfDiagnosisActivity::class.java)
            }
            R.id.nav_consult_a_doctor ->{
                toast("consult a doctor")
            }
            R.id.nav_about_covid360 ->{
                start(AboutAppActivity::class.java)
            }
            R.id.nav_about_360rf ->{
                start(About360rfActivity::class.java)
            }
            R.id.nav_contact_us ->{
                toast("Thanks for contacting")
            }
            R.id.nav_log_out ->{
                alertDialogLogout()
            }
        }
        mDrawerLayout.closeDrawer(GravityCompat.START)

        return true
    }
    private fun fetchDataAllOverIndia() {
        val url = "https://corona.lmao.ninja/v2/countries/india/"
        val request = StringRequest(Request.Method.GET, url, { response ->
            try {
                val jsonObject = JSONObject(response)
                mConfirmedCases.text = jsonObject.getString("cases")
                mActiveCases.text = jsonObject.getString("active")
                mRecoveredCases.text = jsonObject.getString("recovered")
                mDeaths.text = jsonObject.getString("deaths")

                mPieChart.clearAnimation()
                mPieChart.clearChart()
                mPieChart.addPieSlice(PieModel("Confirmed", mRecoveredCases.text.toString().toFloat()-mConfirmedCases.text.toString().toFloat(), Color.parseColor("#FFA726")))
                mPieChart.addPieSlice(PieModel("Recovered", mRecoveredCases.text.toString().toFloat(), Color.parseColor("#66BB6A")))
                mPieChart.addPieSlice(PieModel("Deaths", mDeaths.text.toString().toFloat(), Color.parseColor("#EF5350")))
                mPieChart.addPieSlice(PieModel("Active", mActiveCases.text.toString().toFloat(), Color.parseColor("#29B6F6")))

                mPieChart.startAnimation()
                gettingDataSuccess()

                editor = mSharedPreferences.edit()
                editor.putString("confirmed",mConfirmedCases.text.toString())
                editor.putString("recovered",mRecoveredCases.text.toString())
                editor.putString("deaths",mDeaths.text.toString())
                editor.putString("active",mActiveCases.text.toString())
                editor.apply()

                mConfirmedCases.text = formatLargeNumber((mConfirmedCases.text as String).toLong())
                mActiveCases.text = formatLargeNumber((mActiveCases.text as String).toLong())
                mRecoveredCases.text = formatLargeNumber((mRecoveredCases.text as String).toLong())
                mDeaths.text = formatLargeNumber((mDeaths.text as String).toLong())

            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }) { error -> toast(error.message.toString()) }
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(request)
    }

    private fun gettingDataSuccess() {
        hideProgressDialog()
    }

    private fun fetchDataAsPerState() {
        val url = "https://api.covid19india.org/data.json"
        val request = StringRequest(Request.Method.GET, url, { response ->
            try {
                val `object` = JSONObject(response)
                val stateWise = `object`.getJSONArray("statewise")
                for (i in 1 until stateWise.length()) {
                    val jsonObject = stateWise.getJSONObject(i)
                    val stateName = jsonObject.getString("state")
                    val activeCases = jsonObject.getString("active").toLong().let { formatLargeNumber(it) }
                    val deaths = jsonObject.getString("deaths").toLong().let { formatLargeNumber(it) }
                    val recoveredCases = jsonObject.getString("recovered").toLong().let { formatLargeNumber(it) }
                    val confirmedCases = jsonObject.getString("confirmed").toLong().let { formatLargeNumber(it) }
                    val model = CovidInfo(stateName, activeCases, deaths, confirmedCases, recoveredCases)
                    stateWiseCovidInfoList.add(model)
                }
                adapter = AllStatesCovidAdapter(this,stateWiseCovidInfoList)
                val linearLayoutManager = LinearLayoutManager(this)
                mRecyclerView.layoutManager = linearLayoutManager
                mRecyclerView.setHasFixedSize(true)
                mRecyclerView.isNestedScrollingEnabled = false
                mRecyclerView.adapter = adapter
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }) { error -> toast(error.message.toString())  }
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(request)
    }

    private fun alertDialogLogout() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.app_name))
        builder.setMessage("Are you sure you want to Logout?")
        builder.setIcon(R.drawable.ic_log_out_icon_24)
        builder.setPositiveButton("Yes") { dialogInterface, _ ->
            dialogInterface.dismiss()
            FirebaseAuth.getInstance().signOut()
            start(IntroActivity::class.java)
            finish()
        }
        builder.setNegativeButton("No") { dialogInterface, _ ->
            dialogInterface.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()

    }

    override fun onRefresh() {
        swipeRefreshLayout.isRefreshing = true
        refreshPage()
    }
    private fun refreshPage(){
        showProgressDialog(getString(R.string.please_wait))
        fetchDataAsPerState()
        fetchDataAllOverIndia()
        swipeRefreshLayout.isRefreshing = false
    }
}