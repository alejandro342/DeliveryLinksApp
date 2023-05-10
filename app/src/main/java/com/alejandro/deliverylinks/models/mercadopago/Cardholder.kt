package com.alejandro.deliverylinks.models.mercadopago

import com.google.gson.annotations.SerializedName

class Cardholder(
    @SerializedName("name") val name: String
) {

    override fun toString(): String {
        return "CardHolder(name='$name')"
    }
}