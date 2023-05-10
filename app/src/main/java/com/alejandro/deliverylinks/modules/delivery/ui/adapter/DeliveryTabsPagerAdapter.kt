package com.alejandro.deliverylinks.modules.delivery.ui.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.alejandro.deliverylinks.modules.delivery.ui.fragments.DeliveryOrderStatusFragment

//para inflar el view pager con sus estados //mostar ordenes restaurant
class DeliveryTabsPagerAdapter(
    mFragmentManager: FragmentManager,
    mLifecycle: Lifecycle,
    var mNumberOfTabs: Int //para determinar el numero de tab a mostrar
) : FragmentStateAdapter(mFragmentManager, mLifecycle) {

    override fun getItemCount(): Int {
        return  mNumberOfTabs
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> {
                val mBundle = Bundle()
                mBundle.putString("status", "DESPACHADO")
                val mDeliveryStatusFragment =
                    DeliveryOrderStatusFragment()//fragment que contiene el listado de ordenes
                mDeliveryStatusFragment.arguments = mBundle
                return mDeliveryStatusFragment
            }
            1 -> {
                val mBundle = Bundle()
                mBundle.putString("status", "EN CAMINO")
                val mDeliveryStatusFragment =
                    DeliveryOrderStatusFragment()//fragment que contiene el listado de ordenes
                mDeliveryStatusFragment.arguments = mBundle
                return mDeliveryStatusFragment
            }
            2 -> {
                val mBundle = Bundle()
                mBundle.putString("status", "ENTREGADO")
                val mDeliveryStatusFragment =
                    DeliveryOrderStatusFragment()//fragment que contiene el listado de ordenes
                mDeliveryStatusFragment.arguments = mBundle
                return mDeliveryStatusFragment
            }
            else -> {
                return DeliveryOrderStatusFragment()
            }
        }
    }
}