package com.dicoding.qurbanin.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import com.dicoding.qurbanin.R
import com.dicoding.qurbanin.databinding.FragmentHomeContainerBinding
import com.dicoding.qurbanin.ui.authentication.ProfileFragment
import com.dicoding.qurbanin.ui.home.adapter.MainViewPagerAdapter
import com.dicoding.qurbanin.ui.list_qurban.ListQurbanFragment
import com.google.android.material.navigation.NavigationBarView

class HomeContainerFragment : Fragment() {
    private lateinit var bind : FragmentHomeContainerBinding
    private lateinit var viewPagerAdapter: MainViewPagerAdapter

    private val navController: NavController by lazy {
        Navigation.findNavController(requireActivity(), R.id.container)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        bind = FragmentHomeContainerBinding.inflate(inflater, container, false)
        setViewPager()
        bind.bottomNav.setupWithNavController(navController)

        val mOnItemSelectedListener =
            NavigationBarView.OnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.homeFragment -> {
                        bind.homeViewPager.setCurrentItem(0, true)
                        return@OnItemSelectedListener true
                    }

                    R.id.accountFragment -> {
                        bind.homeViewPager.setCurrentItem(2, true)
                        return@OnItemSelectedListener true
                    }

                    R.id.feedFragment -> {
                        bind.homeViewPager.setCurrentItem(1, true)
                        return@OnItemSelectedListener true
                    }
                }
                false
            }

        bind.bottomNav.setOnItemSelectedListener(mOnItemSelectedListener)
        bind.bottomNav.selectedItemId = R.id.homeFragment
        bind.homeViewPager.isUserInputEnabled = false
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAction()
    }

    private fun setViewPager() {
        val fragmentList = arrayListOf(HomeFragment(), ListQurbanFragment(), ProfileFragment())
        viewPagerAdapter = MainViewPagerAdapter(fragmentList, childFragmentManager, lifecycle)
        bind.homeViewPager.adapter = viewPagerAdapter
    }
    private fun initAction() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (bind.bottomNav.selectedItemId != R.id.homeFragment) {
                bind.bottomNav.selectedItemId = R.id.homeFragment
            } else {
                requireActivity().finishAffinity()
            }
        }
    }
}