package com.alejandro.deliverylinks.modules.client.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.alejandro.deliverylinks.R
import com.alejandro.deliverylinks.models.address.Address
import com.alejandro.deliverylinks.modules.client.ui.views.address.ClientAddressListActivity
import com.alejandro.deliverylinks.utils.SharedPref
import com.google.gson.Gson

//Guardar dirección seleccionada por el usuario
//listar Direcciones del usuario
class AdapterAddress(val mContext: Context, val mAddress: ArrayList<Address>) :
    RecyclerView.Adapter<AdapterAddress.AddressClientViewHolder>() {

    //marcar la dirección que el usuario elija
    //Guardar dirección seleccionada por el usuario
    val sharedPref = SharedPref(mContext)
    val mGson = Gson()

    //almacena la direccion seleccionada por el usuario
    var prev = 0

    //guardar la dirección en sharedpref que el usuario selecciono
    var positionAddresSession = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressClientViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_address, parent, false)
        return AddressClientViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: AddressClientViewHolder, position: Int) {
        val address = mAddress[position] //las direcciones
        //guardar la dirección en sharedpref que el usuario selecciono
        if (!sharedPref.getInformation("address").isNullOrBlank()) {//si el usuario ya elijio una dirección
            val adr = mGson.fromJson(sharedPref.getInformation("address"), Address::class.java)
            if (adr.id == address.id) {
                //para mostrar la direccion ya seleccionada y se almacena
                positionAddresSession = position
                holder.imgViewCheckAddress.visibility = View.VISIBLE
            }
        }

        holder.txtViewAddress.text = address.address
        holder.txtViewNeigborhood.text = address.neighborhood

        holder.itemView.setOnClickListener {
            //desmarcar direccion elejida por el usuario y sustituir direccion
            (mContext as ClientAddressListActivity).resetValue(prev)
            (mContext as ClientAddressListActivity).resetValue(positionAddresSession)
            prev = position
            //Guardar dirección seleccionada por el usuario cuando de click
            //marcar la dirección que el usuario elija
            holder.imgViewCheckAddress.visibility = View.VISIBLE
            saveAddress(address.toJson())
        }
    }

    //Guardar dirección seleccionada por el usuario
    private fun saveAddress(data: String) {
        val mAddressSave = mGson.fromJson(data, Address::class.java)//convertir a objeto tipo address
        sharedPref.saveSession("address", mAddressSave)
    }

    override fun getItemCount(): Int {
        return mAddress.size
    }

    class AddressClientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val txtViewAddress: TextView
        val txtViewNeigborhood: TextView
        val imgViewCheckAddress: ImageView

        init {
            txtViewAddress = itemView.findViewById(R.id.txt_Item_address_Client)
            txtViewNeigborhood = itemView.findViewById(R.id.txt_Item_Neighborhood_Client)
            imgViewCheckAddress = itemView.findViewById(R.id.img_Item_Check_Address)
        }
    }

}