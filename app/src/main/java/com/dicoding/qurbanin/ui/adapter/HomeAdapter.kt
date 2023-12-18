package com.dicoding.qurbanin.ui.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.qurbanin.R
import com.dicoding.qurbanin.data.model.EventQurbanResponse
import com.dicoding.qurbanin.data.model.StockDataResponse
import com.dicoding.qurbanin.databinding.ItemNearestOrganizerBinding
import com.dicoding.qurbanin.databinding.ItemSearchBinding

class HomeAdapter(var onClick: ((EventQurbanResponse) -> Unit)? = null) : ListAdapter<EventQurbanResponse, HomeAdapter.SearchItemViewHolder>(DIFF_CALLBACK) {
    inner class SearchItemViewHolder(val bind: ItemNearestOrganizerBinding) : RecyclerView.ViewHolder(bind.root)

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<EventQurbanResponse> =
            object : DiffUtil.ItemCallback<EventQurbanResponse>() {
                override fun areItemsTheSame(oldItem: EventQurbanResponse, newItem: EventQurbanResponse): Boolean {
                    return oldItem.idEvent == newItem.idEvent
                }

                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(oldItem: EventQurbanResponse, newItem: EventQurbanResponse): Boolean {
                    return oldItem == newItem
                }
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchItemViewHolder {
        val view = ItemNearestOrganizerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind.apply {
            ivNearestOrganizer.clipToOutline = true
            tvNearestOrganizerName.text = item.data?.Name
            tvNearestOrganizerLocation.text = item.data?.Lokasi
            val stock : (String) -> List<StockDataResponse>? = { param ->
                item.stock?.filter { it.dataItem?.Type.toString() == param }
            }
            val sold : (String) -> List<StockDataResponse> = {stockData->
                stock(stockData)?.filter { (it.dataItem?.StatusStock == "sold" )} ?: listOf()
            }
            tvTotalGoat.text = holder.itemView.context.getString(R.string.textStock, sold("single").size.toString(), stock("single")?.size.toString() )
            tvTotalCow.text = holder.itemView.context.getString(R.string.textStock, sold("group").size.toString(), stock("group")?.size.toString() )
        }
        holder.itemView.setOnClickListener {
            onClick?.let { click -> click(item) }
        }
    }
}