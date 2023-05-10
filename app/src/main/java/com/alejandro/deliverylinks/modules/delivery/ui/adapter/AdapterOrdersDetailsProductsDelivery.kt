package com.alejandro.deliverylinks.modules.delivery.ui.adapter

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
import com.alejandro.deliverylinks.modules.delivery.ui.views.orders.DeliveryOrdersDetailActivity

//mostrar el detalle de la orden con sus productos
class AdapterOrdersDetailsProductsDelivery(val mContext: Context, val mOrders: ArrayList<Order>) :
    RecyclerView.Adapter<AdapterOrdersDetailsProductsDelivery.OrderDetailProductsDeliveryViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OrderDetailProductsDeliveryViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_orders_products_details_delivery, parent, false)
        return OrderDetailProductsDeliveryViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: OrderDetailProductsDeliveryViewHolder, position: Int) {
        val order = mOrders[position] //las direcciones

        //obtener direccion a entregar mostrarla
        holder.txtViewNumberOrderDelivery.text = "Order #${order.id}"
        holder.txtViewDateOrderDelivery.text = " ${order.timestamp}"
        holder.txtViewDeliveryOrder.text = " ${order.address?.address}"
        holder.txtNameClientOrderDelivery.text = " ${order.client?.name} ${order.client?.lastname}"

        holder.itemView.setOnClickListener {
            goToOrderDetail(order)
        }
    }

    //mostrar el detalle de la orden con sus producto
    private fun goToOrderDetail(order: Order) {
        val mIntent = Intent(mContext, DeliveryOrdersDetailActivity::class.java)
        mIntent.putExtra("order", order.toJson())
        mContext.startActivity(mIntent)
    }

    override fun getItemCount(): Int {
        return mOrders.size
    }

    class OrderDetailProductsDeliveryViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val txtViewNumberOrderDelivery: TextView
        val txtViewDateOrderDelivery: TextView
        val txtViewDeliveryOrder: TextView
        val txtNameClientOrderDelivery: TextView

        init {
            txtViewNumberOrderDelivery = itemView.findViewById(R.id.txt_Item_Number_Order_Delivery)
            txtViewDateOrderDelivery = itemView.findViewById(R.id.txt_Item_Date_Order_Delivery)
            txtViewDeliveryOrder = itemView.findViewById(R.id.txt_Item_Deliver_Order_Delivery)
            txtNameClientOrderDelivery = itemView.findViewById(R.id.txt_Item_Name_Client_Delivery)


        }
    }
}