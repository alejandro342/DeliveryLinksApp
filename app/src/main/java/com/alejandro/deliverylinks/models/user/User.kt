package com.alejandro.deliverylinks.models.user

import com.alejandro.deliverylinks.models.rol.Rol
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

class User(
    @SerializedName("id") val id: String? = null,
    @SerializedName("name") var name: String,
    @SerializedName("lastname") var lastname: String,
    @SerializedName("email") val email: String,
    @SerializedName("phone") var phone: String,
    @SerializedName("image") var image: String? = null,
    @SerializedName("password") val password: String,
    @SerializedName("session_token") val sessionToken: String? = null,
    //se agrego para guardar el token de notificaciones
    @SerializedName("notification_token") var notificationToken: String? = null,
    @SerializedName("is_avaliable") val isAvailable: Boolean? = null,
    @SerializedName("roles") val roles: ArrayList<Rol>? = null
) {
    override fun toString(): String {
        //modifiacamos para solo mostrar el nombre del delivery
        return "$name $lastname"
    }

    //transformar el modelo usuario a tipo json
    fun toJson():String{
        return Gson().toJson(this)
    }
}