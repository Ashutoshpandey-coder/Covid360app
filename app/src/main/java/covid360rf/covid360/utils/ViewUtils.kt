package covid360rf.covid360.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
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