package co.swatisi.team.metrowalkdc.adapter

import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import co.swatisi.team.metrowalkdc.R
import co.swatisi.team.metrowalkdc.model.Station
import kotlinx.android.synthetic.main.row_stations.view.*

class MetroStationsAdapter(private val stationList: List<Station>) :
        RecyclerView.Adapter<MetroStationsAdapter.ViewHolder>(), Filterable {

    private var filteredStationList = stationList
    private val colorHash = hashMapOf("RD" to Color.RED, "BL" to Color.BLUE,
            "YL" to Color.YELLOW, "GR" to Color.GREEN, "SV" to Color.rgb(192, 192, 192),
            "OR" to Color.rgb(255, 165, 0))

    private lateinit var itemClickListener: OnItemClickListener

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
        holder?.itemView?.station_name?.text = station.name

        // Set the metro line colors
        /* Kotlin hash maps have no way to null-check, so non-null operator is used out of necessity */
        holder?.itemView?.station_line_code_1?.drawable?.colorFilter =
                PorterDuffColorFilter(colorHash[station.lineCode1]!!, PorterDuff.Mode.MULTIPLY)

        if (station.lineCode2.isNotBlank())
            holder?.itemView?.station_line_code_2?.drawable?.colorFilter =
                    PorterDuffColorFilter(colorHash[station.lineCode2]!!, PorterDuff.Mode.MULTIPLY)
        else holder?.itemView?.station_line_code_2?.visibility = View.GONE

        if (station.lineCode3.isNotBlank())
            holder?.itemView?.station_line_code_3?.drawable?.colorFilter =
                    PorterDuffColorFilter(colorHash[station.lineCode3]!!, PorterDuff.Mode.MULTIPLY)
        else holder?.itemView?.station_line_code_3?.visibility = View.GONE
    }

    override fun getItemCount() = filteredStationList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        init {
            itemView.station_holder.setOnClickListener(this)
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
                @Suppress("UNCHECKED_CAST") // The following cast is safe.
                filteredStationList = filterResults?.values as List<Station>
                notifyDataSetChanged()
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int, filteredList: List<Station>)
    }
}