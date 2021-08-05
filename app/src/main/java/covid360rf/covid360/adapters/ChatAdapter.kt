package covid360rf.covid360.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import covid360rf.covid360.R
import covid360rf.covid360.firebase.FireStoreClass
import covid360rf.covid360.model.MessageModel
import covid360rf.covid360.utils.Constants
import covid360rf.covid360.utils.toast

class ChatAdapter(private val context: Context,
                  private val list : ArrayList<MessageModel>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class ReceiverViewHolder(val view : View ) : RecyclerView.ViewHolder(view){
        val receiverMessage: TextView = view.findViewById(R.id.tv_receiver_text)
        //val receiverTime = view.findViewById<TextView>(R.id.tv_receiver_time)
    }

    class SenderViewHolder(val view: View) : RecyclerView.ViewHolder(view){
        val senderMessage: TextView = view.findViewById(R.id.tv_sender_text)
        //val senderTime = view.findViewById<TextView>(R.id.tv_sender_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == Constants.SENDER_VIEW_TYPE){
            val view = LayoutInflater.from(context).inflate(R.layout.layout_bubble_sender, parent ,false)
            SenderViewHolder(view)
        }else{
            val view = LayoutInflater.from(context).inflate(R.layout.layout_bubble_receiver, parent ,false)
            context.toast("Receiver message")
            ReceiverViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder.javaClass == SenderViewHolder::class.java){
            if (holder is SenderViewHolder){
                holder.senderMessage.text = model.message
                //holder.senderTime.text = model.time.toString()
            }
        }else if (holder.javaClass == ReceiverViewHolder::class.java){
            if (holder is ReceiverViewHolder){
                holder.receiverMessage.text = model.message
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        val model = list[position]

        return if (model.id == FireStoreClass().getCurrentUserId()){
            Constants.SENDER_VIEW_TYPE
        }else{
            Constants.RECEIVER_VIEW_TYPE
        }
    }
}