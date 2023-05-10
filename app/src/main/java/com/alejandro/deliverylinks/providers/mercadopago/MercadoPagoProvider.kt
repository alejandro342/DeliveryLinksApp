package com.alejandro.deliverylinks.providers.mercadopago

import com.alejandro.deliverylinks.api.mercadopago.MercadoPagoApiRoutes
import com.alejandro.deliverylinks.models.mercadopago.MercadoPagoCardTokenBody
import com.alejandro.deliverylinks.routes.mercadopago.MercadoPagoRoutes
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import retrofit2.Call

class MercadoPagoProvider {
    var mMercadoPagoRoutes:MercadoPagoRoutes?=null

    init {
        val mApi = MercadoPagoApiRoutes()
        mMercadoPagoRoutes = mApi.getMercadoPagoRoutes()
    }

    fun createCardToken(mercadoPagoCardTokenBody: MercadoPagoCardTokenBody): Call<JsonObject>?{
        return mMercadoPagoRoutes?.createCardToken(mercadoPagoCardTokenBody)
    }

    //obtener cuotas
    fun getInstallments(bin:String, amount:String):Call<JsonArray>?{
        return mMercadoPagoRoutes?.getInstallments(bin, amount)
    }
}