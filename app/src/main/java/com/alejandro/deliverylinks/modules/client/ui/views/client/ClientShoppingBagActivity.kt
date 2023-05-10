package com.alejandro.deliverylinks.modules.client.ui.views.client

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.alejandro.deliverylinks.R
import com.alejandro.deliverylinks.databinding.ActivityClientShoppingBagBinding
import com.alejandro.deliverylinks.models.product.Product
import com.alejandro.deliverylinks.modules.client.ui.adapter.AdapterShoppingBag
import com.alejandro.deliverylinks.modules.client.ui.views.address.ClientAddressListActivity
import com.alejandro.deliverylinks.utils.SharedPref
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ClientShoppingBagActivity : AppCompatActivity() {

    var mBinding: ActivityClientShoppingBagBinding? = null
    var mToolbar:Toolbar?=null

    var mAdapterShoppingBag:AdapterShoppingBag?=null
    var mSharedPref:SharedPref?=null
    var mGson=Gson()

    var mSelectProduct = ArrayList<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityClientShoppingBagBinding.inflate(layoutInflater)
        val mView: View = mBinding!!.root
        setContentView(mView)

        mSharedPref= SharedPref(this)
        //configuracion del toolbar
        mToolbar= findViewById(R.id.toolbar)
        mToolbar?.title="Tu orden"
        mToolbar?.setTitleTextColor(ContextCompat.getColor(this,R.color.white))
        setSupportActionBar(mToolbar)
        mBinding!!.btnAcceptShopping.setOnClickListener{goToAddressList()}
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mBinding!!.rcvShoppingProductsBag.layoutManager=LinearLayoutManager(this)

        getProductoSharedPref()

    }
    fun goToAddressList(){
        //validar que la lista de productos no este vacia
        if (mBinding!!.rcvShoppingProductsBag.adapter !=null){
            if (mBinding!!.rcvShoppingProductsBag.adapter?.itemCount ==0){
                Toast.makeText(this, "No tiene productos en la lista", Toast.LENGTH_SHORT).show()
            }else{
                val mIntent=Intent(this,ClientAddressListActivity::class.java)
                startActivity(mIntent)
            }
        }

    }

    //para obtener el total que viene del adapter
    fun setTotal(total:Double){
        mBinding!!.txtPriceTotalShopping.text = "${total}"
    }
    //obtener los producto guardados en SharedPref
    @SuppressLint("SetTextI18n")
    private fun getProductoSharedPref() {
        //validar si hay un producto
        if (!mSharedPref?.getInformation("order")
                .isNullOrBlank()
        ) {//si existe una orden en SHARED PREF
            //transformar una lista tipo json a tipo product
            val type = object : TypeToken<ArrayList<Product>>() {}.type
            mSelectProduct = mGson.fromJson(mSharedPref?.getInformation("order"), type)
            //al traer los datos los mostramos en el adapter
            mAdapterShoppingBag = AdapterShoppingBag(this,mSelectProduct)
            mBinding!!.rcvShoppingProductsBag.adapter=mAdapterShoppingBag

        }
    }
}