package com.alejandro.deliverylinks.modules.selectroles.view.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.alejandro.deliverylinks.R
import com.alejandro.deliverylinks.models.rol.Rol
import com.alejandro.deliverylinks.modules.client.ui.views.client.ClientHomeActivity
import com.alejandro.deliverylinks.modules.delivery.ui.views.DeliveryHomeActivityView
import com.alejandro.deliverylinks.modules.restaurant.ui.view.RestaurantHomeActivityView
import com.alejandro.deliverylinks.utils.SharedPref
import com.squareup.picasso.Picasso

class AdapterSelectRoles(val mContext: Context, val roles: ArrayList<Rol>) :
    RecyclerView.Adapter<AdapterSelectRoles.RolesHolderView>() {

    val sharedPref = SharedPref(mContext)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RolesHolderView {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_select_roles, parent, false)
        return RolesHolderView(view)
    }

    override fun onBindViewHolder(holder: RolesHolderView, position: Int) {
        val rol = roles[position]
        holder.txtViewRol.text = rol.name

        Picasso.get()
            .load(rol.image)
            .into(holder.imgRol)
        holder.itemView.setOnClickListener { goToRol(rol) }
    }

    private fun goToRol(rol: Rol) {
        if (rol.name == "RESTAURANTE") {
            sharedPref.saveSession("rol", "RESTAURANTE")
            val mIntent = Intent(mContext, RestaurantHomeActivityView::class.java)
            mContext.startActivity(mIntent)
        }
        else if (rol.name == "CLIENTE") {
            sharedPref.saveSession("rol", "CLIENTE")
            val mIntent = Intent(mContext, ClientHomeActivity::class.java)
            mContext.startActivity(mIntent)
        }
        else if (rol.name == "REPARTIDOR") {
            sharedPref.saveSession("rol", "REPARTIDOR")
            val mIntent = Intent(mContext, DeliveryHomeActivityView::class.java)
            mContext.startActivity(mIntent)
        }
    }

    override fun getItemCount(): Int {
        return roles.size
    }

    class RolesHolderView(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtViewRol: TextView
        val imgRol: ImageView

        init {
            txtViewRol = itemView.findViewById(R.id.txt_rol)
            imgRol = itemView.findViewById(R.id.img_rol)
        }
    }

}