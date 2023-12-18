package com.dicoding.qurbanin.core.utils.utility

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import com.dicoding.qurbanin.R
import com.dicoding.qurbanin.data.model.EventRegisteredResponseItem
import com.dicoding.qurbanin.data.model.StockDataResponse
import com.dicoding.qurbanin.databinding.ItemDialogBottomSheetPaymentBinding
import com.dicoding.qurbanin.databinding.ItemDialogInfoBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder

object DialogUtils {
    fun showDialogRegisterEvent(context: Context, title: String, desc: String, action : () -> Unit) {
        val bind = ItemDialogInfoBinding.inflate(LayoutInflater.from(context))
        val build = MaterialAlertDialogBuilder(context)
            .setView(bind.root)
            .setCancelable(true)

        val dialog = build.show()
        bind.tvTitle.text = title
        bind.tvDesc.text = desc
        bind.btnRegister.setOnClickListener{
            action.invoke()
            dialog.dismiss()
        }
    }

    fun showPaymentBottomSheet(context: Context, data: EventRegisteredResponseItem, stock: StockDataResponse, action: (BottomSheetDialog) -> Unit) {
        val dialog = BottomSheetDialog(context)
        val inflater = LayoutInflater.from(context)
        val binding = ItemDialogBottomSheetPaymentBinding.inflate(inflater)
        val view = binding.root
        dialog.setContentView(view)
        dialog.setCancelable(true)

        binding.apply {
            val rekening = data.rekening?.split("-")
            Log.d("TAG", "showPaymentBottomSheet: $rekening")
            tvCard.text = context.getString(R.string.cardNumber, rekening?.get(0) ?: "")
            tvItemName.text = context.getString(R.string.qurban_item, data.idstock)
            tvItemPrice.text = stock.dataItem?.Price
            tvItemPriceTotal.text = stock.dataItem?.Price
            tvNumberCard.text = rekening?.get(1) ?: context.getString(R.string.havent_number_card)
            btnConfirm.setOnClickListener {
                action.invoke(dialog)
                dialog.dismiss()
            }
        }

        dialog.show()
    }
}