package com.dicoding.qurbanin.ui.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.dicoding.qurbanin.R
import com.dicoding.qurbanin.data.model.EventQurbanResponse
import com.dicoding.qurbanin.data.model.StockDataResponse
import com.dicoding.qurbanin.databinding.FragmentStatusQurbanBinding
import com.google.android.material.button.MaterialButton

class StatusQurbanFragment : Fragment() {
    private lateinit var bind : FragmentStatusQurbanBinding

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

        initUI()
    }

    private fun initUI() {
        bind.apply {
            ivEventOrganizer.clipToOutline = true
//            tvHeadlineOrganizer.text = data.data?.Name
//            tvAddress.text = data.data?.Lokasi
//            tvExecution.text = data.data?.Pelaksanaan
//            tvDeliver.text = data.data?.Pengambilan
//            val stock : (String) -> List<StockDataResponse>? = { param ->
//                data.stock?.filter { it.dataItem?.Type.toString() == param }
//            }
//            val sold : (String) -> List<StockDataResponse> = { stockData->
//                stock(stockData)?.filter { (it.dataItem?.StatusStock == "sold" )} ?: listOf()
//            }
//            tvCountPiecesSohibul.text = getString(R.string.textStock, sold("single").size.toString(), stock("single")?.size.toString() )
//            tvCountGroupSohibul.text = getString(R.string.textStock, sold("group").size.toString(), stock("group")?.size.toString() )
//
//            rvStock.apply {
//                layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
//                adapter = stockAdapter
//                setHasFixedSize(true)
//                (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = true
//            }
//            stockAdapter.setData(data.stock?: listOf())
        }
    }

}