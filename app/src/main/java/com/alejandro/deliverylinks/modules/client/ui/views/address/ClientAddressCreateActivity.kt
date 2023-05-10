package com.alejandro.deliverylinks.modules.client.ui.views.address

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.alejandro.deliverylinks.R
import com.alejandro.deliverylinks.databinding.ActivityClientAddressCreateBinding
import com.alejandro.deliverylinks.models.address.Address
import com.alejandro.deliverylinks.models.response.ResponseHttp
import com.alejandro.deliverylinks.models.user.User
import com.alejandro.deliverylinks.modules.client.ui.views.address.map.ClientAddressMapActivity
import com.alejandro.deliverylinks.providers.AddressProvider
import com.alejandro.deliverylinks.utils.SharedPref
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ClientAddressCreateActivity : AppCompatActivity(), View.OnClickListener {

    val TAG = "AddressCreate"
    private var mBinding: ActivityClientAddressCreateBinding? = null
    private var mToolbar: Toolbar? = null

    //para la longitud y latitud
    var mAddressLat = 0.0
    var mAddressLng = 0.0

    //para crear dirección
    var mAddressProvider: AddressProvider? = null
    var mSharedPref:SharedPref?=null
    var mUser:User?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityClientAddressCreateBinding.inflate(layoutInflater)
        val mView: View = mBinding!!.root
        setContentView(mView)

        //para crear dirección
        mSharedPref= SharedPref(this)
        getUserFromSession()
        mAddressProvider= AddressProvider(mUser?.sessionToken!!)

        //Configuración del toolbar
        mToolbar = findViewById(R.id.toolbar)
        mToolbar?.title = "Nueva dirección"
        mToolbar?.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
        setSupportActionBar(mToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mBinding!!.txtPointReferenceClient.setOnClickListener(this)
        mBinding!!.btnSaveAddressClient.setOnClickListener(this)
    }
    //para crear dirección
    fun getUserFromSession() {

        val gson = Gson()
        if (!mSharedPref?.getInformation("user").isNullOrBlank()) {
            //si el usuario esta en session
            mUser = gson.fromJson(mSharedPref?.getInformation("user"), User::class.java)

        }
    }

    //mostrar datos en los campos obtener los datos de la localizacion del mapa
    @SuppressLint("SetTextI18n")
    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            //traer los datos
            if (result.resultCode == Activity.RESULT_OK) {
                val mData = result.data
                val mCity = mData?.getStringExtra("city")
                val mAddress = mData?.getStringExtra("address")
                val mCountry = mData?.getStringExtra("country")
                mAddressLat = mData?.getDoubleExtra("lat", 0.0)!!
                mAddressLng = mData?.getDoubleExtra("lng", 0.0)!!

                //obtener y mostrar los datos en el campo
                mBinding!!.txtPointReferenceClient.setText("$mAddress $mCity")

                Log.d(TAG, "City: $mCity")
                Log.d(TAG, "Address: $mAddress")
                Log.d(TAG, "Country: $mCountry")
                Log.d(TAG, "Lat: $mAddressLat")
                Log.d(TAG, "Lng: $mAddressLng")

            }
        }

    private fun goToAddressMap() {
        val mIntent = Intent(this, ClientAddressMapActivity::class.java)
        resultLauncher.launch(mIntent)
    }

    override fun onClick(mIntenView: View?) {
        when (mIntenView) {
            mBinding!!.txtPointReferenceClient -> goToAddressMap()
            mBinding!!.btnSaveAddressClient -> createAddress()
        }
    }

    //mandar peticion para guardar datos de la direccion
    //crear dideccion obtener los datos de la localizacion del mapa
    private fun createAddress() {
        val address = mBinding!!.txtAddressClient.text.toString()
        val neighbor = mBinding!!.txtColonyClient.text.toString()

        if (isValidForm(address, neighbor)) {
            //petición a nodeJS
            //para crear dirección
            val addressModel = Address(
                address= address,
                neighborhood = neighbor,
                idUser = mUser?.id!!,
                lat = mAddressLat,
                lng = mAddressLng
            )
            mAddressProvider?.createAddress(addressModel)?.enqueue(object :Callback<ResponseHttp>{
                override fun onResponse(
                    call: Call<ResponseHttp>,
                    response: Response<ResponseHttp>
                ) {
                    if(response.body()!=null){
                        Toast.makeText(this@ClientAddressCreateActivity, response.body()?.message, Toast.LENGTH_SHORT).show()
                        //crear dirección y enviarlo a la vista donde se ven las direcciones
                        goToAddressList()
                    }
                    else{
                        Toast.makeText(this@ClientAddressCreateActivity, "Error en la peticion", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                    Toast.makeText(this@ClientAddressCreateActivity, "Error : ${t.message}", Toast.LENGTH_SHORT).show()
                }

            })
        }
    }
    //crear dirección y enviarlo a la vista donde se ven las direcciones
    fun goToAddressList(){
        val mIntent= Intent(this,ClientAddressListActivity::class.java)
        startActivity(mIntent)
    }

    private fun isValidForm(address: String, neighbor: String): Boolean {
        if (address.isNullOrBlank()) {
            Toast.makeText(this, R.string.txt_toast_address_void, Toast.LENGTH_SHORT).show()
            return false
        }
        if (neighbor.isNullOrBlank()) {
            Toast.makeText(this, R.string.txt_toast_colonia_void, Toast.LENGTH_SHORT).show()
            return false
        }
        if (mAddressLat == 0.0) {
            Toast.makeText(this, R.string.txt_toast_lat_void, Toast.LENGTH_SHORT).show()
            return false
        }
        if (mAddressLng == 0.0) {
            Toast.makeText(this, R.string.txt_toast_long_void, Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}