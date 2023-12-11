package com.dicoding.qurbanin.core.utils.utility

import android.content.Context
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import com.dicoding.qurbanin.R
import com.dicoding.qurbanin.databinding.ItemDialogInfoBinding
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
}