package com.alejandro.deliverylinks.providers

import com.alejandro.deliverylinks.api.ApiRoutes
import com.alejandro.deliverylinks.models.address.Address
import com.alejandro.deliverylinks.models.response.ResponseHttp
import com.alejandro.deliverylinks.routes.AddressRoutes
import retrofit2.Call
//crear dirección
class AddressProvider(val token: String) {

    private var mAddressRoutes: AddressRoutes? = null

    init {
        val api = ApiRoutes()
        //validar token diferente de nulo
        //enviar los datos ya con el token
        mAddressRoutes = api.getAddressRoutes(token)

    }

    //crear dirección
    fun createAddress(mAddress: Address): Call<ResponseHttp>? {
        return mAddressRoutes?.createAddress(
            mAddress,
            token
        ) //enviar los datos ya con el token le pasamos el token
    }

    //obtener las direcciones por usuario
      fun getAddress(idUser:String):Call<ArrayList<Address>>?{
          return mAddressRoutes?.getAddress(idUser,token)
      }

}