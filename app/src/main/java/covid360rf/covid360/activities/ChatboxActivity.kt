package covid360rf.covid360.activities

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.type.Date
import covid360rf.covid360.R
import covid360rf.covid360.adapters.ChatAdapter
import covid360rf.covid360.databinding.ActivityChatboxBinding
import covid360rf.covid360.firebase.FireStoreClass
import covid360rf.covid360.model.Doctors
import covid360rf.covid360.model.MessageModel
import covid360rf.covid360.utils.Constants
import java.util.*
import kotlin.collections.ArrayList

class ChatboxActivity : BaseActivity() {
    private lateinit var binding : ActivityChatboxBinding
    private  var mDoctorDetails : Doctors? = null
    private var mReceiverId : String? = null
    private var mSenderId : String? = null
    private var database : FirebaseDatabase? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatboxBinding.inflate(layoutInflater)

        setContentView(binding.root)
        database = FirebaseDatabase.getInstance()

        mSenderId = FirebaseAuth.getInstance().currentUser!!.uid

        setSupportActionBar(binding.toolbarChatBoxActivity)

        binding.ibBackIcon.setOnClickListener {
            onBackPressed()
        }

        if(intent.hasExtra(Constants.DOCTOR_DETAILS)){
            mDoctorDetails = intent.getParcelableExtra(Constants.DOCTOR_DETAILS)
        }

        if (mDoctorDetails != null){
            //TODO remove the hard coded user id with the doctors id
            mReceiverId = "aRJu0JzHANUPZ3uRhtAp6RAN1IR2"
            Glide.with(this).load(mDoctorDetails!!.image).centerCrop().placeholder(R.drawable.ic_user_place_holder).into(binding.ivDoctorImage)
            binding.tvNameDoctor.text = mDoctorDetails!!.name
            binding.tvDesignation.text = mDoctorDetails!!.description
        }



        val senderRoom = mSenderId + mReceiverId
        val receiverRoom = mReceiverId + mSenderId

        database!!.reference.child(Constants.CHATS).child(senderRoom).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val messageModelList = ArrayList<MessageModel>()

                for (snapShot1 : DataSnapshot in snapshot.children){

                    val model : MessageModel = snapShot1.getValue(MessageModel::class.java)!!
                    messageModelList.add(model)
                }
                showInUI(messageModelList)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        binding.ivSend.setOnClickListener {
            val message = binding.etEnterMessage.text.toString().trim()
            val model2 = MessageModel(mSenderId!!,message)
            model2.time = Date().time
            binding.etEnterMessage.setText("")

            database!!.reference.child(Constants.CHATS).child(senderRoom).push().setValue(model2).addOnSuccessListener {
                database!!.reference.child(Constants.CHATS).child(receiverRoom).push().setValue(model2).addOnSuccessListener {

                }
            }
        }


    }
    private fun showInUI(list : ArrayList<MessageModel>){
        if (list.size >0 ){
            binding.rvChats.layoutManager  = LinearLayoutManager(this)
            binding.rvChats.setHasFixedSize(true)

            val adapter = ChatAdapter(this,list)
            binding.rvChats.adapter = adapter
//            adapter.notifyDataSetChanged()
        }
    }

}