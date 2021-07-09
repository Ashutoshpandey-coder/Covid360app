package covid360rf.covid360.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import covid360rf.covid360.utils.Constants.PICK_IMAGE_REQUEST_CODE
import kotlin.math.abs
import kotlin.math.log10

fun Context.toast(message:String, duration:Int = 0){
    Toast.makeText(this, message, duration).show()
}

fun Context.start(destination: Class<*>) {
    startActivity(Intent(this, destination))
}
fun Context.startAction(action: String, data: Uri) {
    startActivity(Intent(action).setData(data))
}

fun formatLargeNumber(number : Long): String{
    var formattedNumberString: String = number.toString()

    when {
        abs(number/Constants.MILLION) > 1 -> {

            if((number/Constants.MILLION).length() < 3){
                (String.format("%.1fM",(number/Constants.MILLION)))
                    .also { formattedNumberString = it }
            }else{
                ((number/Constants.MILLION.toInt()).toString() + "M").also { formattedNumberString = it }
            }
        }
        abs(number/Constants.KILO) > 1 -> {

            if((number/Constants.KILO).length() < 3){
                (String.format("%.1fK",(number/Constants.KILO)))
                    .also { formattedNumberString = it }
            }else{
                ((number/Constants.KILO.toInt()).toString() + "K").also { formattedNumberString = it }
            }
        }
    }

    return formattedNumberString
}

fun Double.length() = when(this) {
    0.0 -> 1
    else -> log10(abs(toDouble())).toInt() + 1
}

fun showImageChooser(activity : Activity){
    val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
    activity.startActivityForResult(intent,PICK_IMAGE_REQUEST_CODE)
}
//This function just return the extension of image
fun getFileExtension(activity : Activity, uri : Uri?) : String?{
    return MimeTypeMap.getSingleton().getMimeTypeFromExtension(activity.contentResolver.getType(uri!!))
}

fun getCurrentUserId(): String {
    val currentUser = FirebaseAuth.getInstance().currentUser
    var currentUserId = ""
    if (currentUser != null) {
        currentUserId = currentUser.uid
    }
    return currentUserId
}