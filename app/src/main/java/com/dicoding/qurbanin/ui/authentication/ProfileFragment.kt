package com.dicoding.qurbanin.ui.authentication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.qurbanin.R
import com.dicoding.qurbanin.core.utils.datastore.SettingPreferences
import com.dicoding.qurbanin.core.utils.datastore.datastore
import com.dicoding.qurbanin.data.Result
import com.dicoding.qurbanin.data.model.ListEventQurbanResponseItem
import com.dicoding.qurbanin.databinding.FragmentProfileBinding
import com.dicoding.qurbanin.ui.ViewModelFactory
import com.dicoding.qurbanin.ui.adapter.ListQurbanAdapter
import com.dicoding.qurbanin.ui.detail.QurbanViewModel
import com.dicoding.qurbanin.ui.list_qurban.ListQurbanFragment
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
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
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager = LinearLayoutManager(requireActivity())
        binding.rvRegisteredQurban.layoutManager = layoutManager

        lifecycleScope.launch {
            settingPreferences = SettingPreferences.getInstance(requireContext().datastore)
            val userId = settingPreferences.getUserId().first()
            val userName = settingPreferences.getUsername().first()
            val userEmail = settingPreferences.getUserEmail().first()

            binding.tvProfileName.text = "$userName"
            binding.tvEmail.text = "$userEmail"

            binding.ivLogoutButton.setOnClickListener {
                logOutAccount()
            }

            val status = "Sedang Berlangsung"
            viewModel.getEventByUserIdAndStatus(userId,status).observe(viewLifecycleOwner){
                when(it) {
                    is Result.Success -> {
                        setRegisteredQurban(it.data)
                    }
                    is Result.Loading -> {
                    }
                    else -> {}
                }
            }
        }
    }

    private fun setRegisteredQurban(data: List<ListEventQurbanResponseItem>){
        val adapter = ListQurbanAdapter(data)
        binding.rvRegisteredQurban.adapter = adapter

        adapter.setOnItemClickCallback(object : ListQurbanAdapter.OnItemClickCallback{
            override fun onItemClicked(data: ListEventQurbanResponseItem) {
                val mBundle = Bundle()
                mBundle.putString(ListQurbanFragment.EXTRA_ID_EVENT,data.IDEvent)
                findNavController().navigate(R.id.action_homeContainerFragment_to_StatusQurbanFragment,mBundle)
            }
        })
    }

    private fun logOutAccount(){
        lifecycleScope.launch {
            settingPreferences.setLoginSession(false)
            findNavController().navigate(R.id.action_homeContainerFragment_to_loginFragment)
        }
    }
}