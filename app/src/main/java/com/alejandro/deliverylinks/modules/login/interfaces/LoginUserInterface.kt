package com.alejandro.deliverylinks.modules.login.interfaces

interface LoginUserInterface {
    fun goRegisterUser()
    fun validateData(mEmail: String, mPassword: String): Boolean
    fun saveUserSession(information: String)
    fun getUserFromSessionLogin()
    fun goSelectRoles()

}