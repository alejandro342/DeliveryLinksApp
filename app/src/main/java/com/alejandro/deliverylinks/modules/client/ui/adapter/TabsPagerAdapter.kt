package com.alejandro.deliverylinks.modules.client.ui.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.alejandro.deliverylinks.modules.client.ui.fragments.ClientOrdersStatusFragment

//para inflar el view pager con sus estados
class TabsPagerAdapter(
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
                mBundle.putString("status", "PAGADO")
                val mClientStatusFragment =
                    ClientOrdersStatusFragment()//fragment que contiene el listado de ordenes
                mClientStatusFragment.arguments = mBundle
                return mClientStatusFragment
            }
            1 -> {
                val mBundle = Bundle()
                mBundle.putString("status", "DESPACHADO")
                val mClientStatusFragment =
                    ClientOrdersStatusFragment()//fragment que contiene el listado de ordenes
                mClientStatusFragment.arguments = mBundle
                return mClientStatusFragment
            }
            2 -> {
                val mBundle = Bundle()
                mBundle.putString("status", "EN CAMINO")
                val mClientStatusFragment =
                    ClientOrdersStatusFragment()//fragment que contiene el listado de ordenes
                mClientStatusFragment.arguments = mBundle
                return mClientStatusFragment
            }
            3 -> {
                val mBundle = Bundle()
                mBundle.putString("status", "ENTREGADO")
                val mClientStatusFragment =
                    ClientOrdersStatusFragment()//fragment que contiene el listado de ordenes
                mClientStatusFragment.arguments = mBundle
                return mClientStatusFragment
            }
            else -> {
                return ClientOrdersStatusFragment()
            }
        }
    }
}