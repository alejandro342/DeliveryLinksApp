package com.alejandro.deliverylinks.modules.selectroles.view.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.alejandro.deliverylinks.databinding.ActivitySelectRolesViewBinding
import com.alejandro.deliverylinks.models.user.User
import com.alejandro.deliverylinks.modules.selectroles.view.adapter.AdapterSelectRoles
import com.alejandro.deliverylinks.utils.SharedPref
import com.google.gson.Gson

class SelectRolesActivityView : AppCompatActivity() {

    private var mBinding: ActivitySelectRolesViewBinding? = null
    var mAdapterSelectRoles: AdapterSelectRoles? = null
    var user: User? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySelectRolesViewBinding.inflate(layoutInflater)
        val mView: View = mBinding!!.root
        setContentView(mView)

        mBinding!!.recyclerViewRoles.layoutManager = LinearLayoutManager(this)
        getUserFromSession()

        mAdapterSelectRoles = AdapterSelectRoles(this, user?.roles!!)
        mBinding!!.recyclerViewRoles.adapter = mAdapterSelectRoles

    }

    fun getUserFromSession() {
        val sharedPref = SharedPref(this)
        val gson = Gson()

        if (!sharedPref?.getInformation("user").isNullOrBlank()) {
            //si el ususario esta en session
            user = gson.fromJson(sharedPref?.getInformation("user"), User::class.java)

        }
    }

}