package com.alejandro.deliverylinks.modules.client.ui.views.client

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.alejandro.deliverylinks.R
import com.alejandro.deliverylinks.databinding.ActivityClientProductsDetailBinding
import com.alejandro.deliverylinks.models.product.Product
import com.alejandro.deliverylinks.utils.SharedPref
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ClientProductsDetailActivity : AppCompatActivity(), View.OnClickListener {
    private var mBinding: ActivityClientProductsDetailBinding? = null
    var TAG = "ProductsDetailA"

    //mostrar los datos en la vista
    private var mProduct: Product? = null
    val gson = Gson()

    //el control de cantida de productos y precio
    private var counterProducts = 1
    private var priceProducts = 0.0

    //agregar producto a carrito
    private var mSharedPref: SharedPref? = null
    private var mSelectProduct = ArrayList<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityClientProductsDetailBinding.inflate(layoutInflater)
        val mView: View = mBinding!!.root
        setContentView(mView)

        //mostrar los datos en la vista que vienen del adapter al dar click
        mProduct = gson.fromJson(intent.getStringExtra("product"), Product::class.java)
        //agregar producto a carrito
        mSharedPref = SharedPref(this)

        //detalles de producto
        setInfoDetail()

        mBinding!!.imgAddProductDetail.setOnClickListener(this)
        mBinding!!.imgRemoveProductDetail.setOnClickListener(this)
        mBinding!!.btnAddProduct.setOnClickListener(this)

        getProductoSharedPref()
    }

    @SuppressLint("SetTextI18n")
    fun setInfoDetail() {

        //establecer las imagenes al SliderImage
        val slideImageList = ArrayList<SlideModel>()
        slideImageList.add(SlideModel(mProduct?.image1, ScaleTypes.CENTER_CROP))
        slideImageList.add(SlideModel(mProduct?.image2, ScaleTypes.CENTER_CROP))
        slideImageList.add(SlideModel(mProduct?.image3, ScaleTypes.CENTER_CROP))
        mBinding!!.imgSliderProduct.setImageList(slideImageList)

        //estabelcer los textos a los campos
        mBinding!!.txtNameProductDetail.text = mProduct?.name
        mBinding!!.txtDescriptionProductDetail.text = mProduct?.description
        mBinding!!.txtPriceProductDetail.text = "$ ${mProduct?.price}"
    }

    //comparar si el producto ya existe en el carrito editamos la cantidad del producto seleccionado
    private fun getIndexof(idProduct: String): Int {
        var pos = 0
        for (p in mSelectProduct) {
            if (p.id == idProduct) {
                return pos
            }
            pos++
        }
        return -1
    }

    //el control de cantida de productos y precio
    @SuppressLint("SetTextI18n")
    private fun addItem() {
        //aunmentar el contador cada que se llame a este metodo
        counterProducts++
        //incrementar el precio por cada producto agregado
        priceProducts = mProduct?.price!! * counterProducts
        mProduct?.quantity = counterProducts
        mBinding!!.txtCounterProductDetail.text = "${mProduct?.quantity}"
        mBinding!!.txtPriceProductDetail.text = "$ $priceProducts"
    }

    //el control de cantida de productos y precio
    @SuppressLint("SetTextI18n")
    private fun removeItem() {
        //validar que almenos se tenga un producto en la lista
        if (counterProducts > 1) {
            //disminuir el contador cada que se llame a este metodo
            counterProducts--
            //incrementar el precio por cada producto agregado
            priceProducts = mProduct?.price!! * counterProducts
            mProduct?.quantity = counterProducts
            mBinding!!.txtCounterProductDetail.text = "${mProduct?.quantity}"
            mBinding!!.txtPriceProductDetail.text = "$ $priceProducts"
        }

    }

    //obtener los producto guardados en SharedPref
    @SuppressLint("SetTextI18n")
    private fun getProductoSharedPref() {
        //validar si hay un producto
        if (!mSharedPref?.getInformation("order")
                .isNullOrBlank()
        ) {//si existe una orden en SHARED PREF
            //transformar una lista tipo json a tipo product
            val type = object : TypeToken<ArrayList<Product>>() {}.type
            mSelectProduct = gson.fromJson(mSharedPref?.getInformation("order"), type)

            //para aumentar el precio y cantidad del producto en detail si ya existe en el carrito
            val index = getIndexof(mProduct?.id!!)
            if (index != -1) {
                mProduct?.quantity = mSelectProduct[index].quantity
                mBinding!!.txtCounterProductDetail.text = "${mProduct?.quantity}"
                priceProducts = mProduct?.price!! * mProduct?.quantity!!
                mBinding!!.txtPriceProductDetail.text = "$$priceProducts"
                //cambiar texto y color al boton en caso de que el producto ya estixta en el carrito
                mBinding!!.btnAddProduct.setText(R.string.btn_edit_product_detail)
                mBinding!!.btnAddProduct.backgroundTintList = ColorStateList.valueOf(Color.RED)
            }

            //listar los productos seleccionados
            for (p in mSelectProduct) {
                Log.d(TAG, "Shared pref: $p")
            }

        }
    }

    //agregar producto a carrito
    private fun addToBag() {
        val index = getIndexof(mProduct?.id!!) //si existe el indice del producto en SharedPref
        if (index == -1) {//el producto no existe en shared pref
            if (mProduct?.quantity == null) {
                mProduct?.quantity = 1
            }
            mSelectProduct.add(mProduct!!)
        } else {// si ya existe editamos la cantidad
            mSelectProduct[index].quantity = counterProducts

        }
        //para guardar los productos
        mSharedPref?.saveSession("order", mSelectProduct)
        Toast.makeText(this, "Producto agregado", Toast.LENGTH_SHORT).show()
    }

    override fun onClick(mItemView: View?) {
        when (mItemView) {
            mBinding!!.imgAddProductDetail -> addItem()
            mBinding!!.imgRemoveProductDetail -> removeItem()
            mBinding!!.btnAddProduct -> addToBag()
        }
    }
}