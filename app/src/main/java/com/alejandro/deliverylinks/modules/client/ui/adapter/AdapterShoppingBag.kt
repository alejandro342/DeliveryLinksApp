package com.alejandro.deliverylinks.modules.client.ui.adapter

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
import com.alejandro.deliverylinks.modules.client.ui.views.client.ClientShoppingBagActivity
import com.alejandro.deliverylinks.utils.SharedPref
import com.squareup.picasso.Picasso

class AdapterShoppingBag(val mContext: Context, val mProducts: ArrayList<Product>) :
    RecyclerView.Adapter<AdapterShoppingBag.ShoppingBagViewHolder>() {

    val sharedPref = SharedPref(mContext)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingBagViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_shopping_bag, parent, false)
        return ShoppingBagViewHolder(view)
    }
    //mandar el total al activity
    init {
        (mContext as ClientShoppingBagActivity).setTotal(getTotalProduct())
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ShoppingBagViewHolder, position: Int) {
        val product = mProducts[position]

        holder.txtViewNameProduct.text = product.name
        //controlar el contador de productos
        holder.txtViewCountProduct.text = "${product.quantity}"
        //controlar el precio de productos
        if (product.quantity != null){
            holder.txtViewPriceProduct.text = "$ ${product.price * product.quantity!!}"
        }
        Picasso.get()
            .load(product.image1)
            .into(holder.imgViewProduct)

        //aumentar o disminuir productos
        holder.imgViewAddProduct.setOnClickListener{addItem(product,holder)}
        holder.imgViewRemoveProduct.setOnClickListener{removeItem(product,holder)}
        //eliminar productos
        holder.imgViewDeleteProduct.setOnClickListener{deleteItemProduct(position)}

        //para ir a la vista detail del producto
        // holder.itemView.setOnClickListener { goToDetailProduct(product) }
    }

    //ponerle accion los botones en el recicler view
    //comparar si el producto ya existe en el carrito editamos la cantidad del producto seleccionado
    private fun getIndexof(idProduct: String): Int {
        var pos = 0
        for (p in mProducts) {
            if (p.id == idProduct) {
                return pos
            }
            pos++
        }
        return -1
    }

    //el control de cantida de productos y precio
    @SuppressLint("SetTextI18n")
    private fun addItem(product: Product, holder: ShoppingBagViewHolder) {
        val index = getIndexof(product.id!!)
        //incrementar el precio por cada producto agregado
        product.quantity = product.quantity!! + 1
        mProducts[index].quantity = product.quantity
        holder.txtViewCountProduct.text = "${product.quantity}"
        holder.txtViewPriceProduct.text = "${product.quantity!! * product.price}"
        sharedPref.saveSession("order", mProducts)
        //cada ves que se agregue un producto se calcular el total
        (mContext as ClientShoppingBagActivity).setTotal(getTotalProduct())
    }

    //el control de cantida de productos y precio
    @SuppressLint("SetTextI18n")
    private fun removeItem(product: Product, holder: ShoppingBagViewHolder) {
        //validar que almenos se tenga un producto en la lista
        if (product.quantity!! > 1) {
            val index = getIndexof(product.id!!)
            //incrementar el precio por cada producto agregado
            product.quantity = product.quantity!! - 1
            mProducts[index].quantity = product.quantity
            holder.txtViewCountProduct.text = "${product.quantity}"
            holder.txtViewPriceProduct.text = "${product.quantity!! * product.price}"
            sharedPref.saveSession("order", mProducts)
            //cada ves que se disminulla la cantidad de un producto se calculara el total
            (mContext as ClientShoppingBagActivity).setTotal(getTotalProduct())
        }

    }
    //eliminar productos
    private fun deleteItemProduct(position:Int){
        mProducts.removeAt(position)
        notifyItemMoved(position,mProducts.size)
        notifyItemRangeChanged(position,mProducts.size)
        sharedPref.saveSession("order",mProducts)
        //cada ves que se elimine un producto de la lista se calcular el total
        (mContext as ClientShoppingBagActivity).setTotal(getTotalProduct())
    }

    //metodo para calcular el total a pagar
    private fun getTotalProduct():Double{
        var mTotal = 0.0
        for (p in mProducts){
            //validar si hay productos para calcular precio
            if (p.quantity != null){
                mTotal=mTotal +(p.quantity!! * p.price)
            }
        }
        return mTotal
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

    class ShoppingBagViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val txtViewNameProduct: TextView
        val txtViewPriceProduct: TextView
        val txtViewCountProduct: TextView
        val imgViewProduct: ImageView
        val imgViewAddProduct: ImageView
        val imgViewRemoveProduct: ImageView
        val imgViewDeleteProduct: ImageView


        init {
            txtViewNameProduct = itemView.findViewById(R.id.txt_Item_Name_Product_Shopping)
            txtViewPriceProduct = itemView.findViewById(R.id.txt_Item_Price_Product_Shopping)
            txtViewCountProduct = itemView.findViewById(R.id.txt_Item_Counter_Product_Shopping)
            imgViewProduct = itemView.findViewById(R.id.img_Item_Product_Shopping)
            imgViewAddProduct = itemView.findViewById(R.id.img_Item_Add_Product_Shopping)
            imgViewRemoveProduct = itemView.findViewById(R.id.img_Item_Remove_Product_Shopping)
            imgViewDeleteProduct = itemView.findViewById(R.id.img_Item_Delete_Product_Shopping)
        }
    }

}