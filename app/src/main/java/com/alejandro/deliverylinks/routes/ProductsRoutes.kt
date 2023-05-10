package com.alejandro.deliverylinks.routes

import com.alejandro.deliverylinks.models.product.Product
import com.alejandro.deliverylinks.models.response.ResponseHttp
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ProductsRoutes {

    @Multipart
    @POST("products/create")
    fun createProduct(
        @Part images: Array<MultipartBody.Part?>,
        @Part("product") product: RequestBody,
        @Header("Authorization") token: String  //enviar los datos ya con el token
    ): Call<ResponseHttp>

    //obtener los productos
    @GET("products/findByCategory/{id_category}")
    fun getProducts(
        @Path("id_category") idCategory : String,
        @Header("Authorization") token: String  //obtener los datos ya con el token
    ): Call<ArrayList<Product>>
}