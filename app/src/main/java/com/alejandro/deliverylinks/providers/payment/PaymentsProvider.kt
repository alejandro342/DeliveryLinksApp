package com.alejandro.deliverylinks.providers.payment

import android.util.Log
import com.alejandro.deliverylinks.api.ApiRoutes
import com.alejandro.deliverylinks.models.category.Category
import com.alejandro.deliverylinks.models.mercadopago.MercadoPagoPayment
import com.alejandro.deliverylinks.models.response.ResponseHttp
import com.alejandro.deliverylinks.routes.payments.PaymentsRoutes
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import java.io.File

class PaymentsProvider(val token: String) {

    private var paymentsRoutes: PaymentsRoutes? = null

    init {
        val api = ApiRoutes()
        //validar token diferente de nulo
        //enviar los datos ya con el token
        paymentsRoutes = api.getPaymentsRoutes(token)

    }

    fun createPayments(mercadoPagoPayment: MercadoPagoPayment): Call<ResponseHttp>? {
        return paymentsRoutes?.createPayment(mercadoPagoPayment, token)

    }
}