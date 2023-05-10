package com.alejandro.deliverylinks.routes

import com.alejandro.deliverylinks.models.category.Category
import com.alejandro.deliverylinks.models.response.ResponseHttp
import com.alejandro.deliverylinks.models.user.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface UsersRoutes {

    @POST("users/create")
    fun registerUser(@Body user: User): Call<ResponseHttp>

    @FormUrlEncoded
    @POST("users/login")
    fun loginUser(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<ResponseHttp>

    @Multipart
    @PUT("users/update")
    fun updateUser(
        @Part image: MultipartBody.Part,
        @Part("user") user: RequestBody,
        @Header("Authorization") token:String  //enviar los datos ya con el token
    ): Call<ResponseHttp>

    @PUT("users/updateWithoutImage")
    fun updateWithoutImage(
        @Body user: User,
        @Header("Authorization") token:String  //enviar los datos ya con el token
    ): Call<ResponseHttp>

    //guardar token de notificaciones
    @PUT("users/updateNotificationToken")
    fun updateNotificationToken(
        @Body user: User,
        @Header("Authorization") token:String  //enviar los datos ya con el token
    ): Call<ResponseHttp>

    //obtener roles delivery
    @GET("users/findDeliveryMen")
    fun getDeliveryMen(
        @Header("Authorization") token: String  //obtener los datos ya con el token
    ) :Call<ArrayList<User>>
}