package com.alejandro.deliverylinks.providers

import com.alejandro.deliverylinks.api.ApiRoutes
import com.alejandro.deliverylinks.models.orders.Order
import com.alejandro.deliverylinks.models.response.ResponseHttp
import com.alejandro.deliverylinks.routes.OrdersRoutes
import retrofit2.Call
//crear orders
class OrdersProvider(val token: String) {

    private var mOrdersRoutes: OrdersRoutes? = null

    init {
        val api = ApiRoutes()
        //validar token diferente de nulo
        //enviar los datos ya con el token
        mOrdersRoutes = api.getOrdersRoutes(token)

    }

    //crear orders
    fun createOrders(mOrders: Order): Call<ResponseHttp>? {
        return mOrdersRoutes?.createOrder(
            mOrders,
            token
        ) //enviar los datos ya con el token le pasamos el token
    }

    //mostar ordenes filtradas por estado
      fun getOrdersByStatus(status:String):Call<ArrayList<Order>>?{
          return mOrdersRoutes?.getOrdersByStatus(status,token)
      }
    //obtener de ordenes por estado y cliente
    fun getOrdersByClientAndStatus(idClient:String,status:String):Call<ArrayList<Order>>?{
        return mOrdersRoutes?.getOrdersByClientAndStatus(idClient,status,token)
    }

    //actualizar orden a despachado
    fun updateToDispatchedOrder(mOrders: Order): Call<ResponseHttp>? {
        return mOrdersRoutes?.orderUpdateToDispatched(mOrders, token) //enviar los datos ya con el token le pasamos el token
    }

    //filtrar las ordenes por id delivery y por status
    fun getOrdersByDeliveryAndStatus(idDelivery:String,status:String):Call<ArrayList<Order>>?{
        return mOrdersRoutes?.getOrdersByDeliveryAndStatus(idDelivery,status,token)
    }
    //actualizar orden a en camino
    fun updateToOnTheWay(mOrders: Order): Call<ResponseHttp>? {
        return mOrdersRoutes?.orderUpdateToOnTheWay(mOrders, token) //enviar los datos ya con el token le pasamos el token
    }
    //actualizar orden a en camino
    fun updateToDelivery(mOrders: Order): Call<ResponseHttp>? {
        return mOrdersRoutes?.orderUpdateToDelivery(mOrders, token) //enviar los datos ya con el token le pasamos el token
    }
    //obtenr ruta del delivery al cliente
    fun updateLagLng(mOrders: Order): Call<ResponseHttp>? {
        return mOrdersRoutes?.orderUpdateLagLng(mOrders, token) //enviar los datos ya con el token le pasamos el token
    }
}