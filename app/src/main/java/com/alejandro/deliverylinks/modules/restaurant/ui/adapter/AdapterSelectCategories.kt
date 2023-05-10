package com.alejandro.deliverylinks.modules.restaurant.ui.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.alejandro.deliverylinks.R
import com.alejandro.deliverylinks.models.category.Category
import com.alejandro.deliverylinks.modules.client.ui.views.client.ClientProductsListActivity
import com.alejandro.deliverylinks.utils.SharedPref
import com.squareup.picasso.Picasso

class AdapterSelectCategories(val mContext: Context, val mCategories: ArrayList<Category>) :
    RecyclerView.Adapter<AdapterSelectCategories.CategoriesViewHolder>() {

    val sharedPref = SharedPref(mContext)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriesViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_categories, parent, false)
        return CategoriesViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoriesViewHolder, position: Int) {
        val category = mCategories[position]
        holder.txtViewCategory.text = category.name

        Picasso.get()
            .load(category.image)
            .into(holder.imgCategory)
        //obtener por categoria
     holder.itemView.setOnClickListener { goToProduct(category) }
    }

    //obtener por categoria
   private fun goToProduct(mCategory:Category) {
            val mIntent = Intent(mContext, ClientProductsListActivity::class.java)
        //mandamos un parametro para saber que categoria se eligio
        mIntent.putExtra("idCategory",mCategory.id)
            mContext.startActivity(mIntent)
    }

    override fun getItemCount(): Int {
        return mCategories.size
    }

    class CategoriesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtViewCategory: TextView
        val imgCategory: ImageView

        init {
            txtViewCategory = itemView.findViewById(R.id.txt_Item_Name_Category_R)
            imgCategory = itemView.findViewById(R.id.Img_Item_Category_R)
        }
    }

}