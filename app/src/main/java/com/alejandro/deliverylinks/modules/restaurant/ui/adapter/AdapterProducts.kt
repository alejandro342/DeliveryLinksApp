package com.alejandro.deliverylinks.modules.restaurant.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.alejandro.deliverylinks.R
import com.alejandro.deliverylinks.models.product.Product
import com.alejandro.deliverylinks.modules.client.ui.views.client.ClientProductsDetailActivity
import com.alejandro.deliverylinks.utils.SharedPref
import com.squareup.picasso.Picasso

class AdapterProducts(val mContext: Context, val mProducts: ArrayList<Product>) :
    RecyclerView.Adapter<AdapterProducts.ProductsViewHolder>() {

    val sharedPref = SharedPref(mContext)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ProductsViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ProductsViewHolder, position: Int) {
        val product = mProducts[position]

        holder.txtViewNameProduct.text = product.name
        holder.txtViewPriceProduct.text = "$ ${product.price}"
        Picasso.get()
            .load(product.image1)
            .into(holder.imgViewProduct)

        //para ir a la vista detail del producto
        holder.itemView.setOnClickListener { goToDetailProduct(product) }
    }

    //para ir a la vista detail del producto
    private fun goToDetailProduct(product: Product) {
        val mIntent = Intent(mContext, ClientProductsDetailActivity::class.java)
        mIntent.putExtra("product", product.toJson())  //mandar los datos a la vista
        mContext.startActivity(mIntent)
    }

    override fun getItemCount(): Int {
        return mProducts.size
    }

    class ProductsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtViewNameProduct: TextView
        val txtViewPriceProduct: TextView
        val imgViewProduct: ImageView


        init {
            txtViewNameProduct = itemView.findViewById(R.id.txt_Item_Name_Producto_List)
            txtViewPriceProduct = itemView.findViewById(R.id.txt_Item_Price_Product_List)
            imgViewProduct = itemView.findViewById(R.id.Img_Item_Product)
        }
    }

}