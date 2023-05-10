package com.alejandro.deliverylinks.modules.restaurant.ui.fragments

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.alejandro.deliverylinks.databinding.FragmentRestaurantCategoryBinding
import com.alejandro.deliverylinks.models.category.Category
import com.alejandro.deliverylinks.models.response.ResponseHttp
import com.alejandro.deliverylinks.models.user.User
import com.alejandro.deliverylinks.providers.CategoriesProvider
import com.alejandro.deliverylinks.utils.SharedPref
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


class RestaurantCategoryFragment : Fragment(), View.OnClickListener {

    private var mView: View? = null
    private var _mBinding: FragmentRestaurantCategoryBinding? = null
    private val mBinding get() = _mBinding

    private var mCategoriesProvider: CategoriesProvider? = null
    private var sharedPref: SharedPref? = null
    private var mUser: User? = null

    //para el manejo de imagenes
    private var mImageFile: File? = null

    var TAG = "CategoryFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _mBinding = FragmentRestaurantCategoryBinding.inflate(inflater, container, false)
        mView = mBinding?.root

        mBinding!!.ImgCategoryRestaurant.setOnClickListener(this)
        mBinding!!.BtnSaveCategoryRestaurant.setOnClickListener { createCategory() }

        sharedPref = SharedPref(requireContext())
        getUserFromSession()
        mCategoriesProvider = CategoriesProvider(mUser?.sessionToken!!)
        return mView
    }

    fun createCategory() {

        val category =
            Category(name = mBinding!!.TxtNameCategoryRestaurant.text.toString().trim())
        if (category.name.isEmpty()) {
            Toast.makeText(requireContext(), "Campo nombre vacio", Toast.LENGTH_SHORT).show()
        } else
            if (mImageFile != null) {

                mCategoriesProvider?.createCategory(mImageFile!!, category)
                    ?.enqueue(object : Callback<ResponseHttp> {

                        override fun onResponse(
                            call: Call<ResponseHttp>,
                            response: Response<ResponseHttp>
                        ) {
                            Log.d(TAG, "RESPONSE : ${response}")
                            Log.d(TAG, "BODY : ${response.body()}")

                            Toast.makeText(
                                requireContext(),
                                response.body()?.message,
                                Toast.LENGTH_SHORT
                            ).show()

                        }

                        override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                            Log.d(TAG, "Error : ${t.message}")
                            Toast.makeText(
                                requireContext(),
                                "Error: ${t.message}",
                                Toast.LENGTH_LONG
                            )
                                .show()
                        }
                    })
            } else {
                Toast.makeText(requireContext(), "Seleccione una imagen", Toast.LENGTH_SHORT).show()
            }
    }

    //para el manejo de imagenes
    private val mStarImageForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            //data que devuelve la funcion select de la imagen que seleccione el usuario
            val data = result.data
            //se crea un archivo
            if (resultCode == Activity.RESULT_OK) {
                val mFieldUri = data?.data
                mImageFile =
                    mFieldUri?.path?.let { File(it) } //archivo qeu se guardara en el servidor
                mBinding!!.ImgCategoryRestaurant.setImageURI(mFieldUri)
            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_LONG)
                    .show()
            } else {
                Toast.makeText(requireContext(), "Proceso cancelado", Toast.LENGTH_LONG).show()
            }
        }

    //para el manejo de imagenes
    fun selectImage() {
        ImagePicker.with(this)
            //para cortar la imagen
            .crop()
            //comprime la image
            .compress(1024)
            //maximo para imagenes
            .maxResultSize(1080, 1080)
            .createIntent { mIntent ->
                mStarImageForResult.launch(mIntent)

            }
    }

    //trear los datos de session del usuario
    fun getUserFromSession() {

        val gson = Gson()
        if (!sharedPref?.getInformation("user").isNullOrBlank()) {
            //si el usuario esta en session
            mUser = gson.fromJson(sharedPref?.getInformation("user"), User::class.java)
            //  Log.d("ClientProfileFragment", "Usuario $mUser")
        }
    }

    override fun onClick(mItem: View?) {
        when (mItem) {
            mBinding?.ImgCategoryRestaurant -> {
                selectImage()
            }
        }
    }

}