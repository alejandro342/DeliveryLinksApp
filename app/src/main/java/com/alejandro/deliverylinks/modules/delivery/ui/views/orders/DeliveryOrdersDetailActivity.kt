package com.alejandro.deliverylinks.modules.delivery.ui.views.orders

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.alejandro.deliverylinks.R
import com.alejandro.deliverylinks.databinding.ActivityDeliveryOrdersDetailBinding
import com.alejandro.deliverylinks.models.orders.Order
import com.alejandro.deliverylinks.models.response.ResponseHttp
import com.alejandro.deliverylinks.models.user.User
import com.alejandro.deliverylinks.modules.client.ui.adapter.AdapterOrderDetailProducts
import com.alejandro.deliverylinks.modules.delivery.ui.views.DeliveryHomeActivityView
import com.alejandro.deliverylinks.modules.delivery.ui.views.orders.map.DeliveryOrdersMapActivity
import com.alejandro.deliverylinks.modules.restaurant.ui.view.RestaurantHomeActivityView
import com.alejandro.deliverylinks.providers.OrdersProvider
import com.alejandro.deliverylinks.providers.UsersProvider
import com.alejandro.deliverylinks.utils.SharedPref
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DeliveryOrdersDetailActivity : AppCompatActivity(),View.OnClickListener {

    private var mBinding: ActivityDeliveryOrdersDetailBinding? = null
    val TAG = "OrdersDetail"

    //mostrar el detalle de la orden con sus productos
    var mOrder: Order? = null
    val mGson = Gson()
    var mToolbar: Toolbar? = null
    var mAdapterOrderDetail: AdapterOrderDetailProducts? = null

    //obterner rol Delivery
    var mUsersProvider: UsersProvider? = null
    var mUser: User? = null
    var mSharedPref: SharedPref? = null
    private var mDeliveryM = ArrayList<User>()

    //actualizar estado de orden
    var mOrdersProvider: OrdersProvider? = null

    //para asignar delivery
    var idDelivery = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityDeliveryOrdersDetailBinding.inflate(layoutInflater)
        val mView: View = mBinding!!.root
        setContentView(mView)
        //obterner rol Delivery
        mSharedPref = SharedPref(this)

        //mostrar el detalle de la orden con sus producto
        mOrder = mGson.fromJson(intent.getStringExtra("order"), Order::class.java)
        //obterner rol Delivery
        getUserFromSession()
        mUsersProvider = UsersProvider(mUser?.sessionToken)
        mOrdersProvider = OrdersProvider(mUser?.sessionToken!!)

        configToolbar()
        Log.d(TAG, "Order: ${mOrder.toString()}")
        setInfoText()
        mBinding!!.rcvProductDetailDelivery.layoutManager = LinearLayoutManager(this)
        mAdapterOrderDetail = AdapterOrderDetailProducts(this, mOrder?.products!!)
        mBinding!!.rcvProductDetailDelivery.adapter = mAdapterOrderDetail
        getTotal()
        mBinding!!.btnStartDelivery.setOnClickListener(this)
        mBinding!!.btnGoToMap.setOnClickListener(this)
        viewTextButtom()
    }

    private fun configToolbar() {
        mToolbar = findViewById(R.id.toolbar)
        mToolbar?.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
        mToolbar?.title = "Order #${mOrder?.id}"
        setSupportActionBar(mToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    //mostrar el detalle de la orden con sus productos
    @SuppressLint("SetTextI18n")
    private fun setInfoText() {
        mBinding!!.txtNameDeliveryOrderDetail.text =
            "${mOrder?.client?.name} ${mOrder?.client?.lastname}"
        mBinding!!.txtDeliverDeliveryOrderDetail.text = mOrder?.address?.address
        mBinding!!.txtDateDeliveryOrderDetail.text = "${mOrder?.timestamp}"
        mBinding!!.txtCurrentStatusDeliveryOrderDetail.text = mOrder?.status
    }

    private fun viewTextButtom() {
        if (mOrder?.status == "DESPACHADO") {
            mBinding!!.btnStartDelivery.visibility = View.VISIBLE
        }
        if (mOrder?.status == "EN CAMINO") {
            mBinding!!.btnGoToMap.visibility = View.VISIBLE
        }
    }

    //obterner rol Delivery datos del usuario sesion
    fun getUserFromSession() {
        val gson = Gson()

        if (!mSharedPref?.getInformation("user").isNullOrBlank()) {
            //si el usuario esta en session
            mUser = gson.fromJson(mSharedPref?.getInformation("user"), User::class.java)
        }
    }

    //mostrar el detalle de la orden con sus producto total
    @SuppressLint("SetTextI18n")
    private fun getTotal() {
        var mTotal = 0.0
        //recorrer los productos que vienen en la orden
        for (p in mOrder?.products!!) {
            mTotal = mTotal + (p.price * p.quantity!!)
        }
        mBinding!!.txtTotalPriceDeliveryOrderDetail.text = "$${mTotal}"

    }

    //asignar o actualizar estado de orden a en camino
    private fun updateOrder() {
        //actualizar orden a en camino
        mOrdersProvider?.updateToOnTheWay(mOrder!!)
            ?.enqueue(object : Callback<ResponseHttp> {

                override fun onResponse(
                    call: Call<ResponseHttp>,
                    response: Response<ResponseHttp>
                ) {

                    if (response.body() != null) {
                        if (response.body()?.isSuccess == true) {
                            Toast.makeText(
                                this@DeliveryOrdersDetailActivity,
                                "Entrega iniciada",
                                Toast.LENGTH_SHORT
                            ).show()
                            goToMap()
                        } else {
                            Toast.makeText(
                                this@DeliveryOrdersDetailActivity,
                                "No se pudo asignar el repartidor",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@DeliveryOrdersDetailActivity,
                            "Sin respuesta del servidor",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                    Toast.makeText(
                        this@DeliveryOrdersDetailActivity,
                        "Error ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun goToMap() {
        val mIntent = Intent(this, DeliveryOrdersMapActivity::class.java)
        mIntent.putExtra("order",mOrder?.toJson()) //mandamos los datos de la orden a la vista DeliveryOrdersMap
        startActivity(mIntent)
    }

    override fun onClick(mItem: View?) {
        when (mItem) {
            mBinding!!.btnStartDelivery -> {
                updateOrder()
            }
            mBinding!!.btnGoToMap ->{goToMap()}
        }
    }
}