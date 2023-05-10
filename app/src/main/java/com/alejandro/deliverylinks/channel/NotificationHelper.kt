package com.alejandro.deliverylinks.channel

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.alejandro.deliverylinks.R

//enviar notificaciones de push de dispositivo a dispositivo
class NotificationHelper(base: Context?) : ContextWrapper(base) {

    private val CHANNEL_ID = "com.alejandro.deliverylinks"
    private val CHANNEL_NAME = "deliverylinks"

    //crear token de noticicaciones
    private var mNManager:NotificationManager?=null
    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//Es igual o mayor a la version OREO de android
            createChannels()
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)//solo se va a ejecutar si la version es OREO o superior
    private fun createChannels() {
        val notificationChannel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationChannel.enableLights(true) //encender luces del dispositivo
        notificationChannel.enableVibration(true) //vibrasion del dispositivo
        notificationChannel.lightColor = Color.BLUE // el color de las luces
        notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE //notificacion privada
        //crear token de noticicaciones
        getManager().createNotificationChannel(notificationChannel)
    }

    //crear token de noticicaciones
    fun getManager():NotificationManager{
        if (mNManager==null){
            mNManager=getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }
        return mNManager as NotificationManager
    }

    //propiedades que se mostraran en las notificaciones
    fun getNotification(title:String, body:String):NotificationCompat.Builder{
        return NotificationCompat.Builder(applicationContext,CHANNEL_ID)
            .setContentTitle(title)//pasar el titulo de la notificación
            .setContentText(body)//pasamos el cuerpo de la notificación
            .setAutoCancel(true)//para podel deslizar la notificacion y eliminarla
            .setColor(Color.GRAY)//establecer color
            .setSmallIcon(R.mipmap.ic_launcher)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body).setBigContentTitle(title))
    }
}