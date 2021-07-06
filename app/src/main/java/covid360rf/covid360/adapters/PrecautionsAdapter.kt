package covid360rf.covid360.adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import covid360rf.covid360.R
import covid360rf.covid360.utils.Constants
import covid360rf.covid360.utils.startAction

class PrecautionsAdapter(private val context: Context,
    private var precautionsImagesList: ArrayList<String>): RecyclerView.Adapter<PrecautionsAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private var precautionImage : ImageView = itemView.findViewById(R.id.precaution_image)

        fun bindItems(precautionUrl : String){
            Glide.with(itemView.context).load(precautionUrl).into(precautionImage)

            precautionImage.setOnClickListener {
                itemView.context.startAction(Intent.ACTION_VIEW, Uri.parse(Constants.WHO_PRECAUTIONS_URL))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.layout_precaution_item,parent,false)

        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(precautionsImagesList[position % precautionsImagesList.size])
    }

    override fun getItemCount(): Int = Int.MAX_VALUE
}