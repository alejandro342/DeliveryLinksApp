package com.alejandro.deliverylinks.modules.restaurant.ui.view

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.alejandro.deliverylinks.R
import com.alejandro.deliverylinks.databinding.ActivityRestaurantHomeViewBinding
import com.alejandro.deliverylinks.modules.client.ui.fragments.ClientProfileFragment
import com.alejandro.deliverylinks.modules.restaurant.presenter.RestaurantPresenter
import com.alejandro.deliverylinks.modules.restaurant.ui.fragments.RestaurantCategoryFragment
import com.alejandro.deliverylinks.modules.restaurant.ui.fragments.RestaurantOrdersFragment
import com.alejandro.deliverylinks.modules.restaurant.ui.fragments.RestaurantProductFragment

class RestaurantHomeActivityView : AppCompatActivity() {

    private var mBinding: ActivityRestaurantHomeViewBinding? = null
    private var mRestaurantPresenter: RestaurantPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityRestaurantHomeViewBinding.inflate(layoutInflater)
        val mView: View = mBinding!!.root
        setContentView(mView)

        mRestaurantPresenter = RestaurantPresenter(this)
        mRestaurantPresenter!!.getUserFromSession()
        //fragment load default
        openFragment(RestaurantOrdersFragment())
        selectItemBottomView()

    }

    private fun selectItemBottomView() {

        mBinding?.bottomNavigation?.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.item_home -> {
                    openFragment(RestaurantOrdersFragment())
                    true
                }
                R.id.item_category -> {
                    openFragment(RestaurantCategoryFragment())
                    true
                }
                R.id.item_products -> {
                    openFragment(RestaurantProductFragment())
                    true
                }
                R.id.item_profile -> {
                    openFragment(ClientProfileFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun openFragment(mFragment: Fragment) {

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, mFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}