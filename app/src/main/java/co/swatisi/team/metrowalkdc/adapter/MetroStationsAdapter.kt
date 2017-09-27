package co.swatisi.team.metrowalkdc.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import co.swatisi.team.metrowalkdc.R
import co.swatisi.team.metrowalkdc.model.StationData
import kotlinx.android.synthetic.main.row_stations.view.*

class MetroStationsAdapter : RecyclerView.Adapter<MetroStationsAdapter.ViewHolder>() {

    lateinit var itemClickListener: OnItemClickListener

    fun setOnItemClickListener(itemClickListener: OnItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent?.context).inflate(R.layout.row_stations, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val station = StationData.stationList()[position]
        holder?.itemView?.stationName?.text = station.name
    }

    override fun getItemCount() = StationData.stationList().size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        init {
            itemView.stationHolder.setOnClickListener(this)
        }

        override fun onClick(view: View) = itemClickListener.onItemClick(itemView, adapterPosition)
    }

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int)
    }
}