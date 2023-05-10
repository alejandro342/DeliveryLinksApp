package com.alejandro.deliverylinks.modules.selectimage.presenter

import android.content.Context
import com.alejandro.deliverylinks.base.BasePresenter
import com.alejandro.deliverylinks.modules.selectimage.interfaces.SelectImageInterface

class SelectImagePresenter(mContext: Context) : BasePresenter(mContext), SelectImageInterface {
    override var mContext: Context? = null

    init {
        this.mContext = mContext
    }
}