package com.alejandro.deliverylinks.routes.payments

import com.alejandro.deliverylinks.models.mercadopago.MercadoPagoPayment
import com.alejandro.deliverylinks.models.response.ResponseHttp
import retrofit2.Call
import retrofit2.http.*

interface PaymentsRoutes {


    //realizar pago
    @POST("payments/create")
    fun createPayment(
        @Body mercadoPagoPayment: MercadoPagoPayment,
        @Header("Authorization") token: String  //obtener los datos ya con el token
    ): Call<ResponseHttp>
}