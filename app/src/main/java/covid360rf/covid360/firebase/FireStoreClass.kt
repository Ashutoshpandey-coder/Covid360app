package covid360rf.covid360.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import covid360rf.covid360.activities.AddDoctorsActivity
import covid360rf.covid360.activities.ConsultationActivity
import covid360rf.covid360.activities.ProfileActivity
import covid360rf.covid360.activities.RegisterActivity
import covid360rf.covid360.model.Doctors
import covid360rf.covid360.model.User
import covid360rf.covid360.utils.Constants

class FireStoreClass {

    private val mFireStore  = FirebaseFirestore.getInstance()

    fun registerUser(activity : RegisterActivity, userInfo : User){
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegisteredSuccess()
            }.addOnFailureListener {
                    exception ->
                Log.e(activity.javaClass.simpleName,"Failed to write document",exception)
            }

    }
    fun getCurrentUserId() : String {
        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserId = ""
        if (currentUser != null){
            currentUserId = currentUser.uid
        }
        return currentUserId
    }
    fun updateUserProfileData(activity: ProfileActivity, hashMap: HashMap<String,Any>){
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .update(hashMap)
            .addOnSuccessListener {
                Log.e("Update Profile", "Profile updated successfully")
                Toast.makeText(activity, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                activity.profileUpdateSuccess()

            }.addOnFailureListener {
                    exception->
                activity.hideProgressDialog()
                Log.e("Update Profile","Failed to update Profile",exception)
                Toast.makeText(activity, "Error in updating Profile", Toast.LENGTH_SHORT).show()
            }

    }
    fun loadUserData(activity : Activity){
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener {
                    document->
                val loggedInUser = document.toObject(User::class.java)
                if (loggedInUser != null){
                    when(activity){
                        is ProfileActivity -> activity.setUserDataInUI(loggedInUser)
                        is ConsultationActivity -> activity.loadUserData(loggedInUser)
                    }

                }
            }.addOnFailureListener {
                    exception->
                when(activity){
                    is ProfileActivity -> activity.hideProgressDialog()
                    is ConsultationActivity -> activity.hideProgressDialog()
                }
                Log.e("SignInUser","Fail sign in FireStore",exception)

            }

    }
    fun gettingListOfDoctors(activity : ConsultationActivity){
        mFireStore.collection(Constants.DOCTORS)
            .get()
            .addOnSuccessListener {
                document->
                Log.i(activity.javaClass.simpleName,document.documents.toString())

                val doctorList : ArrayList<Doctors> = ArrayList()

                for (i in document.documents){
                    val doctor = i.toObject(Doctors::class.java)
                    if (doctor != null){
                        doctorList.add(doctor)
                    }
                }
                activity.gettingListOfDoctors(doctorList)
            }
            .addOnFailureListener {
                e->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,e.message.toString())
            }
    }
    fun addDoctor(activity : AddDoctorsActivity,mDoctorsDetails : Doctors){
        mFireStore.collection(Constants.DOCTORS)
            .document()
            .set(mDoctorsDetails, SetOptions.merge())
            .addOnSuccessListener {
                activity.addDoctorSuccess()
            }
            .addOnFailureListener {
                e->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,"Error while adding doctors",e)
            }
    }


}