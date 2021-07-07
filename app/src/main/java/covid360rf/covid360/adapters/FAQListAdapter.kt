package covid360rf.covid360.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import covid360rf.covid360.R
import covid360rf.covid360.databinding.ItemFaqLayoutBinding
import covid360rf.covid360.model.FAQ

class FAQListAdapter (private val context: Context,
private val list : ArrayList<FAQ>) : RecyclerView.Adapter<FAQListAdapter.ViewHolder>() {
    class ViewHolder(val binding : ItemFaqLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_faq_layout,parent,false)
        return ViewHolder(ItemFaqLayoutBinding.bind(view))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = list[position]

        holder.binding.tvAnswer.text = model.answer.trim()
        holder.binding.tvQuestion.text = model.question.trim()

    }

    override fun getItemCount(): Int {
       return list.size
    }
}