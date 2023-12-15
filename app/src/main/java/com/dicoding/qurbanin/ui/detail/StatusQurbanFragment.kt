package com.dicoding.qurbanin.ui.detail

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.dicoding.qurbanin.R
import com.dicoding.qurbanin.data.Result
import com.dicoding.qurbanin.data.model.EventQurbanResponse
import com.dicoding.qurbanin.data.model.ListEventQurbanResponseItem
import com.dicoding.qurbanin.data.model.StockDataResponse
import com.dicoding.qurbanin.databinding.FragmentStatusQurbanBinding
import com.dicoding.qurbanin.databinding.ItemStockSoldByBinding
import com.dicoding.qurbanin.ui.ViewModelFactory

class StatusQurbanFragment : Fragment() {
    private lateinit var bind : FragmentStatusQurbanBinding
    private val factory by lazy { ViewModelFactory.getInstance(requireContext().applicationContext) }
    private val viewModel : QurbanViewModel by viewModels {
        factory
    }
//    private val args : DetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        bind = FragmentStatusQurbanBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind.ivEventOrganizer.clipToOutline = true

        viewModel.getEvenRegisteredById("regis0101").observe(viewLifecycleOwner) {dataEvent->
            when(dataEvent) {
                is Result.Success -> {

                    viewModel.getStockById(dataEvent.data.IDEvent, dataEvent.data.IDStock).observe(viewLifecycleOwner) {dataStock->
                        when(dataStock) {
                            is Result.Success -> {
                                initUI(dataEvent.data, dataStock.data)
                            }
                            is Result.Loading -> {

                            }
                            else -> {}
                        }
                    }
                }
                is Result.Loading -> {
                }
                else -> {}
            }
        }
    }

    private fun initUI(data: ListEventQurbanResponseItem, stock: StockDataResponse) {
        bind.apply {
            tvHeadlineOrganizer.text = data.Name
            tvAddress.text = data.Location
            tvExecution.text = data.Pelaksanaan
            tvDeliver.text = data.Pengambilan

            layoutStock.apply {
                tvNameStock.text = stock.keyItem
                tvNotePricing.text = getString(R.string.text_note_pricing, stock.dataItem?.Price)
                btnIconDropdown.setOnClickListener {
                    llParticipation.isVisible = !btnIconDropdown.isActivated
                    btnIconDropdown.isActivated = !btnIconDropdown.isActivated
                }
                llParticipation.removeAllViews()
                if (stock.dataItem?.Type == "group") {
                    for (i in stock.SoldBy?.indices!!) {
                        val view = ItemStockSoldByBinding.inflate(LayoutInflater.from(layoutStock.root.context), llParticipation, true)
                        view.nameSold.text = stock.SoldBy[i].name
                        llParticipation.removeView(view.root)
                        llParticipation.addView(view.root)
                    }
                    for (i in 1 .. (7-stock.SoldBy.size)) {
                        val view = ItemStockSoldByBinding.inflate(LayoutInflater.from(layoutStock.root.context), llParticipation, true)
                        view.nameSold.text = "Kosong"
                        llParticipation.removeView(view.root)
                        llParticipation.addView(view.root)
                    }
                } else {
                    val view = ItemStockSoldByBinding.inflate(LayoutInflater.from(layoutStock.root.context), llParticipation, true)
                    view.nameSold.text = if (stock.SoldBy?.isEmpty() == true) "Kosong" else stock.SoldBy?.get(0)?.name ?:"Kosong"
                    llParticipation.removeAllViews()
                    llParticipation.addView(view.root)
                }
            }
        }
    }

}