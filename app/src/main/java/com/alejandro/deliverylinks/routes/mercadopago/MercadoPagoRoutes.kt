package com.alejandro.deliverylinks.routes.mercadopago

import com.alejandro.deliverylinks.models.mercadopago.MercadoPagoCardTokenBody
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface MercadoPagoRoutes {

    //obtener cuotas
    @GET("v1/payment_methods/installments?access_token=tu-Token")
    fun getInstallments(@Query("bin") bin:String, @Query("amount") amount:String):Call<JsonArray>

    @POST("v1/card_tokens?public_key=tu-Key")
    fun createCardToken(@Body body: MercadoPagoCardTokenBody):Call<JsonObject>

}