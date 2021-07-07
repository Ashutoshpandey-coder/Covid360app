package covid360rf.covid360.model

import android.os.Parcel
import android.os.Parcelable

data class  Doctors (
    val name : String = "",
    val description : String = "",
    val image : String= "",
    val phone : String = ""
        ) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun describeContents()= 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        dest.writeString(name)
        dest.writeString(description)
        dest.writeString(image)
        dest.writeString(phone)
    }

    companion object CREATOR : Parcelable.Creator<Doctors> {
        override fun createFromParcel(parcel: Parcel): Doctors {
            return Doctors(parcel)
        }

        override fun newArray(size: Int): Array<Doctors?> {
            return arrayOfNulls(size)
        }
    }
}