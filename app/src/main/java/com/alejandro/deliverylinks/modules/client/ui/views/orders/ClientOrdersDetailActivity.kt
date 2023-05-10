package com.alejandro.deliverylinks.modules.client.ui.views.orders

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.alejandro.deliverylinks.R
import com.alejandro.deliverylinks.databinding.ActivityClientOrdersDetailBinding
import com.alejandro.deliverylinks.models.orders.Order
import com.alejandro.deliverylinks.modules.client.ui.adapter.AdapterOrderDetailProducts
import com.alejandro.deliverylinks.modules.client.ui.views.map.ClientOrdersMapActivity
import com.google.gson.Gson

class ClientOrdersDetailActivity : AppCompatActivity(), View.OnClickListener {

    private var mBinding: ActivityClientOrdersDetailBinding? = null
    val TAG = "OrdersDetail"

    //mostrar el detalle de la orden con sus productos
    var mOrder: Order? = null
    val mGson = Gson()
    var mToolbar: Toolbar? = null
    var mAdapterOrderDetail: AdapterOrderDetailProducts? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityClientOrdersDetailBinding.inflate(layoutInflater)
        val mView: View = mBinding!!.root
        setContentView(mView)
        //mostrar el detalle de la orden con sus producto
        mOrder = mGson.fromJson(intent.getStringExtra("order"), Order::class.java)
        configToolbar()
        Log.d(TAG, "Order: ${mOrder.toString()}")
        setInfoText()
        mBinding!!.rcvProductDetailClient.layoutManager = LinearLayoutManager(this)
        mAdapterOrderDetail = AdapterOrderDetailProducts(this, mOrder?.products!!)
        mBinding!!.rcvProductDetailClient.adapter = mAdapterOrderDetail
        getTotal()
        mBinding!!.btnGoToMapClient.setOnClickListener(this)
        if (mOrder?.status == "EN CAMINO") {
            mBinding!!.btnGoToMapClient.visibility = View.VISIBLE
        }
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
        mBinding!!.txtNameClientOrderDetail.text =
            "${mOrder?.client?.name} ${mOrder?.client?.lastname}"
        mBinding!!.txtDeliverClientOrderDetail.text = mOrder?.address?.address
        mBinding!!.txtDateClientOrderDetail.text = "${mOrder?.timestamp}"
        mBinding!!.txtCurrentStatusClientOrderDetail.text = mOrder?.status

    }

    //mostrar el detalle de la orden con sus producto total
    @SuppressLint("SetTextI18n")
    private fun getTotal() {
        var mTotal = 0.0
        //recorrer los productos que vienen en la orden
        for (p in mOrder?.products!!) {
            mTotal = mTotal + (p.price * p.quantity!!)
        }
        mBinding!!.txtTotalPriceClientOrderDetail.text = "$${mTotal}"

    }

    private fun goToMap() {
        val mIntent = Intent(this, ClientOrdersMapActivity::class.java)
        mIntent.putExtra("order", mOrder?.toJson())
        startActivity(mIntent)
    }

    override fun onClick(mIntemView: View?) {
        when (mIntemView) {
            mBinding!!.btnGoToMapClient -> goToMap()
        }
    }
}