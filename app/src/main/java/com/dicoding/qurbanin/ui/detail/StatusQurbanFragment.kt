package com.dicoding.qurbanin.ui.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.dicoding.qurbanin.R
import com.dicoding.qurbanin.core.utils.utility.DialogUtils
import com.dicoding.qurbanin.core.utils.utility.StringHelper
import com.dicoding.qurbanin.data.Result
import com.dicoding.qurbanin.data.model.EventRegisteredResponse
import com.dicoding.qurbanin.data.model.EventRegisteredResponseItem
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
    private val args : StatusQurbanFragmentArgs by navArgs()
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

        initObserve()
        initAction()
    }

    private fun initAction() {
        bind.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }
    private fun initObserve() {
        viewModel.getEventRegisteredById(args.idRegistered).observe(viewLifecycleOwner) { dataEvent->
            when(dataEvent) {
                is Result.Success -> {
                    viewModel.getStockById(dataEvent.data.idevent, dataEvent.data.idstock).observe(viewLifecycleOwner) {dataStock->
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

    private fun initUI(data: EventRegisteredResponseItem, stock: StockDataResponse) {
        bind.apply {
            tvHeadlineOrganizer.text = data.name
            tvAddress.text = data.location
            tvExecution.text = data.pelaksanaan
            tvDeliver.text = data.pengambilan
            val (color, text) = StringHelper.styleTextViewStatus(requireContext(), data.statusStock)
            tvStatusContainer.backgroundTintList = color
            tvStatusContainer.text = text
            btnPay.isVisible = data.statusStock == "unpaid"
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

            btnPay.setOnClickListener {
                DialogUtils.showPaymentBottomSheet(requireContext(), data, stock) {dialog ->
                    viewModel.postPayment(args.idRegistered, data)
                    viewModel.successPayment.observe(viewLifecycleOwner) {
                        when(it) {
                            is Result.Success -> {
                                dialog.dismiss()
                            }
                            is Result.Error -> {
                                Toast.makeText(requireContext(), it.error, Toast.LENGTH_SHORT).show()
                            }

                            else -> {}
                        }
                    }
                }
            }
        }
    }
}