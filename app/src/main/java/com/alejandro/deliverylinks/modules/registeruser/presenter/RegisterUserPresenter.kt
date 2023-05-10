package com.alejandro.deliverylinks.modules.registeruser.presenter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.alejandro.deliverylinks.R
import com.alejandro.deliverylinks.base.BasePresenter
import com.alejandro.deliverylinks.models.response.ResponseHttp
import com.alejandro.deliverylinks.models.user.User
import com.alejandro.deliverylinks.modules.client.ui.dialogs.DialogSaveUser
import com.alejandro.deliverylinks.modules.login.ui.views.LoginUserView
import com.alejandro.deliverylinks.modules.registeruser.interfaces.RegisterUserInterface
import com.alejandro.deliverylinks.providers.UsersProvider
import com.alejandro.deliverylinks.utils.SharedPref
import com.alejandro.deliverylinks.validations.ValidateData
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterUserPresenter(mContext: Context) : BasePresenter(mContext), RegisterUserInterface {

    val TAG = "RegisterPresenter"
    var mValidateData: ValidateData? = null
    var usersProvider = UsersProvider()
    override var mContext: Context? = null
    private var mDialogSaveUser : DialogSaveUser?=null


    init {
        this.mContext = mContext
        mDialogSaveUser= DialogSaveUser()
    }

    override fun backLogin() {
        val mIntent = Intent(mContext, LoginUserView::class.java)
        Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP //eliminar historial de pantallas
        mContext?.startActivity(mIntent)
        mValidateData = ValidateData()
    }

    override fun validateData(
        mName: String,
        mLastName: String,
        mEmail: String,
        mPhone: String,
        mPassword: String,
        mRepeatPassword: String
    ): Boolean {
        if (mName.isEmpty()) {
            Toast.makeText(mContext, R.string.txt_name_void_register_user, Toast.LENGTH_LONG).show()
            return false
        } else if (mLastName.isEmpty()) {
            Toast.makeText(mContext, R.string.txt_name_void_register_user, Toast.LENGTH_LONG).show()
            return false
        } else if (mEmail.isEmpty()) {
            Toast.makeText(mContext, R.string.txt_email_void_register_user, Toast.LENGTH_LONG)
                .show()
            return false
        } else if (!mEmail.mValidateEmail()) {
            Toast.makeText(mContext, R.string.txt_email_valid_void_register_user, Toast.LENGTH_LONG)
                .show()
            return false
        } else if (mPhone.isEmpty()) {
            Toast.makeText(mContext, R.string.txt_phone_void_register_user, Toast.LENGTH_LONG)
                .show()
            return false
        } else if (mPhone.length < 10) {
            Toast.makeText(mContext, R.string.txt_notValid_phone, Toast.LENGTH_LONG)
                .show()
        } else if (mPassword.isEmpty()) {
            Toast.makeText(mContext, R.string.txt_password_void_register_user, Toast.LENGTH_LONG)
                .show()
            return false
        } else if (mRepeatPassword.isEmpty()) {
            Toast.makeText(
                mContext,
                R.string.txt_repeat_password_void_register_user,
                Toast.LENGTH_LONG
            ).show()
        } else if (mPassword.length < 8) {
            Toast.makeText(
                mContext,
                R.string.txt_length_password,
                Toast.LENGTH_LONG
            ).show()
        } else if (mPassword.equals(mRepeatPassword)) {

            val user = User(
                name = mName,
                lastname = mLastName,
                email = mEmail,
                phone = mPhone,
                password = mPassword
            )
            usersProvider.registerUsers(user)?.enqueue(object : Callback<ResponseHttp> {
                override fun onResponse(
                    call: Call<ResponseHttp>,
                    response: Response<ResponseHttp>
                ) {
                    //validamos que el registro se ejecute
                    if (response.body()?.isSuccess == true) {
                        //llamamos la metodo para genrear el token y guardamos la sesion del usuario
                        saveUserSession(response.body()?.data.toString())
                        //una ves el registro echo mandamos al usuario a la vista client
                        goClient()
                    }

                    Toast.makeText(mContext, response.body()?.message, Toast.LENGTH_LONG).show()

                    Log.d(TAG, "Response: ${response}")
                    Log.d(TAG, "Body: ${response.body()}")
                }

                override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                    Log.d(TAG, "Se produjo un error ${t.message}")
                    Toast.makeText(mContext, R.string.txt_There_was_an_error, Toast.LENGTH_LONG)
                        .show()
                }
            })
        } else {
            Toast.makeText(mContext, R.string.txt_repeat_password_validate, Toast.LENGTH_LONG)
                .show()
        }
        return true
    }

    //para generar el token al registro
    override fun saveUserSession(information: String) {
        val sharedPref = mContext?.let { SharedPref(it) }
        val gson = Gson()
        //aqui se almacena toda la informacion del usuario
        val user = gson.fromJson(information, User::class.java)
        sharedPref?.saveSession("user", user)
    }
}