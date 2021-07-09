package covid360rf.covid360.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import covid360rf.covid360.R
import covid360rf.covid360.databinding.ActivityProfileBinding
import covid360rf.covid360.firebase.FireStoreClass
import covid360rf.covid360.model.User
import covid360rf.covid360.utils.Constants
import covid360rf.covid360.utils.getFileExtension
import covid360rf.covid360.utils.showImageChooser
import covid360rf.covid360.utils.toast
import java.io.IOException

class ProfileActivity : BaseActivity(), View.OnClickListener {
    private lateinit var binding: ActivityProfileBinding
    private var mSelectedImageUri: Uri? = null
    private var mProfileImageUrl: String = ""
    private lateinit var mUserDetails : User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpActionBar(this, findViewById(R.id.toolbar), getString(R.string.profile))
        showProgressDialog(getString(R.string.please_wait))
        FireStoreClass().loadUserData(this)

        binding.profileImageView.setOnClickListener(this)
        binding.btnUpdate.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.profile_image_view -> {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        showImageChooser(this)
                    } else {
                        ActivityCompat.requestPermissions(
                            this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            Constants.READ_STORAGE_PERMISSION_CODE
                        )
                    }
                }
                R.id.btn_update -> {
                    if (mSelectedImageUri != null) {
                        if (binding.etName.text.toString().isEmpty()) {
                            toast("Please enter a name")
                        } else {
                            uploadUserImage()
                        }
                    } else {
                        if (binding.etName.text.toString().isEmpty()) {
                            toast("Please enter a name")
                        } else {
                            updateUserProfileData()
                        }
                    }
                }
            }
        }
    }

    fun setUserDataInUI(user: User) {
        mUserDetails = user
        hideProgressDialog()

        showProgressDialog(getString(R.string.please_wait))
        binding.etName.setText(user.name)
        binding.etPhoneNumber.setText(user.phone)

        if (user.phone != "") {
            binding.etPhoneNumber.setText(user.phone)
        }
        if (user.address.isNotEmpty()){
            binding.etAddress.setText(user.address)
        }

        Glide
            .with(this)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(binding.profileImageView)

        hideProgressDialog()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showImageChooser(this)
            } else {
                toast("Oops! you just denied the permission for storage. You can allow it from settings.")
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
                        .into(binding.profileImageView)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }
    private fun uploadUserImage(){
        showProgressDialog(getString(R.string.please_wait))

        // Now we can store into the database as a storage by storage reference
        if (mSelectedImageUri != null){

            val mSrf : StorageReference = FirebaseStorage.getInstance().reference.child("USER_IMAGE"
                    + System.currentTimeMillis()
                    + "." +
                    getFileExtension(this,mSelectedImageUri))

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
                    //update the profile photo in document also
                    updateUserProfileData()
                    hideProgressDialog()

                }
            }.addOnFailureListener{
                    exception->
                toast(exception.message.toString())
                hideProgressDialog()
            }
        }
    }

    private fun updateUserProfileData(){
        val hashMap = HashMap<String,Any>()
        var anyChangesMade = false

        val name = binding.etName.text.toString().trim()
        if(name != mUserDetails.name){
            hashMap[Constants.NAME] = name
            anyChangesMade = true
        }
        val mobile = binding.etPhoneNumber.text.toString()
        if (mobile.isNotEmpty() && mobile != mUserDetails.phone){
            hashMap[Constants.PHONE] = mobile
            anyChangesMade = true
        }
        val address = binding.etAddress.text.toString().trim{ it <= ' '}
        if (address.isNotEmpty() && address != mUserDetails.address){
            hashMap[Constants.ADDRESS] = address
            anyChangesMade = true
        }
        if (mProfileImageUrl.isNotEmpty() && mProfileImageUrl != mUserDetails.image){
            hashMap[Constants.IMAGE] = mProfileImageUrl
            anyChangesMade = true
        }

        if (anyChangesMade){
            showProgressDialog(getString(R.string.please_wait))
            FireStoreClass().updateUserProfileData(this@ProfileActivity, hashMap)
        }else{
            toast("Profile is already updated")
            finish()
        }


    }

    fun profileUpdateSuccess() {
        hideProgressDialog()
        finish()
    }
}