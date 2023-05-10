package com.alejandro.deliverylinks.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Header

class RetrofitClient {

    fun getClient(url: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    //enviar los datos ya con el token realizar peticiones con token de usuario
    fun getClientWitToken(url:String, token:String):Retrofit{
        val client= OkHttpClient.Builder()
        client.addInterceptor { chain ->
            val request = chain.request()
            val newRequest=request.newBuilder().addHeader("Authorization", token)
            chain.proceed(newRequest.build())
        }
        return Retrofit.Builder()
            .baseUrl(url)
            .client(client.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}