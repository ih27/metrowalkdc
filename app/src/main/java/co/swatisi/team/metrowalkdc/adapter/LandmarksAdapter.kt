package co.swatisi.team.metrowalkdc.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import co.swatisi.team.metrowalkdc.R
import co.swatisi.team.metrowalkdc.model.LandmarkData
import kotlinx.android.synthetic.main.row_landmarks.view.*

class LandmarksAdapter : RecyclerView.Adapter<LandmarksAdapter.ViewHolder>() {

    lateinit var itemClickListener: OnItemClickListener

    fun setOnItemClickListener(itemClickListener: OnItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent?.context).inflate(R.layout.row_landmarks, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val landmark = LandmarkData.landmarkList()[position]
        holder?.itemView?.landmark_name?.text = landmark.name
        holder?.itemView?.landmark_rating?.text = landmark.rating.toString()
        holder?.itemView?.landmark_distance?.text = landmark.distance.toString() + " miles away"
    }

    override fun getItemCount() = LandmarkData.landmarkList().size

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