package covid360rf.covid360.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import covid360rf.covid360.R
import covid360rf.covid360.databinding.ActivityAddDoctorsBinding
import covid360rf.covid360.firebase.FireStoreClass
import covid360rf.covid360.model.Doctors
import covid360rf.covid360.utils.Constants
import java.io.IOException

class AddDoctorsActivity : BaseActivity() {
    private lateinit var binding : ActivityAddDoctorsBinding
    private var mSelectedImageUri: Uri? = null
    private var mProfileImageUrl: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddDoctorsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpActionBar(this,binding.toolbarAddDoctorActivity,getString(R.string.action_add_doctor))

        binding.ivDoctorImageView.setOnClickListener{
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                Constants.showImageChooser(this)
            } else {
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.READ_STORAGE_PERMISSION_CODE
                )
            }
        }
        binding.btnAddDoctor.setOnClickListener {
            if (validateData()){
                if (mSelectedImageUri != null){
                    uploadDoctorImage()
                }else{
                    addDoctorToFirebase()
                }
            }
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Constants.showImageChooser(this)
            } else {
                Toast.makeText(
                    this,
                    "Oops! you just denied the permission for storage. You can allow it from settings.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == Constants.PICK_IMAGE_REQUEST_CODE) {
            if (data!!.data != null) {
                mSelectedImageUri = data.data
                try {
                    Glide
                        .with(this)
                        .load(mSelectedImageUri)
                        .placeholder(R.drawable.ic_user_place_holder)
                        .centerCrop()
                        .into(binding.ivDoctorImageView)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun validateData() : Boolean{
        return when{
            TextUtils.isEmpty(binding.etName.text.toString().trim()) ->{
                showErrorSnackBar(getString(R.string.doctor_name_empty_error))
                false
            }
            TextUtils.isEmpty(binding.etPhoneNumber.text.toString().trim()) || binding.etPhoneNumber.text.toString().trim().length != 10->{
                showErrorSnackBar(getString(R.string.please_enter_valid_mobile_number))
                false
            }
            else->{
                true
            }
        }
    }

    private fun uploadDoctorImage(){
        showProgressDialog(getString(R.string.please_wait))

        // Now we can store into the database as a storage by storage reference
        if (mSelectedImageUri != null){

            val mSrf : StorageReference = FirebaseStorage.getInstance().reference.child(Constants.DOCTOR_IMAGE
                    + System.currentTimeMillis()
                    + "." +
                    Constants.getFileExtension(this,mSelectedImageUri))

            mSrf.putFile(mSelectedImageUri!!).addOnSuccessListener {
                    taskSnapShot->
                //this task give us download url
                Log.i("Firebase image url" ,
                    taskSnapShot.metadata!!.reference!!.downloadUrl.toString())
                //we need download url because we have to store in user data also
                taskSnapShot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                        uri->
                    Log.i("Downloadable image url",uri.toString())
                    mProfileImageUrl = uri.toString()
                    addDoctorToFirebase()
                    hideProgressDialog()

                }
            }.addOnFailureListener{
                    exception->
                Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()
                hideProgressDialog()
            }
        }
    }

    private fun addDoctorToFirebase(){
        val newDoctor = Doctors(
            binding.etName.text.toString().trim(),
            binding.etDescription.text.toString().trim(),
            mProfileImageUrl,
            "+91" +binding.etPhoneNumber.text.toString().trim()
        )
        showProgressDialog(getString(R.string.please_wait))
        FireStoreClass().addDoctor(this,newDoctor)

    }

    fun addDoctorSuccess(){
        hideProgressDialog()
        Toast.makeText(this, getString(R.string.doctor_added_success_message), Toast.LENGTH_SHORT).show()
        setResult(Activity.RESULT_OK)
        finish()
    }

}