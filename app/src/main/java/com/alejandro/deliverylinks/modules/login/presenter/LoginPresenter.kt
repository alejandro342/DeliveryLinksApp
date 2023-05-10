package com.alejandro.deliverylinks.modules.login.presenter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.alejandro.deliverylinks.R
import com.alejandro.deliverylinks.base.BasePresenter
import com.alejandro.deliverylinks.models.response.ResponseHttp
import com.alejandro.deliverylinks.models.user.User
import com.alejandro.deliverylinks.modules.login.interfaces.LoginUserInterface
import com.alejandro.deliverylinks.modules.registeruser.ui.views.RegisterUserView
import com.alejandro.deliverylinks.providers.UsersProvider
import com.alejandro.deliverylinks.utils.MyProgressBar
import com.alejandro.deliverylinks.utils.SharedPref
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginPresenter(mContext: Context, mMyProgressBarB: MyProgressBar, mMyProgressBar: MyProgressBar.progress) :
    BasePresenter(mContext), LoginUserInterface, MyProgressBar,MyProgressBar.progress {

    private var usersProvider = UsersProvider()
    override var mContext: Context? = null
    private var mMyProgressBar: MyProgressBar.progress? = null
    private var  mMyProgressBarB: MyProgressBar?=null

    init {
        this.mContext = mContext
        this.mMyProgressBar = mMyProgressBar
        this.mMyProgressBarB=mMyProgressBarB
    }

    override fun validateData(mEmail: String, mPassword: String): Boolean {

        if (mEmail.isEmpty()) {
            Toast.makeText(mContext, R.string.txt_email_void_register_user, Toast.LENGTH_LONG)
                .show()

        } else if (!mEmail.mValidateEmail()) {
            Toast.makeText(mContext, R.string.txt_email_valid_void_register_user, Toast.LENGTH_LONG)
                .show()
            return false
        } else if (mPassword.isEmpty()) {
            Toast.makeText(mContext, R.string.txt_password_void_register_user, Toast.LENGTH_LONG)
                .show()

        } else {
            mMyProgressBar?.showProgressBar()
            mMyProgressBarB?.hideBottom()
            usersProvider.loginUsers(mEmail, mPassword)?.enqueue(object : Callback<ResponseHttp> {
                override fun onResponse(
                    call: Call<ResponseHttp>,
                    response: Response<ResponseHttp>
                ) {
                    Log.d("LoginPresenter", "Response: ${response.body()}")
                    if (response.body()?.isSuccess == true) {

                        Toast.makeText(mContext, response.body()?.message, Toast.LENGTH_LONG).show()
                        saveUserSession(response.body()?.data.toString())

                    } else {

                        Toast.makeText(mContext, "Datos incorrectos", Toast.LENGTH_LONG).show()
                    }
                    mMyProgressBarB?.showBottom()
                }

                override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                    mMyProgressBar?.hideProgressBar()
                    mMyProgressBarB?.showBottom()
                    Toast.makeText(mContext, "Hubo un error ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
        }
        return true
    }

    override fun saveUserSession(information: String) {

        val sharedPref = mContext?.let { SharedPref(it) }
        val gson = Gson()
        //aqui se almacena toda la informacion del usuario
        val user = gson.fromJson(information, User::class.java)
        sharedPref?.saveSession("user", user)

        //preguntamos si el usuario guardado es mayor a uno mas de un rol no se enviara a la pantalla
        //de cliente

        if (user.roles?.size!! > 1) {//tiene mas de un rol
            goSelectRoles()
        } else {
            goClientHome()
        }
    }

    override fun getUserFromSessionLogin() {

        val sharedPref = mContext?.let { SharedPref(it) }
        val gson = Gson()

        if (!sharedPref?.getInformation("user").isNullOrBlank()) {
            //si el ususario esta en session
            val user = gson.fromJson(sharedPref?.getInformation("user"), User::class.java)
            //validar si el usuario almaceno la informacion
            if (!sharedPref!!.getInformation("rol").isNullOrBlank()) {
                //si selecciono un rol                         eliminar las comilla
                val rol = sharedPref.getInformation("rol")?.replace("\"", "")
                if (rol == "RESTAURANTE") {
                    goRestaurantHome()
                } else if (rol == "CLIENTE") {
                    goClientHome()
                } else if (rol == "REPARTIDOR") {
                    goDeliveryHome()
                }
            } else {
                goClientHome()
            }
        }
    }

    override fun showProgressBar() {

    }

    override fun hideProgressBar() {

    }

    override fun showBottom() {
    }

    override fun hideBottom() {
    }

    override fun goRegisterUser() {

        val mIntent = Intent(mContext, RegisterUserView::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP) //eliminar historial de pantallas
        mContext?.startActivity(mIntent)
    }
}