package com.alejandro.deliverylinks.providers

import android.util.Log
import com.alejandro.deliverylinks.api.ApiRoutes
import com.alejandro.deliverylinks.models.category.Category
import com.alejandro.deliverylinks.models.response.ResponseHttp
import com.alejandro.deliverylinks.routes.CategoriesRoutes
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import java.io.File

class CategoriesProvider(val token: String) {

    private var mCategoriesRoutes: CategoriesRoutes? = null

    init {
        val api = ApiRoutes()
        //validar token diferente de nulo
        //enviar los datos ya con el token
        mCategoriesRoutes = api.getCategoriesRoutes(token)

    }


    fun createCategory(file: File, mCategory: Category): Call<ResponseHttp>? {
        val reqFile = RequestBody.create(MediaType.parse("image/*"), file)
        val image = MultipartBody.Part.createFormData("image", file.name, reqFile)

        Log.d("CATEGORY", mCategory.toJson())

        val requestBody = RequestBody.create(MediaType.parse("text/plain"), mCategory.toJson())
        return mCategoriesRoutes?.createCategory(image,requestBody,token) //enviar los datos ya con el token le pasamos el token
    }

    //obtener las categorias

    fun getCategories():Call<ArrayList<Category>>?{
        return mCategoriesRoutes?.getCategories(token)
    }
}