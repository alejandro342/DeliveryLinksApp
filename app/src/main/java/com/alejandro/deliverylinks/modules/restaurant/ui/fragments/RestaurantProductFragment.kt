package com.alejandro.deliverylinks.modules.restaurant.ui.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.alejandro.deliverylinks.R
import com.alejandro.deliverylinks.databinding.FragmentRestaurantProductBinding
import com.alejandro.deliverylinks.models.category.Category
import com.alejandro.deliverylinks.models.product.Product
import com.alejandro.deliverylinks.models.response.ResponseHttp
import com.alejandro.deliverylinks.models.user.User
import com.alejandro.deliverylinks.modules.client.ui.dialogs.DialogCreatedProduct
import com.alejandro.deliverylinks.providers.CategoriesProvider
import com.alejandro.deliverylinks.providers.ProductsProvider
import com.alejandro.deliverylinks.utils.MyProgressBar
import com.alejandro.deliverylinks.utils.SharedPref
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.ybq.android.spinkit.sprite.Sprite
import com.github.ybq.android.spinkit.style.WanderingCubes
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class RestaurantProductFragment : Fragment(), View.OnClickListener, MyProgressBar,MyProgressBar.progress {

    private var mView: View? = null
    private var _mBinding: FragmentRestaurantProductBinding? = null
    private val mBinding get() = _mBinding
    val TAG = "ProductFragment"

    //para el manejo de imagenes
    private var mImageFile1: File? = null
    private var mImageFile2: File? = null
    private var mImageFile3: File? = null

    //el registro de un producto
    private var mProductsProvider: ProductsProvider? = null

    //para mostrar categorias en el splinner
    private var mCategoriesProvider: CategoriesProvider? = null
    private var mUser: User? = null
    private var mSharedPref: SharedPref? = null
    private var mCategories = ArrayList<Category>()

    //para selecion de categorias en el splinner
    var idCategory = ""

    //progressDialog
    private var mSpinkitProgress: Sprite? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _mBinding = FragmentRestaurantProductBinding.inflate(inflater, container, false)
        mView = mBinding?.root

        mBinding!!.ImgProduct1.setOnClickListener(this)
        mBinding!!.ImgProduct2.setOnClickListener(this)
        mBinding!!.ImgProduct3.setOnClickListener(this)
        mBinding!!.btnSaveProduct.setOnClickListener(this)

        mSpinkitProgress = WanderingCubes()
        mBinding!!.progressBarSpinKit.setIndeterminateDrawable(mSpinkitProgress)
        //para mostrar categorias en el splinner
        mSharedPref = SharedPref(requireActivity())
        getUserFromSession()
        mCategoriesProvider = CategoriesProvider(mUser?.sessionToken!!)
        //el registro de un producto
        mProductsProvider = ProductsProvider(mUser?.sessionToken!!)
        getCategories()
        return mView
    }

    override fun onClick(mItem: View?) {
        when (mItem) {
            mBinding!!.ImgProduct1 -> {
                selectImage(101)
            }
            mBinding!!.ImgProduct2 -> {
                selectImage(102)
            }
            mBinding!!.ImgProduct3 -> {
                selectImage(103)
            }
            mBinding!!.btnSaveProduct -> {
                createProduct()
            }
        }
    }

    fun createProduct() {
        val mName = mBinding!!.txtRegisterNameProduct.text.toString().trim()
        val mDescription = mBinding!!.txtRegisterDescriptionProduct.text.toString().trim()
        val mPrice = mBinding!!.txtRegisterPriceProduct.text.toString().trim()
        //el registro de un producto
        val mFiles = ArrayList<File>()


        if (isValidForm(mName, mDescription, mPrice)) {
            //el registro de un producto
            val product = Product(
                name = mName,
                description = mDescription,
                price = mPrice.toDouble(),
                idCategory = idCategory
            )

            mFiles.add(mImageFile1!!)
            mFiles.add(mImageFile2!!)
            mFiles.add(mImageFile3!!)

            showProgressBar()
            hideBottom()
            mProductsProvider?.createProduct(mFiles, product)
                ?.enqueue(object : Callback<ResponseHttp> {
                    override fun onResponse(
                        call: Call<ResponseHttp>,
                        response: Response<ResponseHttp>
                    ) {
                        hideProgressBar()
                        Log.d(TAG, "Response :${response}")
                        Log.d(TAG, "Body : ${response.body()}")
                        Toast.makeText(
                            requireContext(),
                            response.body()?.message,
                            Toast.LENGTH_SHORT
                        ).show()
                        if (response.body()?.isSuccess == true) {
                            resetData()
                            showDialog()
                            showBottom()
                        }
                    }

                    override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                        hideProgressBar()
                        showBottom()
                        Log.d(TAG, "Error:${t.message}")
                        Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT)
                            .show()
                    }
                })
             }
    }

    //limpiart los campos
    private fun resetData() {
        mBinding!!.txtRegisterNameProduct.setText("")
        mBinding!!.txtRegisterDescriptionProduct.setText("")
        mBinding!!.txtRegisterPriceProduct.setText("")

        mImageFile1 = null
        mImageFile2 = null
        mImageFile3 = null

        mBinding!!.ImgProduct1.setImageResource(R.drawable.ic_image)
        mBinding!!.ImgProduct2.setImageResource(R.drawable.ic_image)
        mBinding!!.ImgProduct3.setImageResource(R.drawable.ic_image)
    }

    fun isValidForm(name: String, description: String, price: String): Boolean {
        if (name.isNullOrBlank()) {
            Toast.makeText(requireContext(), "Campo nombre vacio", Toast.LENGTH_SHORT).show()
            return false
        }
        if (description.isNullOrBlank()) {
            Toast.makeText(requireContext(), "Campo descripción vacio", Toast.LENGTH_SHORT).show()
            return false
        }
        if (price.isNullOrBlank()) {
            Toast.makeText(requireContext(), "Campo precio vacio", Toast.LENGTH_SHORT).show()
            return false
        }
        if (mImageFile1 == null) {
            Toast.makeText(requireContext(), "Campo imagen 1 vacio", Toast.LENGTH_SHORT).show()
            return false
        }
        if (mImageFile2 == null) {
            Toast.makeText(requireContext(), "Campo imagen 2 vacio", Toast.LENGTH_SHORT).show()
            return false
        }
        if (mImageFile3 == null) {
            Toast.makeText(requireContext(), "Campo imagen 3 vacio", Toast.LENGTH_SHORT).show()
            return false
        }
        if (idCategory.isNullOrBlank()) {
            Toast.makeText(
                requireContext(),
                "Selecciona la categoria del producto",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        return true
    }


    //para el manejo de dos o mas imagenes
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {

            val mFieldUri = data?.data

            if (requestCode == 101) {
                mImageFile1 =
                    mFieldUri?.path?.let { File(it) } //archivo qeu se guardara en el servidor
                mBinding!!.ImgProduct1.setImageURI(mFieldUri)
            } else if (requestCode == 102) {
                mImageFile2 =
                    mFieldUri?.path?.let { File(it) } //archivo qeu se guardara en el servidor
                mBinding!!.ImgProduct2.setImageURI(mFieldUri)
            } else if (requestCode == 103) {
                mImageFile3 =
                    mFieldUri?.path?.let { File(it) } //archivo qeu se guardara en el servidor
                mBinding!!.ImgProduct3.setImageURI(mFieldUri)
            }
        } else if (requestCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Acccion cancelada", Toast.LENGTH_SHORT).show()
        }
    }

    //para el manejo de dos o mas imagenes
    fun selectImage(requestCode: Int) {
        ImagePicker.with(this)
            //para cortar la imagen
            .crop()
            //comprime la image
            .compress(1024)
            //maximo para imagenes
            .maxResultSize(1080, 1080)
            .start(requestCode)
    }

    fun getUserFromSession() {
        val gson = Gson()
        if (!mSharedPref?.getInformation("user").isNullOrBlank()) {
            //si el usuario esta en session
            mUser = gson.fromJson(mSharedPref?.getInformation("user"), User::class.java)

        }
    }

    //llenar el spiner
    private fun getCategories() {
        mCategoriesProvider?.getCategories()?.enqueue(object : Callback<ArrayList<Category>> {
            override fun onResponse(
                call: Call<ArrayList<Category>>,
                response: Response<ArrayList<Category>>
            ) {
                if (response.body() != null) {
                    mCategories = response.body()!!
                    val mArrayAdapter = ArrayAdapter(
                        requireActivity(),
                        android.R.layout.simple_dropdown_item_1line,
                        mCategories
                    )
                    mBinding!!.SpinnerCategories.adapter = mArrayAdapter
                    //escuchador a lo que seleccione el usuario
                    mBinding!!.SpinnerCategories.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                adapterView: AdapterView<*>?,
                                view: View?,
                                position: Int,
                                l: Long
                            ) {
                                idCategory = mCategories[position].id!!
                                Log.d(TAG, "Id category ${idCategory}")
                            }

                            override fun onNothingSelected(p0: AdapterView<*>?) {
                            }
                        }
                }
            }

            override fun onFailure(call: Call<ArrayList<Category>>, t: Throwable) {
                Log.d(TAG, "Error:${t.message}")
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun showProgressBar() {
        mBinding!!.progressBarSpinKit.setVisibility(View.VISIBLE)
    }

    override fun hideProgressBar() {
        mBinding!!.progressBarSpinKit.setVisibility(View.GONE)
    }

    override fun showBottom() {
        mBinding!!.btnSaveProduct.setVisibility(View.VISIBLE)
    }

    override fun hideBottom() {
        mBinding!!.btnSaveProduct.setVisibility(View.GONE)
    }

    fun showDialog() {
        DialogCreatedProduct { saveProduct ->

        }.show(parentFragmentManager, "dialog")
    }
}