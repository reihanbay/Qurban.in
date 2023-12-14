package com.dicoding.qurbanin.ui.home

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dicoding.qurbanin.R

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding
    private val TAG = HomeFragment::class.java.simpleName

    private lateinit var viewModelFactory: ViewModelFactory
    private val homeViewModel: HomeViewModel by viewModels { viewModelFactory }

    private lateinit var dataProvinces: List<ProvinceResponseItem>
    private val dataRegencies = mutableListOf<RegencyResponse>()
    private val dataDistricts = mutableListOf<DistrictResponse>()

    private val listProvince = mutableListOf<String>()
    private val listRegency = mutableListOf<String>()
    private val listDistrict = mutableListOf<String>()
    private val listVillage = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        val provinceAdapter = ArrayAdapter(requireActivity(), R.layout.dropdown_item, listProvince)
        binding?.cvSearchOrganizerLocation?.autoTextProvince?.setAdapter(provinceAdapter)

        val regencyAdapter = ArrayAdapter(requireActivity(), R.layout.dropdown_item, listRegency)
        binding?.cvSearchOrganizerLocation?.autoTextRegency?.setAdapter(regencyAdapter)

        val districtAdapter = ArrayAdapter(requireActivity(), R.layout.dropdown_item, listDistrict)
        binding?.cvSearchOrganizerLocation?.autoTextDistrict?.setAdapter(districtAdapter)

        val villageAdapter = ArrayAdapter(requireActivity(), R.layout.dropdown_item, listVillage)
        binding?.cvSearchOrganizerLocation?.autoTextVillage?.setAdapter(villageAdapter)

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModelFactory = ViewModelFactory.getInstance(requireContext().applicationContext)

        val greeting = StringHelper.greeting("Sukma") //change from datastore
        binding?.tvGreeting?.text = greeting

        var userLocation = ""

        viewLifecycleOwner.lifecycleScope.launch {
            homeViewModel.getUserLocation().collect { location ->
                withContext(Dispatchers.Main) {
                    if (location.isBlank()) {
                        showInputDialog()
                    } else {
                        userLocation = location
                        binding?.tvUserLocation?.text = location
                    }
                }
            }
        }

        homeViewModel.getProvinces().observe(viewLifecycleOwner) { resultProvinces ->
            when (resultProvinces) {
                is Result.Loading -> {}
                is Result.Success -> {
                    dataProvinces = resultProvinces.data
                    val list = resultProvinces.data.map { it.name }
                    listProvince.clear()
                    listProvince.addAll(list)
                }
                is Result.Error -> {
                    Log.e(TAG, "Get province: ${resultProvinces.error}")
                    showToast(resultProvinces.error)
                }
            }
        }

        binding?.searchViewOrganizer?.setOnClickListener {
            val toSearchFragment =
                HomeFragmentDirections.actionHomeFragmentToSearchFragment(userLocation)

            it.findNavController().navigate(toSearchFragment)
        }

        searchOrganizer()
    }

    private fun searchOrganizer() {
        val autoTextProvince = binding?.cvSearchOrganizerLocation?.autoTextProvince
        val autoTextRegency = binding?.cvSearchOrganizerLocation?.autoTextRegency
        val autoTextDistrict = binding?.cvSearchOrganizerLocation?.autoTextDistrict
        val autoTextVillage = binding?.cvSearchOrganizerLocation?.autoTextVillage

        val textInputProvince = binding?.cvSearchOrganizerLocation?.textInputProvince
        val textInputRegency = binding?.cvSearchOrganizerLocation?.textInputRegency
        val textInputDistrict = binding?.cvSearchOrganizerLocation?.textInputDistrict
        val textInputVillage = binding?.cvSearchOrganizerLocation?.textInputVillage

        var province = ""
        autoTextProvince?.setOnItemClickListener { adapterView, _, position, _ ->
            province = adapterView.getItemAtPosition(position).toString()
            setProvinceId(province)
        }

        var regency = ""
        autoTextRegency?.setOnItemClickListener { adapterView, _, position, _ ->
            regency = adapterView.getItemAtPosition(position).toString()
            setRegencyId(regency)
        }

        var district = ""
        autoTextDistrict?.setOnItemClickListener { adapterView, _, position, _ ->
            district = adapterView.getItemAtPosition(position).toString()
            setDistrictId(district)
        }

        var village = ""
        autoTextVillage?.setOnItemClickListener { adapterView, _, position, _ ->
            village = adapterView.getItemAtPosition(position).toString()
        }

        binding?.cvSearchOrganizerLocation?.btnSetOrSearch?.setOnClickListener {
            when {
                province.isEmpty() -> {
                    textInputProvince?.error = getString(R.string.select_item_required)
                }
                regency.isEmpty() -> {
                    textInputRegency?.error = getString(R.string.select_item_required)
                    textInputProvince?.error = null
                }
                district.isEmpty() -> {
                    textInputDistrict?.error = getString(R.string.select_item_required)
                    textInputRegency?.error = null
                }
                village.isEmpty() -> {
                    textInputVillage?.error = getString(R.string.select_item_required)
                    textInputDistrict?.error = null
                }
                else -> {
                    textInputVillage?.error = null

                    val location = StringHelper.concatLocation(province, regency, district, village)
                    Log.i(TAG, "Location search: $location")

                    val toSearchFragment =
                        HomeFragmentDirections.actionHomeFragmentToSearchFragment(location)

                    view?.findNavController()?.navigate(toSearchFragment)
                }
            }
        }
    }

    private fun getDataRegencies(id: String) {
        homeViewModel.getRegencies(id).observe(viewLifecycleOwner) { resultRegencies ->
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
                    Log.e(TAG, "Get regency: ${resultRegencies.error}")
                    showToast(resultRegencies.error)
                }
            }
        }
    }

    private fun getDataDistricts(id: String) {
        homeViewModel.getDistricts(id).observe(viewLifecycleOwner) { resultDistricts ->
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
                    Log.e(TAG, "Get district: ${resultDistricts.error}")
                    showToast(resultDistricts.error)
                }
            }
        }
    }

    private fun getDataVillages(id: String) {
        homeViewModel.getVillages(id).observe(viewLifecycleOwner) { resultVillages ->
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
                    Log.e(TAG, "Get village: ${resultVillages.error}")
                    showToast(resultVillages.error)
                }
            }
        }
    }

    private fun showInputDialog() {
        val inputDialog = Dialog(requireContext())
        inputDialog.setContentView(R.layout.item_dialog_search_organizer)
        inputDialog.setCancelable(false)

        val title = inputDialog.findViewById<TextView>(R.id.text_enter_current_location)
        val autoTextProvince = inputDialog.findViewById<AutoCompleteTextView>(R.id.auto_text_province)
        val autoTextRegency = inputDialog.findViewById<AutoCompleteTextView>(R.id.auto_text_regency)
        val autoTextDistrict = inputDialog.findViewById<AutoCompleteTextView>(R.id.auto_text_district)
        val autoTextVillage = inputDialog.findViewById<AutoCompleteTextView>(R.id.auto_text_village)
        val btnSetLocation = inputDialog.findViewById<Button>(R.id.btn_set_or_search)

        val textInputProvince = inputDialog.findViewById<TextInputLayout>(R.id.text_input_province)
        val textInputRegency = inputDialog.findViewById<TextInputLayout>(R.id.text_input_regency)
        val textInputDistrict = inputDialog.findViewById<TextInputLayout>(R.id.text_input_district)
        val textInputVillage = inputDialog.findViewById<TextInputLayout>(R.id.text_input_village)

        title.visibility = View.VISIBLE
        btnSetLocation.text = getString(R.string.set_location)

        val provinceAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, listProvince)
        autoTextProvince.setAdapter(provinceAdapter)

        val regencyAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, listRegency)
        autoTextRegency.setAdapter(regencyAdapter)

        val districtAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, listDistrict)
        autoTextDistrict.setAdapter(districtAdapter)

        val villageAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, listVillage)
        autoTextVillage.setAdapter(villageAdapter)

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

        val width = resources.displayMetrics.widthPixels
        inputDialog.window?.setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT)
        inputDialog.show()

        btnSetLocation.setOnClickListener {
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
                    val location =
                        StringHelper.concatLocation(province, regency, district, village)
                    Log.i(TAG, "Location set: $location")

                    setUserLocation(location)

                    if (location.isNotEmpty()) showToast("Set location success")
                    inputDialog.dismiss()
                }
            }
        }
    }

    private fun setUserLocation(location: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            homeViewModel.setUserLocation(location)
        }
    }

    private fun setProvinceId(province: String) {
        val item = dataProvinces.find { it.name == province }
        val provinceId = item?.id as String
        getDataRegencies(provinceId)

        Log.i(TAG, "provinceId = $provinceId")
    }

    private fun setRegencyId(regency: String) {
        val item = dataRegencies.find { it.name == regency }
        val regencyId = item?.id as String
        getDataDistricts(regencyId)

        Log.i(TAG, "regencyId = $regencyId")
    }

    private fun setDistrictId(district: String) {
        val item = dataDistricts.find { it.name == district }
        val districtId = item?.id as String
        getDataVillages(districtId)

        Log.i(TAG, "districtId = $districtId")
    }

    private fun showToast(message: String) =
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}