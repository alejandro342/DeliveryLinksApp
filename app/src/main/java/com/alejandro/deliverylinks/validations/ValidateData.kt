package com.alejandro.deliverylinks.validations

import android.text.TextUtils

class ValidateData {
    fun isEmailValid():Boolean{
        return !TextUtils.isEmpty(this.toString()) && android.util.Patterns.EMAIL_ADDRESS.matcher(this.toString()).matches()
    }
}