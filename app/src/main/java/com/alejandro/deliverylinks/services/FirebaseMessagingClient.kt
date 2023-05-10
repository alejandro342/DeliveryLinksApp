package com.alejandro.deliverylinks.services

import com.alejandro.deliverylinks.channel.NotificationHelper
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseMessagingClient : FirebaseMessagingService() {

    //notificaciones en segundo plano
    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    //aqui pasan las notificaciones que llegan
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        //capturar la informacion que envian desde el backend
        val data = message.data //un mapa de valores
        val title = data["title"]
        val body = data ["body"]
        val idNotification = data["id_notification"]

        if (!title.isNullOrBlank() && !body.isNullOrBlank() && !idNotification.isNullOrBlank()){
            showNotification(title,body,idNotification)
        }
    }

    //configuaraciones para recibir notificaciones en primer y segundo plano
    private fun showNotification( title:String,body:String,idNotification:String){
        val helper = NotificationHelper(baseContext)
        val builder=helper.getNotification(title, body)
         val id = idNotification.toInt() //transformar idNotification a un entero
        helper.getManager().notify(id,builder.build())
    }
}