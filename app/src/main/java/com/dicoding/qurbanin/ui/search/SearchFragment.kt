package com.dicoding.qurbanin.ui.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.qurbanin.data.Result
import com.dicoding.qurbanin.data.model.EventQurbanResponse
import com.dicoding.qurbanin.databinding.FragmentSearchBinding
import com.dicoding.qurbanin.ui.ViewModelFactory
import com.dicoding.qurbanin.ui.adapter.SearchAdapter
import com.google.android.material.snackbar.Snackbar

class SearchFragment : Fragment() {

    private lateinit var bind: FragmentSearchBinding
    private val factory = ViewModelFactory.getInstance()
    private val viewModel: SearchViewModel by viewModels {
        factory
    }
    private val searchAdapter : SearchAdapter by lazy { SearchAdapter() }
    private lateinit var listEvent : List<EventQurbanResponse>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        bind = FragmentSearchBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getListEvent("")
        initObservable(view)
    }

    private fun initUi() {
        bind.apply {
            rvSearch.apply {
                layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
                adapter = searchAdapter
                setHasFixedSize(true)
            }
        }
    }

    private fun initAction() {
        bind.apply {
            searchAdapter.onClick = {
                findNavController().navigate(SearchFragmentDirections.actionSearchFragmentToDetailFragment(it.idEvent))
            }
            btnBack.setOnClickListener {
                findNavController().popBackStack()
            }

            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    searchAdapter.submitList(viewModel.getSearchListEvent(query, listEvent))
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    searchAdapter.submitList(viewModel.getSearchListEvent(newText, listEvent))
                    return false
                }
            })
        }
    }


    private fun initObservable(view: View) {
        viewModel.listEventData.observe(viewLifecycleOwner) {
            when(it) {
                is Result.Success -> {
                    bind.progressBar.isVisible = false
                    listEvent = it.data
                    searchAdapter.submitList(it.data)
                }
                is Result.Error -> {
                    Snackbar.make(view,it.error, Snackbar.LENGTH_SHORT).show()
                }

                is Result.Loading -> {
                    bind.progressBar.isVisible = true
                }
            }
        }
        initUi()
        initAction()
    }


}