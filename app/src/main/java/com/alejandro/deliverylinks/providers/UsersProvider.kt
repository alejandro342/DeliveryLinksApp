package com.alejandro.deliverylinks.providers

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.alejandro.deliverylinks.api.ApiRoutes
import com.alejandro.deliverylinks.models.category.Category
import com.alejandro.deliverylinks.models.response.ResponseHttp
import com.alejandro.deliverylinks.models.user.User
import com.alejandro.deliverylinks.routes.UsersRoutes
import com.alejandro.deliverylinks.utils.SharedPref
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

//enviar los datos ya con el token debe recibir un parametro token no debe ser obligatorio
class UsersProvider(val token: String? = null) {

    private var usersRoutes: UsersRoutes? = null

    //enviar los datos ya con el token
    private var usersRoutesToken: UsersRoutes? = null

   //notifications
    val TAG ="UsersProvider"
    init {
        val api = ApiRoutes()
        usersRoutes = api.getUsersRoutes()
        //validar token diferente de nulo
        if (token != null) {
            //enviar los datos ya con el token
            usersRoutesToken = api.getUsersRoutesWitToken(token)
        }
    }

    fun registerUsers(user: User): Call<ResponseHttp>? {
        return usersRoutes?.registerUser(user)
    }

    fun loginUsers(email: String, password: String): Call<ResponseHttp>? {
        return usersRoutes?.loginUser(email, password)
    }

    fun updateUser(file: File, user: User): Call<ResponseHttp>? {
        val reqFile = RequestBody.create(MediaType.parse("image/*"), file)
        val image = MultipartBody.Part.createFormData("image", file.name, reqFile)
        val requestBody = RequestBody.create(MediaType.parse("text/plain"), user.toJson())
        return usersRoutesToken?.updateUser(
            image,
            requestBody,
            token!!
        ) //enviar los datos ya con el token le pasamos el token
    }

    //actualizar datos sin image
    fun updateWithoutImage(user: User): Call<ResponseHttp>? {
        return usersRoutesToken?.updateWithoutImage(
            user,
            token!!
        ) //enviar los datos ya con el token le pasamos el token
    }

    //actualizar notification_token
    fun updateNotificationToken(user: User): Call<ResponseHttp>? {
        return usersRoutesToken?.updateNotificationToken(user,token!!)
    }
    //obtener rol Delivery
    fun getDeliveryMen():Call<ArrayList<User>>?{
        return usersRoutesToken?.getDeliveryMen(token!!)
    }

    //crear y guardar token de notificaciones
    fun createTokenNotifications(user: User, mContext:Context){
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            val token = task.result
            //guardar el token en el usuario de sesion
            val mSharedPre = SharedPref(mContext)
            user.notificationToken = token
            mSharedPre.saveSession("user",user)
            updateNotificationToken(user)?.enqueue(object :Callback<ResponseHttp>{
                override fun onResponse(
                    call: Call<ResponseHttp>,
                    response: Response<ResponseHttp>
                ) {
                    if (response.body() ==null){
                        Log.d(TAG,"Error al crear el token")
                    }
                }

                override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                    Toast.makeText(mContext, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }

            })//mandar la peticion al servidor

            Log.d(TAG,"TOKEN DE NOTIFICACIONES $token")
        })
    }
}