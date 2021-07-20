package covid360rf.covid360.activities

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import covid360rf.covid360.R
import covid360rf.covid360.adapters.VaccinationCentresAdapter
import covid360rf.covid360.databinding.ActivityVaccinationCentresBinding
import covid360rf.covid360.model.VaccinationCentre
import covid360rf.covid360.utils.toast
import org.json.JSONException
import java.util.*
import kotlin.collections.ArrayList

class VaccinationCentresActivity : BaseActivity() {
    private lateinit var searchButton: Button
    private lateinit var pinCodeEdt: EditText
    private lateinit var centersRV: RecyclerView
    private lateinit var searchMessage : TextView
    private lateinit var vaccinationCentresAdapter: VaccinationCentresAdapter
    private lateinit var vaccinationCentreList: List<VaccinationCentre>
    private lateinit var loadingPB: ProgressBar
    private lateinit var binding: ActivityVaccinationCentresBinding
    private var centerAddress: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVaccinationCentresBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar : Toolbar = findViewById(R.id.toolbar_vaccination_centers_activity)

        setUpActionBar(this,toolbar,"Vaccination Centers")

        searchButton = findViewById(R.id.idBtnSearch)
        pinCodeEdt = findViewById(R.id.idEdtPinCode)
        centersRV = findViewById(R.id.centersRV)
        loadingPB = findViewById(R.id.idPBLoading)
        searchMessage = findViewById(R.id.tv_search_message)
        vaccinationCentreList = ArrayList()

        searchButton.setOnClickListener {
            searchMessage.visibility = View.GONE
            binding.shimmerViewContainer.visibility = View.VISIBLE
            binding.shimmerViewContainer.startShimmerAnimation()
            searchVaccinationCentres(pinCodeEdt.text.toString())
        }
    }

    private fun searchVaccinationCentres(pinCode: String) {
        if (pinCode.length != 6) {
            toast("Please enter valid pin code")
        } else {
            (vaccinationCentreList as ArrayList<VaccinationCentre>).clear()

            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(
                this,
                { _, year, monthOfYear, dayOfMonth ->
                    loadingPB.visibility = View.VISIBLE

                    val dateStr = """$dayOfMonth - ${monthOfYear + 1} - $year"""
                    getAppointments(pinCode, dateStr)
                },
                year,
                month,
                day
            )

            dpd.show()
        }
    }

    private fun getAppointments(pinCode: String, date: String) {
        val url =
            "https://cdn-api.co-vin.in/api/v2/appointment/sessions/public/calendarByPin?pincode=$pinCode&date=$date"
        val queue = Volley.newRequestQueue(this)

        val request =
            JsonObjectRequest(Request.Method.GET, url, null, { response ->
                loadingPB.visibility = View.GONE

                try {
                    val centerArray = response.getJSONArray("centers")

                    if (centerArray.length() == 0) {
                        Toast.makeText(this, "Oops! No center Found at this pincode.Please try again after sometime or type different pincode.", Toast.LENGTH_LONG).show()
                    }
                    for (i in 0 until centerArray.length()) {

                        val centerObj = centerArray.getJSONObject(i)

                        val centerName: String = centerObj.getString("name")
                        centerAddress = centerObj.getString("address")
                        val centerFromTime: String = centerObj.getString("from")
                        val centerToTime: String = centerObj.getString("to")
                        val fee_type: String = centerObj.getString("fee_type")

                        val sessionObj = centerObj.getJSONArray("sessions").getJSONObject(0)
                        val ageLimit: Int = sessionObj.getInt("min_age_limit")
                        val vaccineName: String = sessionObj.getString("vaccine")
                        val availableCapacity: Int = sessionObj.getInt("available_capacity")


                        val center = VaccinationCentre(
                            centerName,
                            centerAddress!!,
                            centerFromTime,
                            centerToTime,
                            fee_type,
                            ageLimit,
                            vaccineName,
                            availableCapacity
                        )
                        vaccinationCentreList = vaccinationCentreList + center
                    }

                    if (vaccinationCentreList.isNotEmpty()) {

                        searchMessage.visibility = View.GONE
                        binding.shimmerViewContainer.visibility = View.GONE
                        binding.shimmerViewContainer.stopShimmerAnimation()

                        vaccinationCentresAdapter = VaccinationCentresAdapter(
                            this@VaccinationCentresActivity,
                            vaccinationCentreList
                        )
                        centersRV.layoutManager = LinearLayoutManager(this)
                        centersRV.adapter = vaccinationCentresAdapter
                        vaccinationCentresAdapter.notifyDataSetChanged()
                    }else{
                        searchMessage.visibility = View.VISIBLE
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
                {
                    toast("Fail to get response")
                })

        queue.add(request)
    }


    fun onClickOpenAddressButton(address : String) {

        // COMPLETED (6) Use Uri.Builder with the appropriate scheme and query to form the Uri for the address
        val builder = Uri.Builder()
        builder.scheme("geo")
            .path("0,0")
            .appendQueryParameter("q", address)
        val addressUri = builder.build()

        // COMPLETED (7) Replace the Toast with a call to showMap, passing in the Uri from the previous step
        showMap(addressUri)
    }

    // COMPLETED (1) Create a method called showMap with a Uri as the single parameter
    @SuppressLint("QueryPermissionsNeeded")
    private fun showMap(geoLocation: Uri) {
        // COMPLETED (2) Create an Intent with action type, Intent.ACTION_VIEW
        /*
         * Again, we create an Intent with the action, ACTION_VIEW because we want to VIEW the
         * contents of this Uri.
         */
        val intent = Intent(Intent.ACTION_VIEW)

        // COMPLETED (3) Set the data of the Intent to the Uri passed into this method
        /*
         * Using setData to set the Uri of this Intent has the exact same affect as passing it in
         * the Intent's constructor. This is simply an alternate way of doing this.
         */
        intent.data = geoLocation




        // COMPLETED (4) Verify that this Intent can be launched and then call startActivity
        startActivity(intent)
    }
}
