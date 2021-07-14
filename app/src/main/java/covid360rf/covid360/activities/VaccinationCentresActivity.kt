package covid360rf.covid360.activities

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import covid360rf.covid360.R
import covid360rf.covid360.adapters.VaccinationCentresAdapter
import covid360rf.covid360.model.VaccinationCentre
import covid360rf.covid360.utils.toast
import org.json.JSONException
import java.util.Calendar
import kotlin.collections.ArrayList

class VaccinationCentresActivity : AppCompatActivity() {
    private lateinit var searchButton: Button
    private lateinit var pinCodeEdt: EditText
    private lateinit var centersRV: RecyclerView
    private lateinit var vaccinationCentresAdapter: VaccinationCentresAdapter
    private lateinit var vaccinationCentreList: List<VaccinationCentre>
    private lateinit var loadingPB: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vaccination_centres)

        searchButton = findViewById(R.id.idBtnSearch)
        pinCodeEdt = findViewById(R.id.idEdtPinCode)
        centersRV = findViewById(R.id.centersRV)
        loadingPB = findViewById(R.id.idPBLoading)
        vaccinationCentreList = ArrayList()

        searchButton.setOnClickListener {
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
                        Toast.makeText(this, "No Center Found", Toast.LENGTH_SHORT).show()
                    }
                    for (i in 0 until centerArray.length()) {

                        val centerObj = centerArray.getJSONObject(i)

                        val centerName: String = centerObj.getString("name")
                        val centerAddress: String = centerObj.getString("address")
                        val centerFromTime: String = centerObj.getString("from")
                        val centerToTime: String = centerObj.getString("to")
                        val fee_type: String = centerObj.getString("fee_type")

                        val sessionObj = centerObj.getJSONArray("sessions").getJSONObject(0)
                        val ageLimit: Int = sessionObj.getInt("min_age_limit")
                        val vaccineName: String = sessionObj.getString("vaccine")
                        val availableCapacity: Int = sessionObj.getInt("available_capacity")


                        val center = VaccinationCentre(
                            centerName,
                            centerAddress,
                            centerFromTime,
                            centerToTime,
                            fee_type,
                            ageLimit,
                            vaccineName,
                            availableCapacity
                        )
                        vaccinationCentreList = vaccinationCentreList + center
                    }

                    vaccinationCentresAdapter = VaccinationCentresAdapter(vaccinationCentreList)
                    centersRV.layoutManager = LinearLayoutManager(this)
                    centersRV.adapter = vaccinationCentresAdapter
                    vaccinationCentresAdapter.notifyDataSetChanged()

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
                {
                    toast("Fail to get response")
                })

        queue.add(request)
    }
}
