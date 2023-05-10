package com.alejandro.deliverylinks.modules.client.ui.adapter

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
import com.alejandro.deliverylinks.modules.client.ui.views.orders.ClientOrdersDetailActivity
import com.alejandro.deliverylinks.modules.client.ui.views.orders.RestaurantOrdersDetailActivity

//listar ordenes del usuario por estado
class AdapterOrdersStatusClient(val mContext: Context, val mOrders: ArrayList<Order>) :
    RecyclerView.Adapter<AdapterOrdersStatusClient.OrdersClientViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersClientViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_orders_status, parent, false)
        return OrdersClientViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: OrdersClientViewHolder, position: Int) {
        val order = mOrders[position] //las direcciones

        holder.txtViewNumbOrder.text = "Orden #${order.id}"
        holder.txtViewDate.text = " ${order.timestamp}"
        //obtener direccion a entregar mostrarla
        holder.txtViewDeliverIn.text = " ${order.address?.address}"

        holder.itemView.setOnClickListener {
            goToOrderDetail(order)
        }
    }
    //mostrar el detalle de la orden con sus producto
private fun goToOrderDetail(order:Order){
    val mIntent = Intent(mContext,ClientOrdersDetailActivity::class.java)
        mIntent.putExtra("order",order.toJson())
        mContext.startActivity(mIntent)
}

    override fun getItemCount(): Int {
        return mOrders.size
    }

    class OrdersClientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val txtViewNumbOrder: TextView
        val txtViewDate: TextView
        val txtViewDeliverIn: TextView

        init {
            txtViewNumbOrder = itemView.findViewById(R.id.txt_Item_Number_Order_Client)
            txtViewDeliverIn = itemView.findViewById(R.id.txt_Item_Deliver_Order)
            txtViewDate = itemView.findViewById(R.id.txt_Item_date_order)
        }
    }

}