package com.dicoding.qurbanin.ui.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.qurbanin.R
import com.dicoding.qurbanin.core.utils.utility.StringHelper
import com.dicoding.qurbanin.data.model.EventRegisteredResponse
import com.dicoding.qurbanin.databinding.ItemRegisteredQurbanBinding

class ListQurbanAdapter (private val listQurban: ArrayList<EventRegisteredResponse> = arrayListOf()) : RecyclerView.Adapter<ListQurbanAdapter.ViewHolder>() {

    fun setData(list: List<EventRegisteredResponse>) {
        listQurban.clear()
        listQurban.addAll(list)
        notifyDataSetChanged()
    }
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
        val listQurban = listQurban[position]
        val listQurbanItem = listQurban.model

        with(holder.binding) {
            tvHeadlineOrganizer.text = listQurbanItem?.pelaksanaan
            val status = listQurbanItem?.statusStock
            val (color, text) = StringHelper.styleTextViewStatus(holder.itemView.context, status.toString())
            tvStatusContainer.backgroundTintList = color
            tvStatusContainer.text = text

            tvAddress.text = listQurbanItem?.location
            tvNameStock.text = listQurbanItem?.idstock
            tvExecutionDate.text = listQurbanItem?.pelaksanaan
            tvDeliverTime.text = listQurbanItem?.pengambilan
        }

        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(listQurban.idRegistered)
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: String)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

}