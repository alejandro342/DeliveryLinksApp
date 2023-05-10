package com.alejandro.deliverylinks.models.orders

import com.alejandro.deliverylinks.models.address.Address
import com.alejandro.deliverylinks.models.product.Product
import com.alejandro.deliverylinks.models.user.User
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

//crear una orden
class Order(
    @SerializedName("id") val id: String? = null,
    @SerializedName("id_client") val idClient: String,
    @SerializedName("id_delivery") var idDelivery: String? = null,
    @SerializedName("id_address") val idAddress: String?,
    @SerializedName("status") val status: String? = "PAGADO",
    @SerializedName("timestamp") val timestamp: String?=null, //dar formato a la fecha se cambia tipo Long a String
    @SerializedName("products") val products: ArrayList<Product>,
    //para mostrar cliente y direccion a entregar
    @SerializedName("client") val client: User?=null,
    //para mostrar nombre del repartidor asignado
    @SerializedName("delivery") val delivery:User?=null,
    @SerializedName("address") val address: Address?=null,
    //trazar ruta del delivery al cliente
    @SerializedName("lat") var lat: Double?=null,
    @SerializedName("lng") var lng: Double?=null


) {


    fun toJson(): String {
        return Gson().toJson(this)
    }

    override fun toString(): String {
        return "Order(id=$id, idClient='$idClient', idDelivery=$idDelivery, idAddress=$idAddress, status=$status, timestamp=$timestamp, products=$products, client=$client, delivery=$delivery, address=$address)"
    }


}