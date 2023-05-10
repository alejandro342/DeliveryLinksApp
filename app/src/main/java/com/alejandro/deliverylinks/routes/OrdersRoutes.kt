package com.alejandro.deliverylinks.routes

import com.alejandro.deliverylinks.models.orders.Order
import com.alejandro.deliverylinks.models.response.ResponseHttp
import retrofit2.Call
import retrofit2.http.*

interface OrdersRoutes {

    //crear orden
    @POST("orders/create")
    fun createOrder(
        @Body order: Order,
        @Header("Authorization") token: String  //enviar los datos ya con el token
    ): Call<ResponseHttp>

     //mostar ordenes filtradas por estado
     @GET("orders/findByStatus/{status}")
     fun getOrdersByStatus(
         @Path("status") status:String,
         @Header("Authorization") token: String  //obtener los datos ya con el token
     ) :Call<ArrayList<Order>>

     //obtener de ordenes por estado y cliente
     @GET("orders/findByClientAndStatus/{id_client}/{status}")
     fun getOrdersByClientAndStatus(
         @Path("id_client") idClient:String,
         @Path("status") status:String,
         @Header("Authorization") token: String  //obtener los datos ya con el token
     ) :Call<ArrayList<Order>>

    //filtrar las ordenes por id delivery y por status
    @GET("orders/findByDeliveryAndStatus/{id_delivery}/{status}")
    fun getOrdersByDeliveryAndStatus(
        @Path("id_delivery") idDelivery:String,
        @Path("status") status:String,
        @Header("Authorization") token: String  //obtener los datos ya con el token
    ) :Call<ArrayList<Order>>

    //actualizar orden a despachado
    @PUT("orders/updateToDispatched")
    fun orderUpdateToDispatched(
        @Body order: Order,
        @Header("Authorization") token: String  //enviar los datos ya con el token
    ): Call<ResponseHttp>

    //actualizar orden a en camino
    @PUT("orders/updateToOnTheWay")
    fun orderUpdateToOnTheWay(
        @Body order: Order,
        @Header("Authorization") token: String  //enviar los datos ya con el token
    ): Call<ResponseHttp>

    //actualizar orden a entregado
    @PUT("orders/updateToDelivery")
    fun orderUpdateToDelivery(
        @Body order: Order,
        @Header("Authorization") token: String  //enviar los datos ya con el token
    ): Call<ResponseHttp>

    //obtener ruta del delivery al cliente
    @PUT("orders/updateLagLng")
    fun orderUpdateLagLng(
        @Body order: Order,
        @Header("Authorization") token: String  //enviar los datos ya con el token
    ): Call<ResponseHttp>
}