package com.alejandro.deliverylinks.providers

import android.util.Log
import com.alejandro.deliverylinks.api.ApiRoutes
import com.alejandro.deliverylinks.models.product.Product
import com.alejandro.deliverylinks.models.response.ResponseHttp
import com.alejandro.deliverylinks.routes.ProductsRoutes
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import java.io.File

class ProductsProvider(val token: String) {

    private var mProductsRoutes: ProductsRoutes? = null

    init {
        val api = ApiRoutes()
        //validar token diferente de nulo
        //enviar los datos ya con el token
        mProductsRoutes = api.getProductsRoutes(token)

    }

    //enviar mas de una imagen
    fun createProduct(files: List<File>, mProduct: Product): Call<ResponseHttp>? {

        val images = arrayOfNulls<MultipartBody.Part>(files.size)

        //para el recorrido de archivos
        for (i in 0 until files.size){

            val reqFile = RequestBody.create(MediaType.parse("image/*"), files[i])
           images[i] = MultipartBody.Part.createFormData("image", files[i].name, reqFile)
        }


        val requestBody = RequestBody.create(MediaType.parse("text/plain"), mProduct.toJson())
        return mProductsRoutes?.createProduct(
            images,
            requestBody,
            token
        ) //enviar los datos ya con el token le pasamos el token
    }


    fun getProduct(idCategory:String):Call<ArrayList<Product>>?{
        return mProductsRoutes?.getProducts(idCategory,token)
    }

}