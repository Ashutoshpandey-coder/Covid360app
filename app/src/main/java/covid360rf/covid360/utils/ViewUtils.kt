package covid360rf.covid360.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import kotlin.math.abs

fun Context.toast(message:String, length:Int = 0){
    Toast.makeText(this, message, length).show()
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
        abs(number/1000000) > 1 -> {
            ((number/1000000).toString() + "M").also { formattedNumberString = it }
        }
        abs(number/1000) > 1 -> {
            ((number/1000).toString() + "K").also { formattedNumberString = it }
        }
    }

    return formattedNumberString
}