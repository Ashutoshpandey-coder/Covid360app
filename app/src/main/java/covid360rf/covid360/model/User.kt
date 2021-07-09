package covid360rf.covid360.model

import android.os.Parcel
import android.os.Parcelable

data class User (
    val id : String = "",
    val name : String = "",
    val phone : String = "",
    val image : String = "",
    val address : String = ""
        ) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        dest.writeString(id)
        dest.writeString(name)
        dest.writeString(phone)
        dest.writeString(image)
        dest.writeString(address)
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}