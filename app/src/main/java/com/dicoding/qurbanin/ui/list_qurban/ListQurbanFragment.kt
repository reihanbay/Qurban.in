package com.dicoding.qurbanin.ui.list_qurban

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.qurbanin.R
import com.dicoding.qurbanin.data.model.ListEventQurbanResponseItem
import com.dicoding.qurbanin.databinding.FragmentListQurbanBinding
import com.dicoding.qurbanin.ui.adapter.ListQurbanAdapter

class ListQurbanFragment : Fragment() {

    private var _binding: FragmentListQurbanBinding? = null
    private val binding get() = _binding!!
    private lateinit var listQurbanViewModel: ListQurbanViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: User ViewModel Factory
        listQurbanViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[ListQurbanViewModel::class.java]
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

        val listQurbanViewModel = ViewModelProvider(this,
            ViewModelProvider.NewInstanceFactory()).get(ListQurbanViewModel::class.java)

        listQurbanViewModel.listQurban.observe(viewLifecycleOwner) {
            setQurbanList(it)
        }
    }

    private fun setQurbanList(listQurban: ArrayList<ListEventQurbanResponseItem>) {
        val adapter = ListQurbanAdapter(listQurban)
        binding.rvHistory.adapter = adapter

        adapter.setOnItemClickCallback(object : ListQurbanAdapter.OnItemClickCallback{

            override fun onItemClicked(data: ListEventQurbanResponseItem) {
                val mBundle = Bundle()
                mBundle.putString(EXTRA_ID_EVENT,data.IDEvent)
                view?.findNavController()?.navigate(R.id.action_listQurbanFragment_to_statusQurbanFragment,mBundle)
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