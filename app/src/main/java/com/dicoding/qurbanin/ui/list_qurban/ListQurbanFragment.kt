package com.dicoding.qurbanin.ui.list_qurban

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.qurbanin.R
import com.dicoding.qurbanin.core.utils.datastore.SettingPreferences
import com.dicoding.qurbanin.core.utils.datastore.datastore
import com.dicoding.qurbanin.data.Result
import com.dicoding.qurbanin.data.model.ListEventQurbanResponseItem
import com.dicoding.qurbanin.databinding.FragmentListQurbanBinding
import com.dicoding.qurbanin.ui.ViewModelFactory
import com.dicoding.qurbanin.ui.adapter.ListQurbanAdapter
import com.dicoding.qurbanin.ui.detail.QurbanViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ListQurbanFragment : Fragment() {

    private var _binding: FragmentListQurbanBinding? = null
    private val binding get() = _binding!!

    private lateinit var settingPreferences: SettingPreferences

    private val factory : ViewModelFactory = ViewModelFactory.getInstance()
    private val viewModel : QurbanViewModel by viewModels {
        factory
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentListQurbanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager = LinearLayoutManager(requireActivity())
        binding.rvHistory.layoutManager = layoutManager

        lifecycleScope.launch {
            settingPreferences = SettingPreferences.getInstance(requireContext().datastore)
            val userId = settingPreferences.getUserId().first()

            viewModel.getEventByUserId(userId).observe(viewLifecycleOwner){
                when(it) {
                    is Result.Success -> {
                        setQurbanList(it.data)
                    }
                    is Result.Loading -> {
                    }
                    else -> {}
                }
            }
        }
    }

    private fun setQurbanList(data: List<ListEventQurbanResponseItem>) {
        val adapter = ListQurbanAdapter(data)
        binding.rvHistory.adapter = adapter

        adapter.setOnItemClickCallback(object : ListQurbanAdapter.OnItemClickCallback{
            override fun onItemClicked(data: ListEventQurbanResponseItem) {
                val mBundle = Bundle()
                mBundle.putString(EXTRA_ID_EVENT,data.IDEvent)
                findNavController().navigate(R.id.action_homeContainerFragment_to_StatusQurbanFragment,mBundle)
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val EXTRA_ID_EVENT = "extra_id_event"
    }
}