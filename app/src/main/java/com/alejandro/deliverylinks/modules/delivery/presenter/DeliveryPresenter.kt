package com.alejandro.deliverylinks.modules.delivery.presenter

import android.content.Context
import com.alejandro.deliverylinks.base.BasePresenter

class DeliveryPresenter(mContext: Context) : BasePresenter(mContext) {

    override var mContext: Context? = null

    init {
        this.mContext = mContext
    }

}