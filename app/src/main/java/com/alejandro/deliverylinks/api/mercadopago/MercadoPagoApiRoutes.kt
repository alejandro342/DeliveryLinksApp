package com.alejandro.deliverylinks.api.mercadopago

import com.alejandro.deliverylinks.api.RetrofitClient
import com.alejandro.deliverylinks.routes.mercadopago.MercadoPagoRoutes

class MercadoPagoApiRoutes {

    private val BASE_URL = "https://api.mercadopago.com/"
    private val retrofit = RetrofitClient()

    fun getMercadoPagoRoutes(): MercadoPagoRoutes {
        return retrofit.getClient(BASE_URL).create(MercadoPagoRoutes::class.java)
    }

}