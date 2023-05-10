package com.alejandro.deliverylinks.modules.client.ui.views.address

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.alejandro.deliverylinks.R
import com.alejandro.deliverylinks.databinding.ActivityClientAddressListBinding
import com.alejandro.deliverylinks.models.address.Address
import com.alejandro.deliverylinks.models.product.Product
import com.alejandro.deliverylinks.models.user.User
import com.alejandro.deliverylinks.modules.client.ui.adapter.AdapterAddress
import com.alejandro.deliverylinks.modules.client.ui.views.payments.mercado_pago.view.form.ClientPaymentsActivity
import com.alejandro.deliverylinks.modules.client.ui.views.payments.payment_method.ClientPaymentMethodActivity
import com.alejandro.deliverylinks.providers.AddressProvider
import com.alejandro.deliverylinks.providers.OrdersProvider
import com.alejandro.deliverylinks.utils.SharedPref
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ClientAddressListActivity : AppCompatActivity(), View.OnClickListener {

    var mBinding: ActivityClientAddressListBinding? = null
    var mToolbar: Toolbar? = null

    //listar Direcciones del usuario
    var mAdapterAddress: AdapterAddress? = null
    var mSharedPref: SharedPref? = null
    var mAddressProvider: AddressProvider? = null
    var mUser: User? = null
    var mAddressList = ArrayList<Address>()

    //crear orders
    var mOrdersProvider: OrdersProvider? = null
    var mSelectProduct = ArrayList<Product>()

    //validar que seleccione una direccion
    var mGson=Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityClientAddressListBinding.inflate(layoutInflater)
        val mView: View = mBinding!!.root
        setContentView(mView)
        //listar Direcciones del usuario
        mSharedPref= SharedPref(this)
        //crear orders
        getProductoSharedPref()

        //configuracion del toolbar
        mToolbar = findViewById(R.id.toolbar)
        mToolbar?.title = "Direcciones"
        mToolbar?.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
        setSupportActionBar(mToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mBinding!!.fabAddressCreateClient.setOnClickListener(this)
        mBinding!!.btnContinueAddressClient.setOnClickListener(this)
        mBinding!!.rcvAddressClientList.layoutManager = LinearLayoutManager(this)

        //listar Direcciones del usuario
        getUserFromSession()
        mAddressProvider = AddressProvider(mUser?.sessionToken!!)
        //crear orders
        mOrdersProvider = OrdersProvider(mUser?.sessionToken!!)
        getAddress()
    }
    //validar que el usuario seleccione una direccion
    private fun getAddressFromSession(){
        if (!mSharedPref?.getInformation("address").isNullOrBlank()){
            val a = mGson.fromJson(mSharedPref?.getInformation("address"),Address::class.java) //si existe una direccion
            createOrder(a.id!!)
        }
        else{
            Toast.makeText(this, "Seleccione una direccion para continuar", Toast.LENGTH_SHORT).show()
        }
    }
    //crear orders
    private fun createOrder(idAddress:String){//para obtener la direccion agregamos un parametro a recibir
        goToPayment()
    }
    // crear orders y obtener los producto guardados en SharedPref
    @SuppressLint("SetTextI18n")
    private fun getProductoSharedPref() {
        //validar si hay un producto
        if (!mSharedPref?.getInformation("order")
                .isNullOrBlank()
        ) {//si existe una orden en SHARED PREF
            //transformar una lista tipo json a tipo product
            val type = object : TypeToken<ArrayList<Product>>() {}.type
            mSelectProduct = mGson.fromJson(mSharedPref?.getInformation("order"), type)
        }
    }

    //ir a formulario de pago
    private fun goToPayment(){
        val mIntent = Intent(this, ClientPaymentMethodActivity::class.java)
        startActivity(mIntent)
    }

//desmarcar direccion elejida por el usuario y obtener nueva direccion
    fun resetValue(position:Int){
    val viewHolder = mBinding!!.rcvAddressClientList.findViewHolderForAdapterPosition(position)//Una direccion
    val view = viewHolder?.itemView
    val imageCheck = view?.findViewById<ImageView>(R.id.img_Item_Check_Address)
    imageCheck?.visibility = View.GONE
    }

    //listar Direcciones del usuario
    private fun getAddress(){
        mAddressProvider?.getAddress(mUser?.id!!)?.enqueue(object :Callback<ArrayList<Address>>{
            override fun onResponse(
                call: Call<ArrayList<Address>>,
                response: Response<ArrayList<Address>>
            ) {
                if (response.body()!= null){
                    mAddressList= response.body()!!

                    mAdapterAddress = AdapterAddress(this@ClientAddressListActivity,mAddressList)
                    mBinding!!.rcvAddressClientList.adapter =mAdapterAddress
                }
            }

            override fun onFailure(call: Call<ArrayList<Address>>, t: Throwable) {
                Toast.makeText(this@ClientAddressListActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }

        })
    }

    //listar Direcciones del usuario
    fun getUserFromSession() {

        val gson = Gson()
        if (!mSharedPref?.getInformation("user").isNullOrBlank()) {
            //si el usuario esta en session
            mUser = gson.fromJson(mSharedPref?.getInformation("user"), User::class.java)

        }
    }

    fun goToCreateAddres() {
        val mIntent = Intent(this, ClientAddressCreateActivity::class.java)
        startActivity(mIntent)
    }

    override fun onClick(mIntemView: View?) {
        when (mIntemView) {
            mBinding!!.fabAddressCreateClient -> goToCreateAddres()
            mBinding!!.btnContinueAddressClient -> getAddressFromSession()
        }
    }
}