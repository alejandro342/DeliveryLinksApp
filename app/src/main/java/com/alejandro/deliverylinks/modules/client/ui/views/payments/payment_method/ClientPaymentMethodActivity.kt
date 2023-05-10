package com.alejandro.deliverylinks.modules.client.ui.views.payments.payment_method

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.alejandro.deliverylinks.R
import com.alejandro.deliverylinks.databinding.ActivityClientPaymentMethodBinding
import com.alejandro.deliverylinks.modules.client.ui.views.payments.mercado_pago.view.form.ClientPaymentsActivity
import com.alejandro.deliverylinks.modules.client.ui.views.payments.paypal.form.ClientPaymentPayPalActivity

class ClientPaymentMethodActivity : AppCompatActivity(), View.OnClickListener {
    private var mBinding: ActivityClientPaymentMethodBinding? = null

    var mToolbar: Toolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityClientPaymentMethodBinding.inflate(layoutInflater)
        val mView: View = mBinding!!.root
        setContentView(mView)
        mToolbar = findViewById(R.id.toolbar)

        mToolbar?.title = "Forma de pago"
        mToolbar?.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
        setSupportActionBar(mToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mBinding!!.ClientPaymentsMercadoPago.setOnClickListener(this)
        mBinding!!.ClientPaymentsPayPal.setOnClickListener(this)
    }

    private fun goToMercadoPago() {
        val mIntent = Intent(this, ClientPaymentsActivity::class.java)
        startActivity(mIntent)
    }

    private fun goToPayPal() {
        val mIntent = Intent(this, ClientPaymentPayPalActivity::class.java)
        startActivity(mIntent)
    }

    override fun onClick(mIntem: View?) {
        when (mIntem) {
            mBinding!!.ClientPaymentsMercadoPago -> {
                goToMercadoPago()
            }
            mBinding!!.ClientPaymentsPayPal -> {
                goToPayPal()
            }
        }
    }
}