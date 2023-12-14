package com.dicoding.qurbanin.ui.authentication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.qurbanin.R
import com.dicoding.qurbanin.core.utils.datastore.SettingPreferences
import com.dicoding.qurbanin.core.utils.datastore.datastore
import com.dicoding.qurbanin.data.model.ListEventQurbanResponseItem
import com.dicoding.qurbanin.databinding.FragmentProfileBinding
import com.dicoding.qurbanin.ui.adapter.ListQurbanAdapter
import com.dicoding.qurbanin.ui.list_qurban.ListQurbanFragment
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var profileQurbanViewModel: ProfileQurbanViewModel
    private lateinit var settingPreferences: SettingPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: User ViewModel Factory
        profileQurbanViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[ProfileQurbanViewModel::class.java]
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

        val profileQurbanViewModel = ViewModelProvider(this,
            ViewModelProvider.NewInstanceFactory()).get(ProfileQurbanViewModel::class.java)

        settingPreferences = SettingPreferences.getInstance(requireContext().datastore)

        lifecycleScope.launch {
            val userName = settingPreferences.getUsername().first()
            val userEmail = settingPreferences.getUserEmail().first()

            binding.tvProfileName.text = "$userName"
            binding.tvEmail.text = "$userEmail"

            binding.ivLogoutButton.setOnClickListener {
                logOutAccount()
            }
        }

        profileQurbanViewModel.registeredQurban.observe(viewLifecycleOwner) {
            setRegisteredQurban(it)
        }
    }

    private fun setRegisteredQurban(registeredQurban: ArrayList<ListEventQurbanResponseItem>){
        val adapter = ListQurbanAdapter(registeredQurban)
        binding.rvRegisteredQurban.adapter = adapter

        adapter.setOnItemClickCallback(object : ListQurbanAdapter.OnItemClickCallback{

            override fun onItemClicked(data: ListEventQurbanResponseItem) {
                val mBundle = Bundle()
                mBundle.putString(ListQurbanFragment.EXTRA_ID_EVENT,data.IDEvent)
                view?.findNavController()?.navigate(R.id.action_profileFragment_to_statusQurbanFragment,mBundle)
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