package covid360rf.covid360.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import covid360rf.covid360.R
import covid360rf.covid360.activities.ChatboxActivity
import covid360rf.covid360.databinding.LayoutDoctorBinding
import covid360rf.covid360.model.Doctors
import covid360rf.covid360.utils.Constants

class DoctorsListItemAdapter(private val context: Context,
                             private val list : ArrayList<Doctors>) : RecyclerView.Adapter<DoctorsListItemAdapter.ViewHolder>() {
    class ViewHolder(val binding : LayoutDoctorBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_doctor,parent ,false)
        return ViewHolder(LayoutDoctorBinding.bind(view))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val model = list[position]

        Glide.with(context).load(model.image).centerCrop().placeholder(R.drawable.ic_user_place_holder).into(holder.binding.ivDoctorImage)
        holder.binding.tvName.text = model.name
        holder.binding.tvDescription.text = model.description

        holder.itemView.setOnClickListener {
            val intent = Intent(context,ChatboxActivity::class.java)
            intent.putExtra(Constants.DOCTOR_DETAILS,model)
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int = list.size
}