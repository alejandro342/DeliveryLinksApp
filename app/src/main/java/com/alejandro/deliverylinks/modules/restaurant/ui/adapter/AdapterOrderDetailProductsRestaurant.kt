package com.alejandro.deliverylinks.modules.restaurant.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.alejandro.deliverylinks.R
import com.alejandro.deliverylinks.models.orders.Order
import com.alejandro.deliverylinks.modules.client.ui.views.orders.RestaurantOrdersDetailActivity

//mostrar el detalle de la orden con sus productos
class AdapterOrderDetailProductsRestaurant(val mContext: Context, val mOrders: ArrayList<Order>) :
    RecyclerView.Adapter<AdapterOrderDetailProductsRestaurant.OrderDetailProductsRestaurantViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderDetailProductsRestaurantViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_orders_status_restaurant, parent, false)
        return OrderDetailProductsRestaurantViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: OrderDetailProductsRestaurantViewHolder, position: Int) {
        val order = mOrders[position] //las direcciones

        holder.txtViewNumbOrder.text = "Orden #${order.id}"
        holder.txtViewDate.text = " ${order.timestamp}"
        //obtener direccion a entregar mostrarla
        holder.txtViewDeliverIn.text = " ${order.address?.address}"
        holder.txtViewNameClient.text ="${order.client?.name} ${order.client?.lastname}"

        holder.itemView.setOnClickListener {
            goToOrderDetail(order)
        }
    }
    //mostrar el detalle de la orden con sus producto
    private fun goToOrderDetail(order: Order){
        val mIntent = Intent(mContext, RestaurantOrdersDetailActivity::class.java)
        mIntent.putExtra("order",order.toJson())
        mContext.startActivity(mIntent)
    }

    override fun getItemCount(): Int {
        return mOrders.size
    }

    class OrderDetailProductsRestaurantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val txtViewNumbOrder: TextView
        val txtViewDate: TextView
        val txtViewDeliverIn: TextView
        val txtViewNameClient: TextView

        init {
            txtViewNumbOrder = itemView.findViewById(R.id.txt_Item_Number_Order_Restaurant)
            txtViewDeliverIn = itemView.findViewById(R.id.txt_Item_Deliver_Order_Restaurant)
            txtViewDate = itemView.findViewById(R.id.txt_Item_date_order_Restaurant)
            txtViewNameClient = itemView.findViewById(R.id.txt_Item_Name_Client_Restaurant)
        }
    }
}