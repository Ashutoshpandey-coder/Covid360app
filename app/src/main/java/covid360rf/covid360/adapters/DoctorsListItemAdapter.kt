package covid360rf.covid360.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import covid360rf.covid360.R
import covid360rf.covid360.databinding.ItemDoctorBinding
import covid360rf.covid360.model.Doctors

class DoctorsListItemAdapter(private val context: Context,
private val list : ArrayList<Doctors>) : RecyclerView.Adapter<DoctorsListItemAdapter.ViewHolder>() {
    class ViewHolder(val binding : ItemDoctorBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_doctor,parent ,false)
        return ViewHolder(ItemDoctorBinding.bind(view))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val model = list[position]

        Glide.with(context).load(model.image).centerCrop().placeholder(R.drawable.ic_user_place_holder).into(holder.binding.ivDoctorImage)
        holder.binding.tvName.text = model.name
        holder.binding.tvDescription.text = model.description

    }

    override fun getItemCount(): Int {
       return list.size
    }

}