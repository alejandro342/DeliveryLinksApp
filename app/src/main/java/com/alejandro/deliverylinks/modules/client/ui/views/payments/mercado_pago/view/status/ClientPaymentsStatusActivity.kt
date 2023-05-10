package com.alejandro.deliverylinks.modules.client.ui.views.payments.mercado_pago.view.status

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.alejandro.deliverylinks.R
import com.alejandro.deliverylinks.databinding.ActivityClientPaymentsStatusBinding
import com.alejandro.deliverylinks.modules.client.ui.views.client.ClientHomeActivity

class ClientPaymentsStatusActivity : AppCompatActivity(),View.OnClickListener {
    private var mBinding: ActivityClientPaymentsStatusBinding? = null

    //para recuperar los datos que mandamos de PaymentsInstallments
    var mPaymentMethodId=""
    var mPaymentStatus=""
    var mLastFourDigits=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityClientPaymentsStatusBinding.inflate(layoutInflater)
        val mView: View = mBinding!!.root
        setContentView(mView)
        mBinding!!.btnFinishBuy.setOnClickListener(this)
        getData()
    }

    //obtener los datos
    @SuppressLint("SetTextI18n")
    private fun getData(){
        mPaymentMethodId =intent.getStringExtra("paymentMethodId").toString()
        mPaymentStatus=intent.getStringExtra("paymentStatus").toString()
        mLastFourDigits=intent.getStringExtra("lastFourDigits").toString()

        //validar el status de la compra
        if (mPaymentStatus =="approved"){
            mBinding!!.imgCircleStatusYourBuys.setImageResource(R.drawable.ic_check_circle)
            mBinding!!.TxtStatusYourBuys.text="Tu orden fue procesada exitosamente usando (${mPaymentMethodId} *** ${mLastFourDigits}) \n\n Mira el estado de tu compra en la sección de Mis Pedidos"
        }else{
            mBinding!!.imgCircleStatusYourBuys.setImageResource(R.drawable.ic_cancel)
            mBinding!!.TxtStatusYourBuys.text="Hubo un error al procesar el pago"
        }
    }

    private fun goToHome(){
        val mIntent=Intent(this, ClientHomeActivity::class.java)
        mIntent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(mIntent)
    }
    override fun onClick(mItem: View?) {
        when(mItem){
            mBinding!!.btnFinishBuy -> goToHome()
        }
    }
}