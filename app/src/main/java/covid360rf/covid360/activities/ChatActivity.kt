package covid360rf.covid360.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import covid360rf.covid360.R
import covid360rf.covid360.adapters.ChatAdapter
import covid360rf.covid360.databinding.ActivityChatBinding
import covid360rf.covid360.model.Doctors
import covid360rf.covid360.model.MessageModel
import covid360rf.covid360.utils.Constants
import java.util.Date
import kotlin.collections.ArrayList

class ChatActivity : BaseActivity() {
    private lateinit var binding : ActivityChatBinding
    private lateinit var mDoctorDetails : Doctors
    private var mReceiverId : String = "null"
    private var mSenderId : String = "null"
    private lateinit var database : FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance()
        mSenderId = FirebaseAuth.getInstance().currentUser!!.uid

        setSupportActionBar(binding.toolbarChatBoxActivity)

        binding.ibBackIcon.setOnClickListener {
            onBackPressed()
        }

        if(intent.hasExtra(Constants.DOCTOR_DETAILS)){
            mDoctorDetails = intent.getParcelableExtra(Constants.DOCTOR_DETAILS)!!
        }

        //TODO remove the hard coded user id with the doctors id
        mReceiverId = "aRJu0JzHANUPZ3uRhtAp6RAN1IR2"
        Glide.with(this).load(mDoctorDetails.image)
            .centerCrop().placeholder(R.drawable.ic_user_place_holder).into(binding.ivDoctorImage)
        binding.tvNameDoctor.text = mDoctorDetails.name
        binding.tvDesignation.text = mDoctorDetails.description

        val senderRoom = mSenderId + mReceiverId
        val receiverRoom = mReceiverId + mSenderId

        database.reference.child(Constants.CHATS)
            .child(senderRoom).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val messageModelList = ArrayList<MessageModel>()

                messageModelList.clear()
                for (snapShot1 : DataSnapshot in snapshot.children){

                    val model : MessageModel = snapShot1.getValue(MessageModel::class.java)!!
                    messageModelList.add(model)
                }
                showChatsOnUI(messageModelList)
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

        binding.etEnterMessage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(count > 0){
                    binding.ivSend.alpha = 1f
                    binding.ivSend.isEnabled = true
                }else{
                    binding.ivSend.alpha = 0.25f
                    binding.ivSend.isEnabled = false
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

        binding.ivSend.setOnClickListener {
            val messageString = binding.etEnterMessage.text.toString().trim()
            val message = MessageModel(mSenderId, messageString)
            message.timeStamp = Date().time
            binding.etEnterMessage.setText("")


            binding.rvChats.adapter?.let { it1 -> binding.rvChats.smoothScrollToPosition(it1.itemCount) }

            database.reference.child(Constants.CHATS)
                .child(senderRoom).push().setValue(message).addOnSuccessListener {
                database.reference.child(Constants.CHATS)
                    .child(receiverRoom).push().setValue(message)
            }
        }


    }
    private fun showChatsOnUI(list : ArrayList<MessageModel>){
        if (list.size > 0 ){
            binding.rvChats.layoutManager  = LinearLayoutManager(this)
            binding.rvChats.setHasFixedSize(true)

            val adapter = ChatAdapter(this, list)
            binding.rvChats.adapter = adapter

            binding.rvChats.smoothScrollToPosition(adapter.itemCount)
        }
    }

}