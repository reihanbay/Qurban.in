package com.dicoding.qurbanin.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.qurbanin.R
import com.dicoding.qurbanin.core.utils.utility.StringHelper
import com.dicoding.qurbanin.data.Result
import com.dicoding.qurbanin.data.model.DistrictResponse
import com.dicoding.qurbanin.data.model.EventQurbanResponse
import com.dicoding.qurbanin.data.model.ProvinceResponseItem
import com.dicoding.qurbanin.data.model.RegencyResponse
import com.dicoding.qurbanin.databinding.FragmentHomeBinding
import com.dicoding.qurbanin.databinding.ItemDialogSearchOrganizerBinding
import com.dicoding.qurbanin.ui.ViewModelFactory
import com.dicoding.qurbanin.ui.adapter.HomeAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val factory by lazy { ViewModelFactory.getInstance(requireContext().applicationContext) }
    private val homeViewModel: HomeViewModel by viewModels { factory }

    private val homeAdapter : HomeAdapter by lazy { HomeAdapter() }
    private val listEvent : MutableList<EventQurbanResponse> by lazy { mutableListOf() }
    private var location = ""

    private var dataProvinces = mutableListOf<ProvinceResponseItem>()
    private val dataRegencies = mutableListOf<RegencyResponse>()
    private val dataDistricts = mutableListOf<DistrictResponse>()

    private val listProvince = mutableListOf<String>()
    private val listRegency = mutableListOf<String>()
    private val listDistrict = mutableListOf<String>()
    private val listVillage = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeViewModel.getProvinces()

        searchOrganizer()
        initUI()
        initObservable(view)
        initAction()
    }

    private fun initUI() {
        binding.apply {
            val provinceAdapter = ArrayAdapter(requireActivity(), R.layout.dropdown_item, listProvince)
            cvSearchOrganizer.autoTextProvince.setAdapter(provinceAdapter)

            val regencyAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, listRegency)
            cvSearchOrganizer.autoTextRegency.setAdapter(regencyAdapter)

            val districtAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, listDistrict)
            cvSearchOrganizer.autoTextDistrict.setAdapter(districtAdapter)

            val villageAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, listVillage)
            cvSearchOrganizer.autoTextVillage.setAdapter(villageAdapter)

            rvNearestOrganizer.apply {
                adapter = homeAdapter
                layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            }
        }
    }
    private fun initAction() {

        binding.searchViewOrganizer.setOnClickListener {
            val toSearchFragment = HomeContainerFragmentDirections.actionHomeContainerFragmentToSearchFragment(location)
            findNavController().navigate(toSearchFragment)
        }

        binding.tvUserLocation.setOnClickListener {
            showInputDialog()
            homeViewModel.getListEvent(location)
        }

        homeAdapter.onClick = {
            findNavController().navigate(HomeContainerFragmentDirections.actionHomeContainerFragmentToDetailFragment(it.idEvent))
        }
    }

    private fun initObservable(view: View) {
        viewLifecycleOwner.lifecycleScope.launch {
            homeViewModel.getUserLocation()
        }
        //Get Data Username Local
        homeViewModel.getUserName().observe(viewLifecycleOwner) { name ->
            binding.tvGreeting.text = StringHelper.greeting(name)
        }
        homeViewModel.location.observe(viewLifecycleOwner) {
            if (it.isNullOrEmpty()) {
                showInputDialog()
            } else {
                binding.tvUserLocation.text = it
                location = it
                homeViewModel.getListEvent(location)
            }
        }
        homeViewModel.listEventData.observe(viewLifecycleOwner) {
            when(it) {
                is Result.Success -> {
                    binding.progressBar.isVisible = false
                    listEvent.addAll(it.data)
                    if (it.data.isEmpty()) {
                        binding.emptyData.isGone = false
                        binding.rvNearestOrganizer.isGone = true
                    }
                    homeAdapter.submitList(it.data)
                }
                is Result.Error -> {
                    Snackbar.make(view,it.error, Snackbar.LENGTH_SHORT).show()
                }

                is Result.Loading -> {
                    binding.progressBar.isVisible = true
                }
            }
        }

        //API Location
        homeViewModel.listProvince.observe(viewLifecycleOwner) { resultProvinces ->
            when (resultProvinces) {
                is Result.Loading -> {}
                is Result.Success -> {
                    dataProvinces.addAll(resultProvinces.data)
                    val list = resultProvinces.data.map { it.name }
                    listProvince.clear()
                    listProvince.addAll(list)
                }
                is Result.Error -> {
                    showToast(resultProvinces.error)
                }
            }
        }

        homeViewModel.listRegencies.observe(viewLifecycleOwner) { resultRegencies ->
            when (resultRegencies) {
                is Result.Loading -> {}
                is Result.Success -> {
                    dataRegencies.clear()
                    dataRegencies.addAll(resultRegencies.data)
                    val list = resultRegencies.data.map { it.name }
                    listRegency.clear()
                    listRegency.addAll(list)
                }

                is Result.Error -> {
                    showToast(resultRegencies.error)
                }
            }
        }

        homeViewModel.listDistrict.observe(viewLifecycleOwner) { resultDistricts ->
            when (resultDistricts) {
                is Result.Loading -> {}
                is Result.Success -> {
                    dataDistricts.clear()
                    dataDistricts.addAll(resultDistricts.data)
                    val list = resultDistricts.data.map { it.name }
                    listDistrict.clear()
                    listDistrict.addAll(list)
                }

                is Result.Error -> {
                    showToast(resultDistricts.error)
                }
            }
        }

        homeViewModel.listVillage.observe(viewLifecycleOwner) { resultVillages ->
            when (resultVillages) {
                is Result.Loading -> {}
                is Result.Success -> {
                    val list = resultVillages.data.map {
                        it.name
                    }
                    listVillage.clear()
                    listVillage.addAll(list)
                }

                is Result.Error -> {
                    showToast(resultVillages.error)
                }
            }
        }
    }
    private fun searchOrganizer() {
        val autoTextProvince = binding.cvSearchOrganizer.autoTextProvince
        val autoTextRegency = binding.cvSearchOrganizer.autoTextRegency
        val autoTextDistrict = binding.cvSearchOrganizer.autoTextDistrict
        val autoTextVillage = binding.cvSearchOrganizer.autoTextVillage


        val textInputProvince = binding.cvSearchOrganizer.textInputProvince
        val textInputRegency = binding.cvSearchOrganizer.textInputRegency
        val textInputDistrict = binding.cvSearchOrganizer.textInputDistrict
        val textInputVillage = binding.cvSearchOrganizer.textInputVillage

        var province = ""
        autoTextProvince.setOnItemClickListener { adapterView, _, position, _ ->
            province = adapterView.getItemAtPosition(position).toString()
            setProvinceId(province)
        }

        var regency = ""
        autoTextRegency.setOnItemClickListener { adapterView, _, position, _ ->
            regency = adapterView.getItemAtPosition(position).toString()
            setRegencyId(regency)
        }

        var district = ""
        autoTextDistrict.setOnItemClickListener { adapterView, _, position, _ ->
            district = adapterView.getItemAtPosition(position).toString()
            setDistrictId(district)
        }

        var village = ""
        autoTextVillage.setOnItemClickListener { adapterView, _, position, _ ->
            village = adapterView.getItemAtPosition(position).toString()
        }
        autoTextProvince.isFocusable = false
        autoTextRegency.isFocusable = false
        autoTextDistrict.isFocusable = false
        autoTextVillage.isFocusable = false
        binding.cvSearchOrganizer.btnSetOrSearch.setOnClickListener {
            when {
                province.isEmpty() -> {
                    textInputProvince.error = getString(R.string.select_item_required)
                }
                regency.isEmpty() -> {
                    textInputRegency.error = getString(R.string.select_item_required)
                    textInputProvince.error = null
                }
                district.isEmpty() -> {
                    textInputDistrict.error = getString(R.string.select_item_required)
                    textInputRegency.error = null
                }
                village.isEmpty() -> {
                    textInputVillage.error = getString(R.string.select_item_required)
                    textInputDistrict.error = null
                }
                else -> {
                    textInputVillage.error = null

                    val location = StringHelper.concatLocation(province, regency, district, village)

                    val toSearchFragment = HomeContainerFragmentDirections.actionHomeContainerFragmentToSearchFragment(location)
                    findNavController().navigate(toSearchFragment)
                }
            }
        }
    }


    private fun showInputDialog() {
        val bind = ItemDialogSearchOrganizerBinding.inflate(LayoutInflater.from(requireContext()))
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(bind.root)
            .setCancelable(true)

        bind.textEnterCurrentLocation.visibility = View.VISIBLE
        bind.btnSetOrSearch.text = getString(R.string.set_location)

        val provinceAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, listProvince)
        bind.autoTextProvince.setAdapter(provinceAdapter)

        val regencyAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, listRegency)
        bind.autoTextRegency.setAdapter(regencyAdapter)

        val districtAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, listDistrict)
        bind.autoTextDistrict.setAdapter(districtAdapter)

        val villageAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, listVillage)
        bind.autoTextVillage.setAdapter(villageAdapter)

        var province = ""
        bind.autoTextProvince.setOnItemClickListener { adapterView, _, position, _ ->
            province = adapterView.getItemAtPosition(position).toString()
            setProvinceId(province)
        }

        var regency = ""
        bind.autoTextRegency.setOnItemClickListener { adapterView, _, position, _ ->
            regency = adapterView.getItemAtPosition(position).toString()
            setRegencyId(regency)
        }

        var district = ""
        bind.autoTextDistrict.setOnItemClickListener { adapterView, _, position, _ ->
            district = adapterView.getItemAtPosition(position).toString()
            setDistrictId(district)
        }

        var village = ""
        bind.autoTextVillage.setOnItemClickListener { adapterView, _, position, _ ->
            village = adapterView.getItemAtPosition(position).toString()
        }

        bind.autoTextProvince.isFocusable = false
        bind.autoTextRegency.isFocusable = false
        bind.autoTextDistrict.isFocusable = false
        bind.autoTextVillage.isFocusable = false


        val build = dialog.show()

        bind.btnSetOrSearch.setOnClickListener {
            when {
                province.isEmpty() -> {
                    bind.textInputProvince.error = getString(R.string.select_item_required)
                }
                regency.isEmpty() -> {
                    bind.textInputRegency.error = getString(R.string.select_item_required)
                    bind.textInputProvince.error = null
                }
                district.isEmpty() -> {
                    bind.textInputDistrict.error = getString(R.string.select_item_required)
                    bind.textInputRegency.error = null
                }
                village.isEmpty() -> {
                    bind.textInputVillage.error = getString(R.string.select_item_required)
                    bind.textInputDistrict.error = null
                }
                else -> {
                    val location = StringHelper.concatLocation(province, regency, district, village)
                    setUserLocation(location)
                    this.location = location
                    if (location.isNotEmpty()) showToast("Set location success")
                    build.dismiss()
                }
            }
        }
    }

    private fun setUserLocation(location: String) {
       homeViewModel.setUserLocation(location)
    }

    private fun setProvinceId(province: String) {
        val item = dataProvinces.find { it.name == province }
        val provinceId = item?.id as String
        homeViewModel.getRegencies(provinceId)
    }

    private fun setRegencyId(regency: String) {
        val item = dataRegencies.find { it.name == regency }
        val regencyId = item?.id as String
        homeViewModel.getDistricts(regencyId)
    }

    private fun setDistrictId(district: String) {
        val item = dataDistricts.find { it.name == district }
        val districtId = item?.id as String
        homeViewModel.getVillages(districtId)
    }

    private fun showToast(message: String) =
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}