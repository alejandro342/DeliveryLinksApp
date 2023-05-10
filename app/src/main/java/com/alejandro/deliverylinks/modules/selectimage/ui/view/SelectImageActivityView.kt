package com.alejandro.deliverylinks.modules.selectimage.ui.view

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.alejandro.deliverylinks.databinding.ActivitySelectImageViewBinding
import com.alejandro.deliverylinks.models.response.ResponseHttp
import com.alejandro.deliverylinks.models.user.User
import com.alejandro.deliverylinks.modules.selectimage.presenter.SelectImagePresenter
import com.alejandro.deliverylinks.providers.UsersProvider
import com.alejandro.deliverylinks.utils.SharedPref
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class SelectImageActivityView : AppCompatActivity(), View.OnClickListener {

    var mBinding: ActivitySelectImageViewBinding? = null

    //para el manejo de imagenes
    private var mImageFile: File? = null
    private var mSelectImagePresenter: SelectImagePresenter? = null
    var mSharedPref: SharedPref? = null
    var mUser: User? = null
    val TAG = "SelectImageActivityView"

   // var mUsersProvider = UsersProvider()
   var mUsersProvider: UsersProvider? = null //enviar los datos ya con el token

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySelectImageViewBinding.inflate(layoutInflater)
        val mView: View = mBinding!!.root
        setContentView(mView)
        mSharedPref = SharedPref(this)
        mSelectImagePresenter = SelectImagePresenter(this)
        getUserFromSession()
        mUsersProvider= UsersProvider(mUser?.sessionToken) //enviar los datos ya con el token
        mBinding!!.circleImageUser.setOnClickListener(this)
        mBinding!!.btnJumpInfo.setOnClickListener(this)
        mBinding!!.btnConfirmInfo.setOnClickListener(this)
    }

    override fun onClick(options: View?) {
        when (options) {
            mBinding?.circleImageUser -> selectImage()
            mBinding?.btnJumpInfo -> mJumpInfo()
            mBinding?.btnConfirmInfo -> saveImage()
        }
    }

    fun mJumpInfo() {
        mSelectImagePresenter?.goClientHome()
    }

    fun saveUserSession(information: String) {
        val gson = Gson()
        //aqui se almacena toda la informacion del usuario
        val user = gson.fromJson(information, User::class.java)
        mSharedPref?.saveSession("user", user)
        mJumpInfo()
    }
    private fun saveImage() {
        if (mImageFile != null && mUser != null ){
            mUsersProvider?.updateUser(mImageFile!!, mUser!!)?.enqueue(object : Callback<ResponseHttp> {
                override fun onResponse(call: Call<ResponseHttp>, response: Response<ResponseHttp>) {

                    Log.d(TAG, "RESPONSE : ${response}")
                    Log.d(TAG, "BODY : ${response.body()}")

                    saveUserSession(response.body()?.data.toString())
                }

                override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                    Log.d(TAG, "Error : ${t.message}")
                    Toast.makeText(
                        this@SelectImageActivityView,
                        "Error: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }

            })
        }else{
            Toast.makeText(this, "La imagen ni los datos de sesion deben ser nulos", Toast.LENGTH_SHORT).show()
        }
    }

    fun getUserFromSession() {

        val gson = Gson()

        if (!mSharedPref?.getInformation("user").isNullOrBlank()) {
            //si el ususario esta en session
            mUser = gson.fromJson(mSharedPref?.getInformation("user"), User::class.java)
        }
    }

    //para el manejo de imagenes
    private val mStarImageForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            //data que devuelve la funcion select de la imagen que seleccione el usuario
            val data = result.data
            //se crea un archivo
            if (resultCode == Activity.RESULT_OK) {
                val mFieldUri = data?.data
                mImageFile = File(mFieldUri?.path) //archivo qeu se guardara en el servidor
                mBinding!!.circleImageUser.setImageURI(mFieldUri)
            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Proceso cancelado", Toast.LENGTH_LONG).show()
            }
        }

    //para el manejo de imagenes
    fun selectImage() {
        ImagePicker.with(this)
            //para cortar la imagen
            .crop()
            //comprime la image
            .compress(1024)
            //maximo para imagenes
            .maxResultSize(1080, 1080)
            .createIntent { mIntent ->
                mStarImageForResult.launch(mIntent)

            }
    }
}