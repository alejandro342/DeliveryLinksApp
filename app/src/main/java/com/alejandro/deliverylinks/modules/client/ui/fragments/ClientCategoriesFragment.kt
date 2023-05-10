package com.alejandro.deliverylinks.modules.client.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.alejandro.deliverylinks.R
import com.alejandro.deliverylinks.databinding.FragmentClientCategoriesBinding
import com.alejandro.deliverylinks.models.category.Category
import com.alejandro.deliverylinks.models.user.User
import com.alejandro.deliverylinks.modules.client.ui.views.client.ClientShoppingBagActivity
import com.alejandro.deliverylinks.modules.restaurant.ui.adapter.AdapterSelectCategories
import com.alejandro.deliverylinks.providers.CategoriesProvider
import com.alejandro.deliverylinks.utils.SharedPref
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ClientCategoriesFragment : Fragment() {

    val TAG = "CategoriesFragment"
    private var mView: View? = null
    private var _mBinding: FragmentClientCategoriesBinding? = null
    private val mBinding get() = _mBinding

    private var mAdapterCategories: AdapterSelectCategories? = null
    private var mCategoriesProvider: CategoriesProvider? = null
    private var mUser: User? = null
    private var mSharedPref: SharedPref? = null
    private var mCategories = ArrayList<Category>()
    var mToolbar: Toolbar? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _mBinding = FragmentClientCategoriesBinding.inflate(inflater, container, false)
        mView = mBinding?.root

        setHasOptionsMenu(true) //habilitar las opciones del menu

        mToolbar = mView?.findViewById(R.id.toolbar)
        mToolbar?.setTitleTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        mToolbar?.title = "Categorías"
        //para que el fragment soporte el toolbar
        (activity as AppCompatActivity).setSupportActionBar(mToolbar)

        mBinding!!.rcviewCategories.layoutManager = LinearLayoutManager(requireContext())

        mSharedPref = SharedPref(requireActivity())
        getUserFromSession()
        mCategoriesProvider = CategoriesProvider(mUser?.sessionToken!!)

        getCategories()
        return mView
    }

    //trabajar con opciones del menu inflar el menu
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_shoping_bag,menu) //intanciar el archivo que se creo de menu
        super.onCreateOptionsMenu(menu, inflater)
    }
    //para las aciones qeu realizara el menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId ==R.id.item_shoping_bag){
            gotToShopingBag()
        }

        return super.onOptionsItemSelected(item)
    }
    private fun gotToShopingBag(){
        val mIntent = Intent(requireContext(), ClientShoppingBagActivity::class.java)
        requireContext().startActivity(mIntent)
    }

    private fun getCategories() {
        mCategoriesProvider?.getCategories()?.enqueue(object : Callback<ArrayList<Category>> {
            override fun onResponse(
                call: Call<ArrayList<Category>>,
                response: Response<ArrayList<Category>>
            ) {
                if (response.body() != null) {
                    mCategories = response.body()!!
                    mAdapterCategories = AdapterSelectCategories(requireContext(), mCategories)
                    mBinding!!.rcviewCategories.adapter = mAdapterCategories
                }
            }

            override fun onFailure(call: Call<ArrayList<Category>>, t: Throwable) {
                Log.d(TAG, "Error:${t.message}")
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun getUserFromSession() {
        val gson = Gson()
        if (!mSharedPref?.getInformation("user").isNullOrBlank()) {
            //si el usuario esta en session
            mUser = gson.fromJson(mSharedPref?.getInformation("user"), User::class.java)
            //  Log.d("ClientProfileFragment", "Usuario $mUser")
        }
    }
}