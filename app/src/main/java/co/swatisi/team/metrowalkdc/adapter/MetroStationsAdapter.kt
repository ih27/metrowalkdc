package co.swatisi.team.metrowalkdc.adapter

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import co.swatisi.team.metrowalkdc.R
import co.swatisi.team.metrowalkdc.model.Station
import co.swatisi.team.metrowalkdc.model.StationData
import kotlinx.android.synthetic.main.row_stations.view.*
import java.util.ArrayList

class MetroStationsAdapter : RecyclerView.Adapter<MetroStationsAdapter.ViewHolder>(), Filterable {
    private val tag = "MetroStationsAdapter"
    lateinit var itemClickListener: OnItemClickListener
    private var stationList = StationData.stationList()
    private var filteredStationList = stationList.clone() as ArrayList<Station>

    fun setOnItemClickListener(itemClickListener: OnItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent?.context)
                .inflate(R.layout.row_stations, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val station = filteredStationList[position]
        holder?.itemView?.stationName?.text = station.name
    }

    override fun getItemCount() = filteredStationList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        init {
            itemView.stationHolder.setOnClickListener(this)
        }

        override fun onClick(view: View) = itemClickListener.onItemClick(itemView, adapterPosition,
                filteredStationList)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence?): FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    filteredStationList = stationList
                } else {
                    val filteredList = arrayListOf<Station>()
                    for (station in stationList) {
                        if (station.name.toLowerCase().contains(charString)) {
                            filteredList.add(station)
                        }
                    }
                    filteredStationList = filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = filteredStationList
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence?, filterResults: FilterResults?) {
                filteredStationList = filterResults?.values as ArrayList<Station>
                Log.d(tag, filteredStationList.toString())
                notifyDataSetChanged()
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int, filteredList: ArrayList<Station>)
    }
}