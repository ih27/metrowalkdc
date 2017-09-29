package co.swatisi.team.metrowalkdc.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.support.v7.graphics.Palette
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import co.swatisi.team.metrowalkdc.R
import co.swatisi.team.metrowalkdc.model.LandmarkData
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.android.synthetic.main.row_landmarks.view.*
import java.security.AccessControlContext


class LandmarksAdapter(private val context: Context) : RecyclerView.Adapter<LandmarksAdapter.ViewHolder>() {

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
        // Load only if the image URL exists
        if (landmark.imageURL.toString().isNotEmpty()) {
            Picasso.with(context).load(landmark.imageURL).into(holder?.itemView?.landmark_image)
        }
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