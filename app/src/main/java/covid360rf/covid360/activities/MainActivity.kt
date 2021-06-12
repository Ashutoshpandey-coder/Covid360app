package covid360rf.covid360.activities

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.navigation.NavigationView
import covid360rf.covid360.R
import covid360rf.covid360.adapters.AllStatesCovidAdapter
import covid360rf.covid360.model.CovidInfo
import org.eazegraph.lib.charts.PieChart
import org.eazegraph.lib.models.PieModel
import org.json.JSONException
import org.json.JSONObject
import java.util.ArrayList

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener{
    private lateinit var mConfirmedCases : TextView
    private lateinit var mActiveCases : TextView
    private lateinit var mRecoveredCases : TextView
    private lateinit var mDeaths : TextView
    private lateinit var mPieChart: PieChart
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var mNavigationView: NavigationView
    private val stateWiseCovidInfoList = ArrayList<CovidInfo>()
    private lateinit var mSharedPreferences : SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private var adapter: AllStatesCovidAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        @SuppressLint("InflateParams")
//        val view : View = layoutInflater.inflate(R.layout.main_content,null)

        val linearLayout : LinearLayout = findViewById(R.id.linearLayout)
        val view : LinearLayout = linearLayout.findViewById(R.id.main_content)

        mConfirmedCases = view.findViewById(R.id.tv_confirmedCases)
        mActiveCases = view.findViewById(R.id.activeCases)
        mRecoveredCases = view.findViewById(R.id.recoveredCases)
        mDeaths = view.findViewById(R.id.deaths)
        mPieChart = view.findViewById(R.id.pieChart)
        mRecyclerView = view.findViewById(R.id.rv_states_data)
        mNavigationView = findViewById(R.id.nav_view)
        mDrawerLayout = findViewById(R.id.drawer_layout)


        setUpActionBar()

        mSharedPreferences = getSharedPreferences("stored previous data", MODE_PRIVATE)!!

        mNavigationView.setNavigationItemSelectedListener(this)

        showProgressDialog(getString(R.string.please_wait))
        fetchDataAllOverIndia()
        fetchDataAsPerState()
    }
    private fun setUpActionBar(){
        val toolbar : androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_main_activity)
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
            R.id.nav_home ->{
                Toast.makeText(this, "Home is pressed", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_precaution ->{
                Toast.makeText(this, "Precautions", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_test_centres ->{
                Toast.makeText(this, "Test Centres", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_vaccination_info ->{
                Toast.makeText(this, "vaccination", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_self_diagnosis ->{
                Toast.makeText(this, "Diagnosis", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_consult_a_doctor ->{
                Toast.makeText(this, "consult a doctor", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_about_covid360 ->{
                Toast.makeText(this, "About covid360", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_about_360rf ->{
                Toast.makeText(this, "About 360rf", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_contact_us ->{
                Toast.makeText(this, "Thanks for contacting", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_log_out ->{
                Toast.makeText(this, "please wait", Toast.LENGTH_SHORT).show()
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
                mPieChart.addPieSlice(PieModel("Confirmed", mConfirmedCases.text.toString().toFloat(), Color.parseColor("#FFA726")))
                mPieChart.addPieSlice(PieModel("Recovered", mRecoveredCases.text.toString().toFloat(), Color.parseColor("#66BB6A")))
                mPieChart.addPieSlice(PieModel("Deaths", mDeaths.text.toString().toFloat(), Color.parseColor("#EF5350")))
                mPieChart.addPieSlice(PieModel("Active", mActiveCases.text.toString().toFloat(), Color.parseColor("#29B6F6")))

                mPieChart.startAnimation()
                gettingDataSuccess()

              /*  editor = mSharedPreferences.edit()
                editor.putString("confirmed",binding.confirmedCases.text.toString())
                editor.putString("recovered",binding.recoveredCases.text.toString())
                editor.putString("deaths",binding.deaths.text.toString())
                editor.putString("active",binding.activeCases.text.toString())
                editor.apply()*/

            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }) { error -> Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show() }
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(request)
    }

    private fun gettingDataSuccess() {
        hideProgressDialog()
        Toast.makeText(this, "Hello I am in.", Toast.LENGTH_SHORT).show()
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
                    val activeCases = jsonObject.getString("active")
                    val deaths = jsonObject.getString("deaths")
                    val recoveredCases = jsonObject.getString("recovered")
                    val confirmedCases = jsonObject.getString("confirmed")
                    val model = CovidInfo(stateName, activeCases, deaths, confirmedCases, recoveredCases)
                    stateWiseCovidInfoList.add(model)
                }
                adapter = AllStatesCovidAdapter(this,stateWiseCovidInfoList)
                val linearLayoutManager = LinearLayoutManager(this)
                mRecyclerView.layoutManager = linearLayoutManager
                mRecyclerView.isNestedScrollingEnabled = false
                mRecyclerView.adapter = adapter
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }) { error -> Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show() }
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(request)
    }
}