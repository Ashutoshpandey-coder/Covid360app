package covid360rf.covid360.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import covid360rf.covid360.R
import covid360rf.covid360.databinding.EachRowBinding
import covid360rf.covid360.model.CovidInfo

class AllStatesCovidAdapter (private val context : Context,
                             private val list : ArrayList<CovidInfo>) : RecyclerView.Adapter<AllStatesCovidAdapter.ViewHolder>() {
    class ViewHolder(val binding : EachRowBinding) : RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view  = LayoutInflater.from(context).inflate(R.layout.each_row,parent,false)
        return ViewHolder(EachRowBinding.bind(view))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = list[position]

        holder.binding.tvState.text = model.state
        holder.binding.tvActive.text = model.active
        holder.binding.tvConfirmed.text = model.confirmed
        holder.binding.tvDeaths.text = model.deaths
        holder.binding.tvRecovered.text = model.recovered

        holder.binding.llNumberCasesPerState.setBackgroundColor(Color.parseColor("#FFFFFF"))
    }

    override fun getItemCount(): Int {
        return list.size
    }

}