package covid360rf.covid360.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import androidx.recyclerview.widget.RecyclerView
import covid360rf.covid360.R
import covid360rf.covid360.databinding.LayoutStateCovidInfoBinding
import covid360rf.covid360.model.CovidInfo

class AllStatesCovidAdapter (private val context : Context,
                             private val list : ArrayList<CovidInfo>) : RecyclerView.Adapter<AllStatesCovidAdapter.ViewHolder>() {
    private var mSearchList : ArrayList<CovidInfo> = ArrayList(list)

    class ViewHolder(val binding : LayoutStateCovidInfoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view  = LayoutInflater.from(context).inflate(R.layout.layout_state_covid_info,parent,false)
        return ViewHolder(LayoutStateCovidInfoBinding.bind(view))
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

    override fun getItemCount(): Int = list.size

     @JvmName("getFilter1")
     fun getFilter(): Filter {
        return filter
    }

    var filter: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val filteredList = java.util.ArrayList<CovidInfo>()
            if (constraint.toString().isEmpty()) {
                filteredList.addAll(mSearchList)
            } else {
                for (list in mSearchList) {
                    if (list.state.lowercase().contains(constraint.toString().lowercase())) {
                        filteredList.add(list)
                    }
                }
            }
            val filterResults = FilterResults()
            filterResults.values = filteredList
            return filterResults
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            list.clear()
            list.addAll((results.values as Collection<CovidInfo>))
            notifyDataSetChanged()
        }
    }

}