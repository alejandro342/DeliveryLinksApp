package com.alejandro.deliverylinks.modules.client.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.alejandro.deliverylinks.databinding.FragmentClientOrdersStatusBinding
import com.alejandro.deliverylinks.models.orders.Order
import com.alejandro.deliverylinks.models.user.User
import com.alejandro.deliverylinks.modules.client.ui.adapter.AdapterOrdersStatusClient
import com.alejandro.deliverylinks.providers.OrdersProvider
import com.alejandro.deliverylinks.utils.SharedPref
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

//mostar ordenes filtradas por estado
class ClientOrdersStatusFragment : Fragment() {

    private var mView: View? = null
    private var _mBinding: FragmentClientOrdersStatusBinding? = null
    private val mBinding get() = _mBinding

    //traer los datos mostar ordenes filtradas por estado
    var mAdapter : AdapterOrdersStatusClient?=null
    var mOrdersProvider: OrdersProvider? = null
    var mUser: User? = null
    var mSharedPref: SharedPref? = null
    var status = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _mBinding = FragmentClientOrdersStatusBinding.inflate(inflater, container, false)
        mView = mBinding?.root
        mSharedPref= SharedPref(requireActivity())
        //traer los datos mostar ordenes filtradas por estado obtener los argumentos de estado del adapter
        status= arguments?.getString("status")!!

        //mostar ordenes filtradas por estado
        getUserFromSession()
        mOrdersProvider = OrdersProvider(mUser?.sessionToken!!)
        mBinding!!.rcvOrdersStatusClient.layoutManager=LinearLayoutManager(requireContext())

        getOrders()
        return mView
    }

    // traer las ordenes por estado y cliente
    private fun getOrders(){
        mOrdersProvider?.getOrdersByClientAndStatus(mUser?.id!!,status)?.enqueue(object :Callback<ArrayList<Order>>{
            override fun onResponse(
                call: Call<ArrayList<Order>>,
                response: Response<ArrayList<Order>>
            ) {
                if (response.body() != null){
                    val orders = response.body()
                    mAdapter = AdapterOrdersStatusClient(requireContext(),orders!!)
                    mBinding!!.rcvOrdersStatusClient.adapter=mAdapter
                }
            }

            override fun onFailure(call: Call<ArrayList<Order>>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }

        })
    }

    //traer datos del usuario y mostar ordenes filtradas por estado
    fun getUserFromSession() {

        val gson = Gson()
        if (!mSharedPref?.getInformation("user").isNullOrBlank()) {
            //si el usuario esta en session
            mUser = gson.fromJson(mSharedPref?.getInformation("user"), User::class.java)

        }
    }

}