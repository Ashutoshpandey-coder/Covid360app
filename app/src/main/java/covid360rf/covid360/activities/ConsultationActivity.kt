package covid360rf.covid360.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import covid360rf.covid360.R
import covid360rf.covid360.adapters.DoctorsListItemAdapter
import covid360rf.covid360.databinding.ActivityConsultationBinding
import covid360rf.covid360.firebase.FireStoreClass
import covid360rf.covid360.model.Doctors
import covid360rf.covid360.model.User
import covid360rf.covid360.utils.Constants

class ConsultationActivity : BaseActivity() {
    private lateinit var binding : ActivityConsultationBinding
    private lateinit var mUserDetails : User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConsultationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showProgressDialog(getString(R.string.please_wait))
        FireStoreClass().gettingListOfDoctors(this)


        getUserDetails()



        setUpActionBar()

    }
    private fun getUserDetails(){
        showProgressDialog(getString(R.string.please_wait))
        FireStoreClass().loadUserData(this)
    }
    fun loadUserData(user : User){
        mUserDetails = user
        hideProgressDialog()
    }
    private fun setUpActionBar(){
        setSupportActionBar(binding.toolbarProfileActivity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_white_back_icon_24)
        supportActionBar?.title = "Consultation"

        binding.toolbarProfileActivity.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    fun gettingListOfDoctors( doctorsList : ArrayList<Doctors>){

        if (doctorsList.size > 0){
            binding.rvDoctorsList.layoutManager = LinearLayoutManager(this)
            binding.rvDoctorsList.setHasFixedSize(true)

            val adapter = DoctorsListItemAdapter(this,doctorsList)
            binding.rvDoctorsList.adapter = adapter
            hideProgressDialog()
        }else{
            hideProgressDialog()
        }



    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
       Handler(Looper.myLooper()!!).postDelayed({
           if (mUserDetails.phone == Constants.TEAM_MEMBER_NUMBER) {
               menuInflater.inflate(R.menu.add_doctor_menu, menu)
           }
       },1000)
        return false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_add_doctor->{
                val intent = Intent(this,AddDoctorsActivity::class.java)
                @Suppress("DEPRECATION")
                startActivityForResult(intent,Constants.ADD_DOCTORS_ACTIVITY_RESULT)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if (resultCode == Activity.RESULT_OK && requestCode == Constants.ADD_DOCTORS_ACTIVITY_RESULT){
        showProgressDialog(getString(R.string.please_wait))
        FireStoreClass().gettingListOfDoctors(this)
    }
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)
    }
}