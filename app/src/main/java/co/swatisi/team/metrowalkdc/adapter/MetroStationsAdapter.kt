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
    private val stationList = StationData.stationList().toMutableList() as ArrayList<Station>
    private var filteredStationList = stationList

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
                val filterResults = FilterResults()
                val charString = charSequence.toString()
                filteredStationList = when(charString.isEmpty()) {
                    true ->
                        stationList /* the list stays the same */
                    else -> {
                        val filteredList = arrayListOf<Station>()
                        stationList.filter { it.name.toLowerCase().contains(charString) }
                                .forEach { filteredList.add(it) }
                        filteredList /* the list is filtered */
                    }
                }
                // Assign the data to the FilterResults
                filterResults.values = filteredStationList
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence?, filterResults: FilterResults?) {
                filteredStationList = filterResults?.values as ArrayList<Station> /* no need to check cast*/
                Log.d(tag, filteredStationList.toString())
                notifyDataSetChanged()
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int, filteredList: ArrayList<Station>)
    }
}