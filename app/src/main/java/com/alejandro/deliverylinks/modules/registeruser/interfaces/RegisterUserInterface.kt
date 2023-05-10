package com.alejandro.deliverylinks.modules.registeruser.interfaces

interface RegisterUserInterface {
    fun backLogin()
    fun validateData(mName:String, mLastName:String, mEmail:String,mPhone:String, mPassword:String,mRepeatPassword:String) :Boolean
    fun saveUserSession(information: String)
    fun goClient()

}