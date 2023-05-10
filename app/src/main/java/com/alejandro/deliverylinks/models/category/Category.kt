package com.alejandro.deliverylinks.models.category

import com.google.gson.Gson

class Category(
    val id: String? = null,
    val name: String,
    val image: String? = null
) {

    override fun toString(): String {
        return name
    }

    //transformar el modelo category a tipo json
    fun toJson(): String {
        return Gson().toJson(this)
    }

}