package com.dicoding.qurbanin.ui.authentication

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.qurbanin.R
import com.dicoding.qurbanin.core.utils.datastore.SettingPreferences
import com.dicoding.qurbanin.core.utils.datastore.datastore
import com.dicoding.qurbanin.data.Result
import com.dicoding.qurbanin.data.model.EventRegisteredResponse
import com.dicoding.qurbanin.databinding.FragmentProfileBinding
import com.dicoding.qurbanin.ui.ViewModelFactory
import com.dicoding.qurbanin.ui.adapter.ListQurbanAdapter
import com.dicoding.qurbanin.ui.home.HomeContainerFragmentDirections
import com.dicoding.qurbanin.ui.list_qurban.ListQurbanFragment
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val factory by lazy { ViewModelFactory.getInstance(requireContext().applicationContext) }
    private val viewModel: AuthenticationViewModel by viewModels { factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager = LinearLayoutManager(requireActivity())
        binding.rvRegisteredQurban.layoutManager = layoutManager

        val profileQurbanViewModel = ViewModelProvider(this,
            ViewModelProvider.NewInstanceFactory())[ProfileQurbanViewModel::class.java]
        viewModel.getDataUserNameLocal().observe(viewLifecycleOwner) {
            binding.tvProfileName.text = it
        }
        viewModel.getDataUserEmail().observe(viewLifecycleOwner) {
            when(it) {
                is Result.Success -> binding.tvEmail.text = it.data
                else -> {}
            }

        }

        binding.ivLogoutButton.setOnClickListener {
            viewModel.logoutUser()
            findNavController().navigate(HomeContainerFragmentDirections.actionHomeContainerFragmentToLoginFragment())
        }

        profileQurbanViewModel.registeredQurban.observe(viewLifecycleOwner) {
            setRegisteredQurban(it)
        }
    }

    private fun setRegisteredQurban(registeredQurban: ArrayList<EventRegisteredResponse>){
        val adapter = ListQurbanAdapter(registeredQurban)
        binding.rvRegisteredQurban.adapter = adapter
        adapter.setOnItemClickCallback(object : ListQurbanAdapter.OnItemClickCallback{
            override fun onItemClicked(data: String) {
                findNavController().navigate(HomeContainerFragmentDirections.actionHomeContainerFragmentToStatusQurbanFragment(data))
            }
        })
    }



}