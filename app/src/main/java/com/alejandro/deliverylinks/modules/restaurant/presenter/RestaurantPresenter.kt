package com.alejandro.deliverylinks.modules.restaurant.presenter

import android.content.Context
import com.alejandro.deliverylinks.base.BasePresenter
import com.alejandro.deliverylinks.modules.restaurant.interfaces.RestaurantPresenterInterface

class RestaurantPresenter(mContext: Context) : BasePresenter(mContext),
    RestaurantPresenterInterface {
    override var mContext: Context? = null

    init {
        this.mContext = mContext
    }
}