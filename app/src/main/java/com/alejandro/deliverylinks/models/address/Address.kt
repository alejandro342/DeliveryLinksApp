package com.alejandro.deliverylinks.models.address

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

//crear dirección
class Address(
    @SerializedName("id") val id: String? = null,
    @SerializedName("id_user") val idUser:String,
    @SerializedName("address") val address:String,
    @SerializedName("neighborhood") val neighborhood:String,
    @SerializedName("lat") val lat : Double,
    @SerializedName("lng") val lng:Double
) {
    override fun toString(): String {
        return "Address(id=$id, idUser='$idUser', address='$address', neighborhood='$neighborhood', lat=$lat, lng=$lng)"
    }

    //transformar el modelo address a tipo json
    fun toJson(): String {
        return Gson().toJson(this)
    }
}