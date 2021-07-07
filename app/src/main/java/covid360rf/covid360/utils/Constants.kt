package covid360rf.covid360.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap

object Constants{

    const val USERS : String = "users"
    const val DOCTORS : String = "doctors"

    const val NAME : String = "name"
    const val PHONE : String = "phone"
   const val IMAGE : String = "image"
   const val ADDRESS : String = "address"
    const val DOCTOR_IMAGE : String = "DOCTOR_IMAGE"
    const val ADD_DOCTORS_ACTIVITY_RESULT  = 4
    const val READ_STORAGE_PERMISSION_CODE = 1
    const val PICK_IMAGE_REQUEST_CODE = 2

    // staff mobile numbers
    const val TEAM_MEMBER_NUMBER : String = "+917838653842"

    fun showImageChooser(activity : Activity){
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activity.startActivityForResult(intent,PICK_IMAGE_REQUEST_CODE)
    }
    //This function just return the extension of image
    fun getFileExtension(activity : Activity, uri : Uri?) : String?{
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(activity.contentResolver.getType(uri!!))
    }

}