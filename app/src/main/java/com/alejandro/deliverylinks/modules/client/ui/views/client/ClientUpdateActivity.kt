package com.alejandro.deliverylinks.modules.client.ui.views.client

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.alejandro.deliverylinks.R
import com.alejandro.deliverylinks.databinding.ActivityClientUpdateBinding
import com.alejandro.deliverylinks.models.response.ResponseHttp
import com.alejandro.deliverylinks.models.user.User
import com.alejandro.deliverylinks.modules.client.presenter.ClientHomePresenter
import com.alejandro.deliverylinks.providers.UsersProvider
import com.alejandro.deliverylinks.utils.SharedPref
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


class ClientUpdateActivity : AppCompatActivity(), View.OnClickListener {

    private var mBinding: ActivityClientUpdateBinding? = null
    private var mClientHomePresenter: ClientHomePresenter? = null
    private var sharedPref: SharedPref? = null
    private var mUser: User? = null
    var mToolbar: Toolbar? = null

    //para el manejo de imagenes
    private var mImageFile: File? = null

    //var mUsersProvider = UsersProvider()
    var mUsersProvider: UsersProvider? = null //enviar los datos ya con el token
    var TAG = "UpdateProfileActivityView"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityClientUpdateBinding.inflate(layoutInflater)
        val mView: View = mBinding!!.root
        setContentView(mView)
        mToolbar = mView.findViewById(R.id.toolbar)
        mClientHomePresenter = ClientHomePresenter(this)
        sharedPref = SharedPref(this)

        mToolbar = mView.findViewById(R.id.toolbar)
        mToolbar?.title="Editar perfil"
        mToolbar?.setTitleTextColor(ContextCompat.getColor(this,R.color.white))
        setSupportActionBar(mToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        getUserFromSession()
        mUsersProvider = UsersProvider(mUser?.sessionToken) //enviar los datos ya con el token
        setDataProfile()
        mBinding?.btnSaveUpdateProfile?.setOnClickListener(this)
        mBinding?.btnCancelUpdate?.setOnClickListener(this)
        mBinding?.ImgUpdateImageProfile?.setOnClickListener(this)

    }

    override fun onClick(mItem: View?) {
        when (mItem) {

            mBinding?.ImgUpdateImageProfile -> {
                selectImage()
            }
            mBinding?.btnSaveUpdateProfile -> {
                updateDataProfile()
            }
            mBinding?.btnCancelUpdate -> {
                mClientHomePresenter?.goClientProfileHome()
            }
        }
    }

    fun getUserFromSession() {

        val gson = Gson()
        if (!sharedPref?.getInformation("user").isNullOrBlank()) {
            //si el usuario esta en session
            mUser = gson.fromJson(sharedPref?.getInformation("user"), User::class.java)
            //  Log.d("ClientProfileFragment", "Usuario $mUser")
        }
    }

    fun setDataProfile() {

        mBinding?.txtUpdateNameProfile?.setText(mUser?.name)
        mBinding?.txtUpdateLastNameUser?.setText(mUser?.lastname)
        mBinding!!.txtUpdatePhoneProfile.setText(mUser?.phone)
        if (!mUser?.image.isNullOrBlank()) {
            Picasso.get()
                .load(mUser?.image)
                .into(mBinding?.ImgUpdateImageProfile)
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
                mBinding!!.ImgUpdateImageProfile.setImageURI(mFieldUri)
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

    private fun updateDataProfile() {

        mUser?.name = mBinding?.txtUpdateNameProfile?.text.toString()
        mUser?.lastname = mBinding?.txtUpdateLastNameUser?.text.toString()
        mUser?.phone = mBinding?.txtUpdatePhoneProfile?.text.toString()

        if (mImageFile != null) {

            mUsersProvider?.updateUser(mImageFile!!, mUser!!)?.enqueue(object :
                Callback<ResponseHttp> {
                override fun onResponse(
                    call: Call<ResponseHttp>,
                    response: Response<ResponseHttp>
                ) {

                    Log.d(TAG, "RESPONSE : ${response}")
                    Log.d(TAG, "BODY : ${response.body()}")

                    Toast.makeText(
                        this@ClientUpdateActivity,
                        response.body()?.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    if (response.body()?.isSuccess == true) {
                        saveUserSession(response.body()?.data.toString())
                    }
                }

                override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                    Log.d(TAG, "Error : ${t.message}")
                    Toast.makeText(
                        this@ClientUpdateActivity,
                        "Error: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }

            })
        } else {
            mUsersProvider?.updateWithoutImage(mUser!!)?.enqueue(object :
                Callback<ResponseHttp> {
                override fun onResponse(
                    call: Call<ResponseHttp>,
                    response: Response<ResponseHttp>
                ) {

                    Log.d(TAG, "RESPONSE : ${response}")
                    Log.d(TAG, "BODY : ${response.body()}")

                    Toast.makeText(
                        this@ClientUpdateActivity,
                        response.body()?.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    if (response.body()?.isSuccess == true) {
                        saveUserSession(response.body()?.data.toString())
                    }
                }

                override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                    Log.d(TAG, "Error : ${t.message}")
                    Toast.makeText(
                        this@ClientUpdateActivity,
                        "Error: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
        }
    }

    fun saveUserSession(information: String) {
        val gson = Gson()
        //aqui se almacena toda la informacion del usuario
        val user = gson.fromJson(information, User::class.java)
        sharedPref?.saveSession("user", user)
    }
}