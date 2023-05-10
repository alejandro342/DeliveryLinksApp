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
import com.alejandro.deliverylinks.models.product.Product
import com.alejandro.deliverylinks.utils.SharedPref
import com.squareup.picasso.Picasso

//mostrar el detalle de la orden con sus productos
class AdapterOrderDetailProducts(val mContext: Context, val mProducts: ArrayList<Product>) :
    RecyclerView.Adapter<AdapterOrderDetailProducts.OrderDetailProductsViewHolder>() {

    val sharedPref = SharedPref(mContext)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OrderDetailProductsViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_order_product_detail, parent, false)
        return OrderDetailProductsViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: OrderDetailProductsViewHolder, position: Int) {
        val product = mProducts[position]
        //mostrar el detalle de la orden con sus productos
        holder.txtViewNameProductDetail.text = product.name
        //contador de productos y validar
        if (product.quantity!=null){
            holder.txtViewQuantityDetail.text = "${product.quantity}"
        }
        Picasso.get()
            .load(product.image1)
            .into(holder.imgViewProductDetail)
    }

    override fun getItemCount(): Int {
        return mProducts.size
    }

    //mostrar el detalle de la orden con sus productos
    class OrderDetailProductsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val txtViewNameProductDetail: TextView
        val txtViewQuantityDetail: TextView
        val imgViewProductDetail: ImageView

        init {
            txtViewNameProductDetail = itemView.findViewById(R.id.txt_Item_Name_Product_Detail_Order)
            txtViewQuantityDetail = itemView.findViewById(R.id.txt_Item_Order_Quantity_Product_Detail)
            imgViewProductDetail = itemView.findViewById(R.id.img_Item_Order_Product_Detail)
        }
    }
}