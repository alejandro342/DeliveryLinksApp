package com.alejandro.deliverylinks.modules.client.ui.views.client

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.alejandro.deliverylinks.databinding.ActivityClientProductsListBinding
import com.alejandro.deliverylinks.models.product.Product
import com.alejandro.deliverylinks.models.user.User
import com.alejandro.deliverylinks.modules.restaurant.ui.adapter.AdapterProducts
import com.alejandro.deliverylinks.providers.ProductsProvider
import com.alejandro.deliverylinks.utils.SharedPref
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ClientProductsListActivity : AppCompatActivity() {

    private var mBinding: ActivityClientProductsListBinding? = null
    private var mAdapterProducts: AdapterProducts? = null
    private var mProductsProvider: ProductsProvider? = null
    //obtener usuario de sesion
    private var mSharedPref:SharedPref?=null
    private var mUser:User?=null
    private var mListProduct :ArrayList<Product> = ArrayList()

    val TAG ="ProductsActivity"


    //obtener categoria
    var idCategory : String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityClientProductsListBinding.inflate(layoutInflater)
        val mView: View = mBinding!!.root
        setContentView(mView)
        //obtenemos el parametro para obtener la categoria
        idCategory=intent.getStringExtra("idCategory")
        mSharedPref= SharedPref(this)
        getUserFromSession()
        mProductsProvider = ProductsProvider(mUser?.sessionToken!!)
        mBinding?.rcviewProducts?.layoutManager =LinearLayoutManager(this)
        getProducts()
    }

    fun getUserFromSession() {

        val gson = Gson()
        if (!mSharedPref?.getInformation("user").isNullOrBlank()) {
            //si el usuario esta en session
            mUser = gson.fromJson(mSharedPref?.getInformation("user"), User::class.java)
            //  Log.d("ClientProfileFragment", "Usuario $mUser")
        }
    }
    //traer los productos
    fun getProducts(){
        //obtener por categoria
        mProductsProvider?.getProduct(idCategory!!)?.enqueue(object:Callback<ArrayList<Product>>{
            override fun onResponse(
                call: Call<ArrayList<Product>>,
                response: Response<ArrayList<Product>>
            ) {
                if (response.body() != null){
                    mListProduct= response.body()!!
                    mAdapterProducts = AdapterProducts(this@ClientProductsListActivity,mListProduct)
                    mBinding!!.rcviewProducts.adapter=mAdapterProducts
                }
            }

            override fun onFailure(call: Call<ArrayList<Product>>, t: Throwable) {
                Toast.makeText(this@ClientProductsListActivity, t.message, Toast.LENGTH_SHORT).show()
                Log.d(TAG,"Error: ${t.message}")
            }

        })
    }
}