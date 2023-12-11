package com.dicoding.qurbanin.ui.detail

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.dicoding.qurbanin.R
import com.dicoding.qurbanin.core.utils.utility.DialogUtils
import com.dicoding.qurbanin.data.Result
import com.dicoding.qurbanin.data.model.EventQurbanResponse
import com.dicoding.qurbanin.data.model.StockDataResponse
import com.dicoding.qurbanin.databinding.FragmentDetailBinding
import com.dicoding.qurbanin.ui.ViewModelFactory
import com.dicoding.qurbanin.ui.adapter.EventStockAdapter
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class DetailFragment : Fragment() {
    private lateinit var bind : FragmentDetailBinding
    private val factory : ViewModelFactory = ViewModelFactory.getInstance()
    private val viewModel : QurbanViewModel by viewModels {
        factory
    }
    private val stockAdapter : EventStockAdapter by lazy { EventStockAdapter() }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        bind = FragmentDetailBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservable()
        initAction()
    }
    private fun initObservable() {
        viewModel.getEventById("Event01").observe(viewLifecycleOwner) {
            Log.d("Asd", "initObservable: $it")
            when(it) {
                is Result.Success -> {
                    bind.container.visibility = View.VISIBLE
                    initUI(it.data)
                }
                is Result.Loading -> {
                    bind.container.visibility = View.GONE
                }
                else -> {}
            }
        }
    }

    private fun initAction() {
        bind.ivBack.setOnClickListener { findNavController().previousBackStackEntry }

        var dataSelect : StockDataResponse? = null
        stockAdapter.setOnClickItemListener(object : EventStockAdapter.setListenerClick{
            override fun setOnClickItemListener(data: StockDataResponse?) {
                dataSelect = data
            }
        })
        bind.btnRegisterEvent.setOnClickListener{
            if (dataSelect!=null) {
                DialogUtils.showDialogRegisterEvent(requireActivity(), dataSelect!!.keyItem.split("-")[0], getString(R.string.textDescDialogRegister, dataSelect!!.keyItem, dataSelect!!.dataItem?.Price)) {
                    Toast.makeText(requireContext(), dataSelect!!.keyItem, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun initUI(data: EventQurbanResponse) {
        bind.apply {
            val stock : (String) -> List<StockDataResponse>? = { param ->
                data.stock?.filter { it.dataItem?.Type.toString() == param }
            }
            val sold : (String) -> List<StockDataResponse> = { stockData->
                stock(stockData)?.filter { (it.dataItem?.StatusStock == "sold" )} ?: listOf()
            }
            tvCountPiecesSohibul.text = getString(R.string.textStock, sold("single").size.toString(), stock("single")?.size.toString() )
            tvCountGroupSohibul.text = getString(R.string.textStock, sold("group").size.toString(), stock("group")?.size.toString() )
            tvHeadlineOrganizer.text = data.data?.Name
            tvAddress.text = data.data?.Lokasi
            tvExecution.text = data.data?.Pelaksanaan
            tvDeliver.text = data.data?.Pengambilan
            ivEventOrganizer.clipToOutline = true
            rvStock.apply {
                layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
                adapter = stockAdapter
                setHasFixedSize(true)
                (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = true
            }
            stockAdapter.setData(data.stock?: listOf())
        }
    }


}
