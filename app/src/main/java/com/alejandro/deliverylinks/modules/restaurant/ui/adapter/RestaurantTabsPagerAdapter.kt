package com.alejandro.deliverylinks.modules.restaurant.ui.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.alejandro.deliverylinks.modules.restaurant.ui.fragments.RestaurantOrdersStatusFragment

//para inflar el view pager con sus estados //mostar ordenes restaurant
class RestaurantTabsPagerAdapter(
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
                val mRestaurantStatusFragment =
                    RestaurantOrdersStatusFragment()//fragment que contiene el listado de ordenes
                mRestaurantStatusFragment.arguments = mBundle
                return mRestaurantStatusFragment
            }
            1 -> {
                val mBundle = Bundle()
                mBundle.putString("status", "DESPACHADO")
                val mRestaurantStatusFragment =
                    RestaurantOrdersStatusFragment()//fragment que contiene el listado de ordenes
                mRestaurantStatusFragment.arguments = mBundle
                return mRestaurantStatusFragment
            }
            2 -> {
                val mBundle = Bundle()
                mBundle.putString("status", "EN CAMINO")
                val mRestaurantStatusFragment =
                    RestaurantOrdersStatusFragment()//fragment que contiene el listado de ordenes
                mRestaurantStatusFragment.arguments = mBundle
                return mRestaurantStatusFragment
            }
            3 -> {
                val mBundle = Bundle()
                mBundle.putString("status", "ENTREGADO")
                val mRestaurantStatusFragment =
                    RestaurantOrdersStatusFragment()//fragment que contiene el listado de ordenes
                mRestaurantStatusFragment.arguments = mBundle
                return mRestaurantStatusFragment
            }
            else -> {
                return RestaurantOrdersStatusFragment()
            }
        }
    }
}