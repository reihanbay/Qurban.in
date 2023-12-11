package com.dicoding.qurbanin.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.qurbanin.R
import com.dicoding.qurbanin.data.model.EventQurbanResponse
import com.dicoding.qurbanin.data.model.StockDataResponse
import com.dicoding.qurbanin.databinding.ItemStockQurbanBinding
import com.dicoding.qurbanin.databinding.ItemStockSoldByBinding


class EventStockAdapter(val list: MutableList<StockDataResponse> = mutableListOf()) : RecyclerView.Adapter<EventStockAdapter.StockViewHolder>() {
    inner class StockViewHolder(val bind: ItemStockQurbanBinding) : RecyclerView.ViewHolder(bind.root)
//    inner class DataCallback(private val old : List<StockDataResponse>, private val new: List<StockDataResponse>) : DiffUtil.Callback() {

//    }
    var listener : setListenerClick? = null

    fun setOnClickItemListener(listenerClick: setListenerClick) {
        listener = listenerClick
    }

    var selectedItemPosition = -1
    var lastSelectedItemPosition = -1
    fun setData(data: List<StockDataResponse>) {
        list.clear()
        list.addAll(data)
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): EventStockAdapter.StockViewHolder {
        val view = ItemStockQurbanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StockViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventStockAdapter.StockViewHolder, position: Int) {
        val item = list[position]


        holder.bind.apply {
            tvNameStock.text = item.keyItem
            tvNotePricing.text = holder.itemView.context.getString(R.string.text_note_pricing, item.dataItem?.Price)
            btnIconDropdown.setOnClickListener {
                llParticipation.isVisible = !btnIconDropdown.isActivated
                btnIconDropdown.isActivated = !btnIconDropdown.isActivated
            }
            if (item.dataItem?.Type == "group") {
                for (i in item.SoldBy?.indices!!) {
                    val view = ItemStockSoldByBinding.inflate(LayoutInflater.from(holder.itemView.context), llParticipation, true)
                    view.nameSold.text = item.SoldBy.get(i).name ?:"Kosong"
                    llParticipation.removeAllViews()
                    llParticipation.addView(view.root)
                }
            } else {
                val view = ItemStockSoldByBinding.inflate(LayoutInflater.from(holder.itemView.context), llParticipation, true)
                view.nameSold.text = item.SoldBy?.get(0)?.name ?:"Kosong"
                llParticipation.removeAllViews()
                llParticipation.addView(view.root)
            }


            //Backgrond Color
            fun colorBg(selected: Boolean) {
                if (selected) clContainer.setBackgroundColor(holder.itemView.context.getColor(R.color.green_100))
                else clContainer.setBackgroundColor(holder.itemView.context.getColor(R.color.white))
            }
            colorBg(position == selectedItemPosition)
            clContainer.setOnClickListener{
                if (item.dataItem?.StatusStock != "sold") {
                    selectedItemPosition = position
                    listener?.setOnClickItemListener(item)
                }
                lastSelectedItemPosition = if (lastSelectedItemPosition == -1) {
                    selectedItemPosition
                } else {
                    notifyItemChanged(lastSelectedItemPosition)
                    selectedItemPosition
                }
                notifyItemChanged(selectedItemPosition)
            }
        }
    }

    override fun getItemCount(): Int = list.size

    interface setListenerClick {
        fun setOnClickItemListener(data: StockDataResponse?)
    }
}