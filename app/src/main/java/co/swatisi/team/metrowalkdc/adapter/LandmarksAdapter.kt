package co.swatisi.team.metrowalkdc.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import co.swatisi.team.metrowalkdc.R
import co.swatisi.team.metrowalkdc.model.Landmark
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.row_landmarks.view.*

class LandmarksAdapter(private val context: Context, private val landmarkList: List<Landmark>) :
        RecyclerView.Adapter<LandmarksAdapter.ViewHolder>() {
    private lateinit var itemClickListener: OnItemClickListener

    fun setOnItemClickListener(itemClickListener: OnItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent?.context).inflate(R.layout.row_landmarks, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val landmark = landmarkList[position]

        // Set the name, rating, distance and image dynamically
        holder?.itemView?.landmark_name?.text = landmark.name
        holder?.itemView?.landmark_rating?.rating = landmark.rating.toFloat()
        holder?.itemView?.landmark_distance?.text = String.format(
                context.getString(R.string.landmark_miles_text, landmark.distance))
        // Load only if the image URL exists
        if (landmark.imageURL.isNotEmpty()) {
            Picasso.with(context).load(landmark.imageURL).into(holder?.itemView?.landmark_image)
        }
        holder?.itemView?.landmark_image?.contentDescription = landmark.id
    }

    override fun getItemCount() = landmarkList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        init {
            itemView.landmarkHolder.setOnClickListener(this)
        }

        override fun onClick(view: View) = itemClickListener.onItemClick(itemView, adapterPosition)
    }

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int)
    }
}