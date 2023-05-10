package com.alejandro.deliverylinks.modules.delivery.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.alejandro.deliverylinks.R
import com.alejandro.deliverylinks.databinding.FragmentDeliveryOrdersBinding
import com.alejandro.deliverylinks.modules.delivery.ui.adapter.DeliveryTabsPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class DeliveryOrdersFragment : Fragment() {
    private var mView: View? = null
    private var _mBinding: FragmentDeliveryOrdersBinding? = null
    private val mBinding get() = _mBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _mBinding = FragmentDeliveryOrdersBinding.inflate(inflater, container, false)
        mView = mBinding?.root

        configTab()
        configAdapterTab()
        stateTabsClient()
        return mView
    }

    fun configTab() {
        //configuarion del tab
        mBinding!!.TabOrdersDelivery.setSelectedTabIndicatorColor(Color.WHITE)
        mBinding!!.TabOrdersDelivery.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.primary_blue_variant
            )
        )
        mBinding!!.TabOrdersDelivery.tabTextColors =
            ContextCompat.getColorStateList(requireContext(), R.color.white)
    }

    fun configAdapterTab() {
        //establecer la cantidad de tabs
        val numberOfTabs = 3
        val mAdapter =
            DeliveryTabsPagerAdapter(
                requireActivity().supportFragmentManager,
                lifecycle,
                numberOfTabs
            )
        mBinding!!.viewPOrdersDelivery.adapter = mAdapter
        mBinding!!.viewPOrdersDelivery.isUserInputEnabled = true
    }

    //textos en el viewPager
    fun stateTabsClient() {
        TabLayoutMediator(
            mBinding!!.TabOrdersDelivery,
            mBinding!!.viewPOrdersDelivery
        ) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "DESPACHADO"
                }
                1 -> {
                    tab.text = "EN CAMINO"
                }
                2 -> {
                    tab.text = "ENTREGADO"
                }
            }
        }.attach()
    }

}