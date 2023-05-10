package com.alejandro.deliverylinks.modules.client.ui.views.client

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.alejandro.deliverylinks.R
import com.alejandro.deliverylinks.databinding.ActivityClientHomeBinding
import com.alejandro.deliverylinks.modules.client.presenter.ClientHomePresenter
import com.alejandro.deliverylinks.modules.client.ui.fragments.ClientCategoriesFragment
import com.alejandro.deliverylinks.modules.client.ui.fragments.ClientOrdersFragment
import com.alejandro.deliverylinks.modules.client.ui.fragments.ClientProfileFragment
import com.alejandro.deliverylinks.providers.UsersProvider
import com.alejandro.deliverylinks.utils.SharedPref

class ClientHomeActivity : AppCompatActivity() {

    private var mBinding: ActivityClientHomeBinding? = null

    private var mHomePresenter: ClientHomePresenter? = null

    var mSharedPref:SharedPref?=null

    //para hacer uso del token de notificaciones
    var mUsersProvider:UsersProvider?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityClientHomeBinding.inflate(layoutInflater)
        val view: View = mBinding!!.root
        setContentView(view)
        mSharedPref=SharedPref(this)
        mHomePresenter = ClientHomePresenter(this)

        //fragment load default
        openFragment(ClientCategoriesFragment())
        mHomePresenter!!.getUserFromSession()
        selectItemBottomView()
    }

    private fun selectItemBottomView() {
        mBinding?.bottomNavigation?.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.item_home -> {
                    openFragment(ClientCategoriesFragment())
                    true
                }
                R.id.item_orders -> {
                    openFragment(ClientOrdersFragment())
                    true
                }
                R.id.item_profile -> {
                    openFragment(ClientProfileFragment())
                    true
                }
                else -> false
            }
        }
        mHomePresenter?.getUserFromSession()
        mUsersProvider = UsersProvider(token = mHomePresenter?.mUser?.sessionToken!!)
        createToken()
    }

    private fun createToken(){
        mUsersProvider?.createTokenNotifications(mHomePresenter?.mUser!!,this)
    }

    private fun openFragment(mFragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, mFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}
