package com.alejandro.deliverylinks.utils

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.alejandro.deliverylinks.providers.UsersProvider
import com.github.dhaval2404.imagepicker.ImagePicker
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File

class ImageUtils(mContext: Context) : AppCompatActivity() {
    var mContext: Context? = null

    //para el manejo de imagenes
    private var mImageFile: File? = null
    var mUsersProvider = UsersProvider()
    var TAG = "UpdateProfileActivityView"
    var mImage: CircleImageView? = null

    init {
        mImage
        this.mContext = mContext
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

       selectImage()
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
                mImageFile = File(mFieldUri?.path) //archivo qeu se guardara en el servidor
                mImage?.setImageURI(mFieldUri)
            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Proceso cancelado", Toast.LENGTH_LONG).show()
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

}