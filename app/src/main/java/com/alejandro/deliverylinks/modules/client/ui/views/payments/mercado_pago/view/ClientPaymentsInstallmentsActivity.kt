package com.alejandro.deliverylinks.modules.client.ui.views.payments.mercado_pago.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.alejandro.deliverylinks.R
import com.alejandro.deliverylinks.databinding.ActivityClientPaymentsInstallmentsBinding
import com.alejandro.deliverylinks.models.address.Address
import com.alejandro.deliverylinks.models.mercadopago.MercadoPagoInstallments
import com.alejandro.deliverylinks.models.mercadopago.MercadoPagoPayment
import com.alejandro.deliverylinks.models.mercadopago.Payer
import com.alejandro.deliverylinks.models.orders.Order
import com.alejandro.deliverylinks.models.product.Product
import com.alejandro.deliverylinks.models.response.ResponseHttp
import com.alejandro.deliverylinks.models.user.User
import com.alejandro.deliverylinks.modules.client.ui.views.payments.mercado_pago.view.status.ClientPaymentsStatusActivity
import com.alejandro.deliverylinks.providers.mercadopago.MercadoPagoProvider
import com.alejandro.deliverylinks.providers.payment.PaymentsProvider
import com.alejandro.deliverylinks.utils.MyProgressBar
import com.alejandro.deliverylinks.utils.SharedPref
import com.github.ybq.android.spinkit.sprite.Sprite
import com.github.ybq.android.spinkit.style.WanderingCubes
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ClientPaymentsInstallmentsActivity : AppCompatActivity(), View.OnClickListener,MyProgressBar.progress {

    var mBinding: ActivityClientPaymentsInstallmentsBinding? = null
    var TAG = "ClientPaymentsI"

    //obtener cuotas
    private var mMercadoPagoProvider = MercadoPagoProvider()

    //realizar pago
    private var mPaymentsProvider: PaymentsProvider? = null

    //pago y obtener los datos del usuario
    var mUser: User? = null

    //obtener datos de dirección del  usuario
    var address: Address? = null

    //para el paymentMethodId
    var paymentMethodId = ""
    var paymentTypeId = ""
    var issuerid = ""

    //para recibir los datos de la otra actividad
    private var cardToken = ""
    private var firstSixDigits = ""

    //obtener el total a pagar
    private var mTotal = 0.0
    var mSharedPref: SharedPref? = null
    private var mSelectProduct = ArrayList<Product>()
    var mGson = Gson()

    //seleccionar cuota
    var mInstallmentsSelect = "" //cuota seleccionada

    //progressDialog
    private var mSpinkitProgress: Sprite? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityClientPaymentsInstallmentsBinding.inflate(layoutInflater)
        val mView: View = mBinding!!.root
        setContentView(mView)

        mSpinkitProgress = WanderingCubes()
        mBinding!!.progressBarSpinKit?.setIndeterminateDrawable(mSpinkitProgress)
        mSharedPref = SharedPref(this)
        getUserFromSession()//obtener datos de session del usuario
        getAddressFromSession()//obtener datos de dirección del usuario
        //obtenemos los datos de sesión
        mPaymentsProvider = PaymentsProvider(mUser?.sessionToken!!)
        //para recibir los datos de la otra actividad
        cardToken = intent.getStringExtra("cardToken").toString()
        firstSixDigits = intent.getStringExtra("firstSixDigits").toString()
        mBinding!!.btnConfirmPayInstallments.setOnClickListener(this)
        getProductoSharedPref()
        getInstallments()
    }

    private fun createPayment() {
        //obtenemos los productos del usuario
        val order = Order(
            products = mSelectProduct,
            idClient = mUser?.id!!,
            idAddress = address?.id!!
        )
        //objeto payer
        val payer=Payer(email=mUser?.email!!)
        val mercadoPagoPayment = MercadoPagoPayment(
            order = order,
            token = cardToken,//obtener token de la tarjeta
            description = "Appdelivery",//solo texto de referencia
            paymentMethodId =paymentMethodId,
            paymentTypeId=paymentTypeId,
            issuerId=issuerid,
            payer = payer,
            transactionAmount = mTotal,
            installments =mInstallmentsSelect.toInt()
        )
        showProgressBar()
        //recibimos el objeto de Mercado Pago payment
        mPaymentsProvider?.createPayments(mercadoPagoPayment)?.enqueue(object :Callback<ResponseHttp>{
            override fun onResponse(call: Call<ResponseHttp>, response: Response<ResponseHttp>) {
               hideProgressBar()
                if (response.body() !=null){
                    //si la respuesta es exitosa removemos los productos de la orden
                    if (response.body()?.isSuccess==true){
                        mSharedPref?.remove("order")
                    }
                    Log.d(TAG,"Response : $response")
                    Log.d(TAG,"Body ${response.body()}")
                    Toast.makeText(this@ClientPaymentsInstallmentsActivity, response.body()?.message, Toast.LENGTH_SHORT).show()
            //obtener los datos del response
                    val status = response.body()?.data?.get("status")?.asString
                    val lastFour=response.body()?.data?.get("card")?.asJsonObject?.get("last_four_digits")?.asString
             goToPaymentsStatus(paymentMethodId,status!!,lastFour!!)
                }else{
                    goToPaymentsStatus(paymentMethodId,"denied","")
                    Toast.makeText(this@ClientPaymentsInstallmentsActivity, R.string.txt_no_successful_response, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
               hideProgressBar()
                Toast.makeText(this@ClientPaymentsInstallmentsActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun getInstallments() {
        mMercadoPagoProvider.getInstallments(firstSixDigits, "$mTotal")
            ?.enqueue(object : Callback<JsonArray> {
                override fun onResponse(
                    call: Call<JsonArray>,
                    response: Response<JsonArray>
                ) {
                    if (response.body() != null) {
                        val mJsonInstallments =
                            response.body()!!.get(0).asJsonObject.get("payer_costs").asJsonArray
                        val type = object : TypeToken<ArrayList<MercadoPagoInstallments>>() {}.type
                        val mInstallments = mGson.fromJson<ArrayList<MercadoPagoInstallments>>(
                            mJsonInstallments,
                            type
                        )
                        //obtener los datos de paymentsMethodId
                        paymentMethodId= response.body()?.get(0)?.asJsonObject?.get("payment_method_id")?.asString!!
                        paymentTypeId=response.body()?.get(0)?.asJsonObject?.get("payment_type_id")?.asString!!
                        issuerid= response.body()?.get(0)?.asJsonObject?.get("issuer")?.asJsonObject?.get("id")?.asString!!
                        Log.d(TAG, "response : ${response}")
                        Log.d(TAG, "installments: ${mInstallments}")
                        val mArrayAdapter = ArrayAdapter<MercadoPagoInstallments>(
                            this@ClientPaymentsInstallmentsActivity,
                            android.R.layout.simple_dropdown_item_1line,
                            mInstallments
                        )
                        mBinding!!.SpinnerInstallment.adapter = mArrayAdapter
                        //escuchador a lo que seleccione el usuario
                        mBinding!!.SpinnerInstallment.onItemSelectedListener =
                            object : AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(
                                    adapterView: AdapterView<*>?,
                                    view: View?,
                                    position: Int,
                                    l: Long
                                ) {
                                    mInstallmentsSelect = mInstallments[position].toString()
                                    Log.d(TAG, "Cuota seleccionada ${mInstallmentsSelect}")
                                    //mandar description a la otra pantalla
                                    mBinding!!.txtInstallmentsDescription.text =
                                        mInstallments[position].recommendedMessage
                                }

                                override fun onNothingSelected(p0: AdapterView<*>?) {
                                }
                            }
                    }
                }

                override fun onFailure(call: Call<JsonArray>, t: Throwable) {
                    Log.d(TAG, "Error:${t.message}")
                    Toast.makeText(
                        this@ClientPaymentsInstallmentsActivity,
                        "Error: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    //obtener los productos guardados en SharedPref
    private fun getProductoSharedPref() {
        //validar si hay un producto
        if (!mSharedPref?.getInformation("order")
                .isNullOrBlank()
        ) {
            //transformar una lista tipo json a tipo product
            val type = object : TypeToken<ArrayList<Product>>() {}.type
            mSelectProduct = mGson.fromJson(mSharedPref?.getInformation("order"), type)
            getTotalProduct()

        }
    }

    //metodo para calcular el total a pagar
    private fun getTotalProduct(): Double {
        for (p in mSelectProduct) {
            if (p.quantity != null) {
                mTotal = mTotal + (p.quantity!! * p.price)
            }
        }
        //mandar total a la otra pantalla
        mBinding!!.txtInstallmentsTotal.text = "${mTotal}"
        return mTotal
    }

    //obtener los datos del usuario
    fun getUserFromSession() {

        val gson = Gson()
        if (!mSharedPref?.getInformation("user").isNullOrBlank()) {
            //si el usuario esta en session
            mUser = gson.fromJson(mSharedPref?.getInformation("user"), User::class.java)
        }
    }

    //validar que el usuario seleccione una dirección
    private fun getAddressFromSession() {
        if (!mSharedPref?.getInformation("address").isNullOrBlank()) {
            address = mGson.fromJson( mSharedPref?.getInformation("address"),Address::class.java ) //si existe una direccion
        }
    }
    //validar cuota seleccionada
    private fun validateCuota(){
        if (!mInstallmentsSelect.isBlank()){
            createPayment()
        }else{
            Toast.makeText(this, R.string.txt_select_number_installments, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onClick(myItem: View?) {
        when (myItem){
            mBinding!!.btnConfirmPayInstallments ->{validateCuota()}
        }
    }

    //ir a la vista finalizar compra
    private fun goToPaymentsStatus(paymentMethodId:String, paymentStatus:String, lasFourDigitalCard:String){
                                   //validar tipo de tarjeta si se realizo con exito la compra, ultimos cuatro digitos de la tarjeta
    val mIntent=Intent(this, ClientPaymentsStatusActivity::class.java)
        //mandar los datos a la otra actividad
        mIntent.putExtra("paymentMethodId",paymentMethodId)
        mIntent.putExtra("paymentStatus",paymentStatus)
        mIntent.putExtra("lastFourDigits",lasFourDigitalCard)
        mIntent.flags=Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK//limpiar historial de pantallas
        startActivity(mIntent)
    }

    override fun showProgressBar() {
        mBinding!!.progressBarSpinKit?.setVisibility(View.VISIBLE)
    }

    override fun hideProgressBar() {
        mBinding!!.progressBarSpinKit?.setVisibility(View.GONE)
    }
}