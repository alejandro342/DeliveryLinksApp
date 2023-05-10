package com.alejandro.deliverylinks.routes

import com.alejandro.deliverylinks.models.address.Address
import com.alejandro.deliverylinks.models.category.Category
import com.alejandro.deliverylinks.models.response.ResponseHttp
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface AddressRoutes {

    //crear dirección
    @POST("address/create")
    fun createAddress(
        @Body address:Address,
        @Header("Authorization") token: String  //enviar los datos ya con el token
    ): Call<ResponseHttp>

    //obtener las categorias
    @GET("address/findByUser/{id_user}")
    fun getAddress(
        @Path("id_user") idUser:String,
        @Header("Authorization") token: String  //obtener los datos ya con el token
    ) :Call<ArrayList<Address>>
}