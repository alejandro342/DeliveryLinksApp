package com.alejandro.deliverylinks.modules.client.ui.views.orders

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
import com.alejandro.deliverylinks.databinding.ActivityRestaurantOrdersDetailBinding
import com.alejandro.deliverylinks.models.orders.Order
import com.alejandro.deliverylinks.models.response.ResponseHttp
import com.alejandro.deliverylinks.models.user.User
import com.alejandro.deliverylinks.modules.client.ui.adapter.AdapterOrderDetailProducts
import com.alejandro.deliverylinks.modules.restaurant.ui.view.RestaurantHomeActivityView
import com.alejandro.deliverylinks.providers.OrdersProvider
import com.alejandro.deliverylinks.providers.UsersProvider
import com.alejandro.deliverylinks.utils.SharedPref
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RestaurantOrdersDetailActivity : AppCompatActivity(), View.OnClickListener {

    private var mBinding: ActivityRestaurantOrdersDetailBinding? = null
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
        mBinding = ActivityRestaurantOrdersDetailBinding.inflate(layoutInflater)
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
        getDeliveryMen()
        configToolbar()
        Log.d(TAG, "Order: ${mOrder.toString()}")
        setInfoText()
        mBinding!!.rcvProductDetailRestaurant.layoutManager = LinearLayoutManager(this)
        mAdapterOrderDetail = AdapterOrderDetailProducts(this, mOrder?.products!!)
        mBinding!!.rcvProductDetailRestaurant.adapter = mAdapterOrderDetail
        getTotal()
        mBinding!!.btnAcceptDelivery.setOnClickListener(this)
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
        mBinding!!.txtDeliveryRestaurantOrderDetail.text =
            "${mOrder?.delivery?.name} ${mOrder?.delivery?.lastname}"
        Log.d(TAG,"Delivery : ${mOrder?.delivery?.name}")
        mBinding!!.txtNameRestaurantOrderDetail.text =
            "${mOrder?.client?.name} ${mOrder?.client?.lastname}"
        mBinding!!.txtDeliverRestaurantOrderDetail.text = mOrder?.address?.address
        mBinding!!.txtDateRestaurantOrderDetail.text = "${mOrder?.timestamp}"
        mBinding!!.txtCurrentStatusRestaurantOrderDetail.text = mOrder?.status
    }

    //obterner rol Delivery
    private fun getDeliveryMen() {
        mUsersProvider?.getDeliveryMen()?.enqueue(object : Callback<ArrayList<User>> {
            override fun onResponse(
                call: Call<ArrayList<User>>,
                response: Response<ArrayList<User>>
            ) {
                if (response.body() != null) {
                    mDeliveryM = response.body()!!
                    val mArrayAdapter = ArrayAdapter<User>(
                        this@RestaurantOrdersDetailActivity,
                        android.R.layout.simple_dropdown_item_1line,
                        mDeliveryM
                    )
                    mBinding!!.SpinnerDeliveryMen.adapter = mArrayAdapter
                    //escuchador a lo que seleccione el usuario
                    mBinding!!.SpinnerDeliveryMen.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                adapterView: AdapterView<*>?,
                                view: View?,
                                position: Int,
                                l: Long
                            ) {
                                idDelivery = mDeliveryM[position].id!!
                                Log.d(TAG, "Id delivery ${idDelivery}")
                            }

                            override fun onNothingSelected(p0: AdapterView<*>?) {}
                        }
                }
            }

            override fun onFailure(call: Call<ArrayList<User>>, t: Throwable) {
                Toast.makeText(
                    this@RestaurantOrdersDetailActivity,
                    "Error ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }

        })
    }

    private fun viewTextButtom() {
        if (mOrder?.status == "PAGADO") {
            mBinding!!.btnAcceptDelivery.visibility = View.VISIBLE
            mBinding!!.SpinnerDeliveryMen.visibility = View.VISIBLE
            mBinding!!.txtDeliveryAvailable.visibility = View.VISIBLE
        }
        if (mOrder?.status != "PAGADO") {
            mBinding!!.textDeliveryRestaurantDetail.visibility = View.VISIBLE
            mBinding!!.txtDeliveryRestaurantOrderDetail.visibility = View.VISIBLE
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
        mBinding!!.txtTotalPriceRestaurantOrderDetail.text = "$${mTotal}"

    }

    //asignar o actualizar estado de orden
    private fun updateOrder() {
        mOrder?.idDelivery = idDelivery
        mOrdersProvider?.updateToDispatchedOrder(mOrder!!)
            ?.enqueue(object : Callback<ResponseHttp> {

                override fun onResponse(
                    call: Call<ResponseHttp>,
                    response: Response<ResponseHttp>
                ) {

                    if (response.body() != null) {
                        if (response.body()?.isSuccess == true) {
                            Toast.makeText(
                                this@RestaurantOrdersDetailActivity,
                                "Repartidor asignado",
                                Toast.LENGTH_SHORT
                            ).show()
                            goToOrders()
                        } else {
                            Toast.makeText(
                                this@RestaurantOrdersDetailActivity,
                                "No se pudo asignar el repartidor",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@RestaurantOrdersDetailActivity,
                            "Sin respuesta del servidor",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                    Toast.makeText(
                        this@RestaurantOrdersDetailActivity,
                        "Error ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun goToOrders() {
        val mIntent = Intent(this, RestaurantHomeActivityView::class.java)
        mIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(mIntent)
    }

    override fun onClick(mItem: View?) {
        when (mItem) {
            mBinding!!.btnAcceptDelivery -> {
                updateOrder()
            }
        }
    }
}