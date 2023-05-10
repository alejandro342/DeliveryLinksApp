package com.alejandro.deliverylinks.api

import com.alejandro.deliverylinks.routes.*
import com.alejandro.deliverylinks.routes.payments.PaymentsRoutes

class ApiRoutes {

    private val BASE_URL = "tu-direccion-ip-y-:puerto/ruta-de-tu-api"
    private val retrofit = RetrofitClient()

    fun getUsersRoutes(): UsersRoutes {
        return retrofit.getClient(BASE_URL).create(UsersRoutes::class.java)
    }

    //enviar los datos ya con el token realizar peticiones con token de usuario
    fun getUsersRoutesWitToken(token:String): UsersRoutes {
        return retrofit.getClientWitToken(BASE_URL,token).create(UsersRoutes::class.java)
    }

    fun getCategoriesRoutes(token:String): CategoriesRoutes {
        return retrofit.getClientWitToken(BASE_URL,token).create(CategoriesRoutes::class.java)
    }

    fun getProductsRoutes(token:String): ProductsRoutes {
        return retrofit.getClientWitToken(BASE_URL,token).create(ProductsRoutes::class.java)
    }

    //crear dirección
    fun getAddressRoutes(token:String): AddressRoutes {
        return retrofit.getClientWitToken(BASE_URL,token).create(AddressRoutes::class.java)
    }

    //crear orders
    fun getOrdersRoutes(token:String): OrdersRoutes {
        return retrofit.getClientWitToken(BASE_URL,token).create(OrdersRoutes::class.java)
    }

    //crear payments
    fun getPaymentsRoutes(token:String): PaymentsRoutes {
        return retrofit.getClientWitToken(BASE_URL,token).create(PaymentsRoutes::class.java)
    }

}