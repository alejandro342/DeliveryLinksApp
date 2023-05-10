package com.alejandro.deliverylinks.modules.delivery.ui.views

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.alejandro.deliverylinks.R
import com.alejandro.deliverylinks.databinding.ActivityDeliveryHomeViewBinding
import com.alejandro.deliverylinks.models.user.User
import com.alejandro.deliverylinks.modules.client.ui.fragments.ClientProfileFragment
import com.alejandro.deliverylinks.modules.delivery.presenter.DeliveryPresenter
import com.alejandro.deliverylinks.modules.delivery.ui.fragments.DeliveryOrdersFragment
import com.alejandro.deliverylinks.utils.SharedPref
import com.google.gson.Gson

class DeliveryHomeActivityView : AppCompatActivity() {

    private var mBinding: ActivityDeliveryHomeViewBinding? = null
    private var mDeliveryPresenter: DeliveryPresenter? = null
    private var sharedPref: SharedPref? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityDeliveryHomeViewBinding.inflate(layoutInflater)
        val mView: View = mBinding!!.root
        setContentView(mView)
        mDeliveryPresenter = DeliveryPresenter(this)
        sharedPref = SharedPref(this)

        //fragment load default
        openFragment(DeliveryOrdersFragment())
        mDeliveryPresenter!!.getUserFromSession()
        selectItemBottomView()
    }

    private fun selectItemBottomView() {
        mBinding?.bottomNavigation?.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.item_orders -> {
                    openFragment(DeliveryOrdersFragment())
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