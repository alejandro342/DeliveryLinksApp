package com.alejandro.deliverylinks.modules.client.presenter

import android.content.Context
import android.content.Intent
import com.alejandro.deliverylinks.base.BasePresenter
import com.alejandro.deliverylinks.modules.client.interfaces.ClientInterface
import com.alejandro.deliverylinks.modules.client.ui.views.client.ClientHomeActivity
import com.alejandro.deliverylinks.modules.client.ui.views.client.ClientUpdateActivity

class ClientHomePresenter(mContext: Context) : BasePresenter(mContext), ClientInterface {
    override var mContext: Context? = null

    init {
        this.mContext = mContext
    }

    override fun goClientProfileHome() {
        val mIntent = Intent(mContext, ClientHomeActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK) //eliminar historial de pantallas
        mContext?.startActivity(mIntent)
    }

    override fun goClientUpdateProfile() {
        val mIntent = Intent(mContext, ClientUpdateActivity::class.java)
        mContext?.startActivity(mIntent)
    }

}