package com.alejandro.deliverylinks.base

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import com.alejandro.deliverylinks.models.user.User
import com.alejandro.deliverylinks.modules.client.ui.views.client.ClientHomeActivity
import com.alejandro.deliverylinks.modules.delivery.ui.views.DeliveryHomeActivityView
import com.alejandro.deliverylinks.modules.login.ui.views.LoginUserView
import com.alejandro.deliverylinks.modules.restaurant.ui.view.RestaurantHomeActivityView
import com.alejandro.deliverylinks.modules.selectimage.ui.view.SelectImageActivityView
import com.alejandro.deliverylinks.modules.selectroles.view.ui.SelectRolesActivityView
import com.alejandro.deliverylinks.utils.SharedPref
import com.google.gson.Gson

open class BasePresenter(mContext: Context) {

    open var mContext: Context? = null
    var mUser:User?=null

    init {
        this.mContext = mContext
    }

    //mandar al usuario a la pantalla principal de RestaurantHome
    fun goRestaurantHome() {
        val mIntent = Intent(mContext, RestaurantHomeActivityView::class.java)
        mIntent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK //eliminar historial de pantallas
        mContext?.startActivity(mIntent)
    }

    //mandar al usuario a la pantalla principal ClientHome
    fun goClientHome() {
        val mIntent = Intent(mContext, ClientHomeActivity::class.java)
        mIntent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK //eliminar historial de pantallas
        mContext?.startActivity(mIntent)
    }

    //mandar al usuario a la pantalla principal DeliveryHome
    fun goDeliveryHome() {
        val mIntent = Intent(mContext, DeliveryHomeActivityView::class.java)
        mIntent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK //eliminar historial de pantallas
        mContext?.startActivity(mIntent)
    }

    //mandar al usuario a la pantalla principal SelectImage Client
    fun goClient() {
        val mIntent = Intent(mContext, SelectImageActivityView::class.java)
        mIntent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK //eliminar historial de pantallas
        mContext?.startActivity(mIntent)

    }

    fun goSelectRoles() {

        val mIntent = Intent(mContext, SelectRolesActivityView::class.java)
        mIntent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK //eliminar historial de pantallas
        mContext?.startActivity(mIntent)
    }

    //validar correo
    fun String.mValidateEmail(): Boolean {
        return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this)
            .matches()
    }

    //Cerrar session del usuario
    fun closeSession() {
        val sharedPref = mContext?.let { SharedPref(it) }
        sharedPref?.closeSession("user")
        val mIntent = Intent(mContext, LoginUserView::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK) //eliminar historial de pantallas
        mContext?.startActivity(mIntent)
    }

    fun getUserFromSession() {
        val sharedPref = mContext?.let { SharedPref(it) }
        val gson = Gson()

        if (!sharedPref?.getInformation("user").isNullOrBlank()) {
            //si el usuario esta en session
            mUser = gson.fromJson(sharedPref?.getInformation("user"), User::class.java)
           // Log.d("TAG", "Usuario $user")
        }
    }
}