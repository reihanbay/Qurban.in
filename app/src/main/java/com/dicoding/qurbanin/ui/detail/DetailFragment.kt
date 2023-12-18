package com.dicoding.qurbanin.ui.detail

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.dicoding.qurbanin.R
import com.dicoding.qurbanin.core.utils.utility.DialogUtils
import com.dicoding.qurbanin.data.Result
import com.dicoding.qurbanin.data.model.EventQurbanResponse
import com.dicoding.qurbanin.data.model.EventRegisteredResponse
import com.dicoding.qurbanin.data.model.EventRegisteredResponseItem
import com.dicoding.qurbanin.data.model.StockDataResponse
import com.dicoding.qurbanin.databinding.FragmentDetailBinding
import com.dicoding.qurbanin.ui.ViewModelFactory
import com.dicoding.qurbanin.ui.adapter.EventStockAdapter
import com.google.firebase.auth.FirebaseAuth

class DetailFragment : Fragment() {
    private lateinit var bind : FragmentDetailBinding
    private val factory by lazy { ViewModelFactory.getInstance(requireContext().applicationContext) }
    private val viewModel : QurbanViewModel by viewModels {
        factory
    }
    private lateinit var dataEvent: EventQurbanResponse
    private val args : DetailFragmentArgs by navArgs()
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
        viewModel.getEventById(args.idEvent).observe(viewLifecycleOwner) {
            when(it) {
                is Result.Success -> {
                    bind.container.visibility = View.VISIBLE
                    dataEvent = it.data
                    initUI()
                }
                is Result.Loading -> {
                    bind.container.visibility = View.GONE
                }
                else -> {}
            }
        }

        viewModel.successRegistered.observe(viewLifecycleOwner) {
            when(it) {
                is Result.Success -> {
                    findNavController().navigate(DetailFragmentDirections.actionDetailFragmentToHomeContainerFragment())
                }
                is Result.Loading -> {

                }
                is Result.Error -> {
                    Toast.makeText(requireContext(), it.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun initAction() {
        bind.ivBack.setOnClickListener { findNavController().popBackStack() }

        var dataSelect : StockDataResponse? = null
        stockAdapter.setOnClickItemListener(object : EventStockAdapter.SetListenerClick{
            override fun setOnClickItemListener(data: StockDataResponse?) {
                dataSelect = data
            }
        })
        val auth = FirebaseAuth.getInstance().currentUser

        bind.btnRegisterEvent.setOnClickListener{
            if (dataSelect!=null) {
                DialogUtils.showDialogRegisterEvent(requireActivity(), dataSelect!!.keyItem.split("-")[0], getString(R.string.textDescDialogRegister, dataSelect!!.keyItem, dataSelect!!.dataItem?.Price)) {
                    val data = EventRegisteredResponseItem(dataSelect!!.keyItem,"unpaid",dataEvent.data!!.Pelaksanaan,dataEvent.idEvent,dataEvent.data!!.Name,dataEvent.data!!.Pengambilan, auth!!.uid,
                        dataEvent.data!!.Lokasi,dataEvent.data!!.Name,dataEvent.data!!.Rekening)
                    viewModel.registerEvent(data)
                }
            }
        }
    }
    private fun initUI() {
        bind.apply {
            val stock : (String) -> List<StockDataResponse>? = { param ->
                dataEvent.stock?.filter { it.dataItem?.Type.toString() == param }
            }
            val sold : (String) -> List<StockDataResponse> = { stockData->
                stock(stockData)?.filter { (it.dataItem?.StatusStock == "sold" )} ?: listOf()
            }
            tvCountPiecesSohibul.text = getString(R.string.textStock, sold("single").size.toString(), stock("single")?.size.toString() )
            tvCountGroupSohibul.text = getString(R.string.textStock, sold("group").size.toString(), stock("group")?.size.toString() )
            tvHeadlineOrganizer.text = dataEvent.data?.Name
            tvAddress.text = dataEvent.data?.Lokasi
            tvExecution.text = dataEvent.data?.Pelaksanaan
            tvDeliver.text = dataEvent.data?.Pengambilan
            ivEventOrganizer.clipToOutline = true
            rvStock.apply {
                layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
                adapter = stockAdapter
                setHasFixedSize(true)
                (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = true
            }
            stockAdapter.setData(dataEvent.stock?: listOf())
        }
    }


}
