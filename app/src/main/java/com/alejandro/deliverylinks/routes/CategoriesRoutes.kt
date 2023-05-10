package com.alejandro.deliverylinks.routes

import com.alejandro.deliverylinks.models.category.Category
import com.alejandro.deliverylinks.models.response.ResponseHttp
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface CategoriesRoutes {

    @Multipart
    @POST("categories/create")
    fun createCategory(
        @Part image: MultipartBody.Part,
        @Part("category") category: RequestBody,
        @Header("Authorization") token: String  //enviar los datos ya con el token
    ): Call<ResponseHttp>

    //obtener las categorias
    @GET("categories/getAll")
    fun getCategories(
        @Header("Authorization") token: String  //obtener los datos ya con el token
    ) :Call<ArrayList<Category>>
}