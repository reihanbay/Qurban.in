package com.dicoding.qurbanin.ui.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.qurbanin.R
import com.dicoding.qurbanin.data.model.ListEventQurbanResponseItem
import com.dicoding.qurbanin.databinding.ItemRegisteredQurbanBinding

class ListQurbanAdapter(val listQurban: List<ListEventQurbanResponseItem> = mutableListOf()) : RecyclerView.Adapter<ListQurbanAdapter.ViewHolder>() {

    class ViewHolder(var binding: ItemRegisteredQurbanBinding) :
        RecyclerView.ViewHolder(binding.root)

    private lateinit var onItemClickCallback: OnItemClickCallback

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemRegisteredQurbanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listQurban.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val listQurbanItem = listQurban[position]

        with(holder.binding) {
            tvHeadlineOrganizer.text = listQurbanItem.Pelaksanaan
            tvStatusContainer.text = listQurbanItem.StatusEvent

            tvStatusContainer.backgroundTintList = when (listQurbanItem.StatusEvent) {
                "Belum Lunas" -> ColorStateList.valueOf(holder.itemView.context.getColor(R.color.yellow))
                "Lunas" -> ColorStateList.valueOf(holder.itemView.context.getColor(R.color.green_40))
                "Sedang Berlangsung" -> ColorStateList.valueOf(holder.itemView.context.getColor(R.color.blue))
                "Tersembelih" -> ColorStateList.valueOf(holder.itemView.context.getColor(R.color.purple))
                else -> null
            }

            tvAddress.text = listQurbanItem.Location
            tvNameStock.text = listQurbanItem.IDStock
            tvExecutionDate.text = listQurbanItem.Pelaksanaan
            tvDeliverTime.text = listQurbanItem.Pengambilan
        }

        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(listQurbanItem)
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: ListEventQurbanResponseItem)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }
}