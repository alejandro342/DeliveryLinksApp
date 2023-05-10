package com.alejandro.deliverylinks.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson

//clase para guardar sesión del usuario
class SharedPref(activity: Context) {

    private var prefs: SharedPreferences? = null

    init {
        prefs = activity.getSharedPreferences("com.alejandro.deliverylinks", Context.MODE_PRIVATE)
    }

    //guardar la información de la sesión
    fun saveSession(key: String, objeto: Any) {
        try {

            val gson = Gson()
            val json = gson.toJson(objeto)

            with(prefs?.edit()) {
                this?.putString(key, json)
                this?.commit()
            }
        } catch (e: Exception) {
            Log.d("ERROR", "Err: ${e.message}")
        }
    }

    //para obtener la información de la sesión
    fun getInformation(key: String): String? {
        val information = prefs?.getString(key, "")
        return information
    }

    //cerrar sesión del usuario
    fun closeSession(key: String) {
        prefs?.edit()?.remove(key)?.apply()
    }

    //limpiar la orden después de efectuarse el pago con tarjeta
    fun remove(key: String) {
        prefs?.edit()?.remove(key)?.apply()
    }
}