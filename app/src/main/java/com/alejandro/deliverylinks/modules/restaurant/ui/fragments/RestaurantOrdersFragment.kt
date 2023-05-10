package com.alejandro.deliverylinks.modules.restaurant.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.alejandro.deliverylinks.R
import com.alejandro.deliverylinks.databinding.FragmentRestaurantOrdersBinding
import com.alejandro.deliverylinks.modules.client.ui.adapter.TabsPagerAdapter
import com.alejandro.deliverylinks.modules.restaurant.ui.adapter.RestaurantTabsPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

//mostrar ordenes en el rol restaurant
class RestaurantOrdersFragment : Fragment() {
    private var mView: View? = null
    private var _mBinding: FragmentRestaurantOrdersBinding? = null
    private val mBinding get() = _mBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _mBinding = FragmentRestaurantOrdersBinding.inflate(inflater, container, false)
        mView = mBinding?.root

        configTab()
        configAdapterTab()
        stateTabsClient()
        return mView
    }

    fun configTab() {
        //configuarion del tab
        mBinding!!.TabOrdersClient.setSelectedTabIndicatorColor(Color.WHITE)
        mBinding!!.TabOrdersClient.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.primary_blue_variant))
        mBinding!!.TabOrdersClient.tabTextColors =
            ContextCompat.getColorStateList(requireContext(), R.color.white)
        mBinding!!.TabOrdersClient.tabMode = TabLayout.MODE_SCROLLABLE
        mBinding!!.TabOrdersClient.isInlineLabel = true

    }

    fun configAdapterTab() {
        //establecer la cantidad de tabs
        val numberOfTabs = 4
        val mAdapter =
            RestaurantTabsPagerAdapter(requireActivity().supportFragmentManager, lifecycle, numberOfTabs)
        mBinding!!.viewPOrdersClient.adapter = mAdapter
        mBinding!!.viewPOrdersClient.isUserInputEnabled = true
    }
    //textos en el viewPager
    fun stateTabsClient() {
        TabLayoutMediator(
            mBinding!!.TabOrdersClient,
            mBinding!!.viewPOrdersClient
        ) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "PAGADO"
                }
                1 -> {
                    tab.text = "DESPACHADO"
                }
                2 -> {
                    tab.text = "EN CAMINO"
                }
                3 -> {
                    tab.text = "ENTREGADO"
                }
            }
        }.attach()
    }

}