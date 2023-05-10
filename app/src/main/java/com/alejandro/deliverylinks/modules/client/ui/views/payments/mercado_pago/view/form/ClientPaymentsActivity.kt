package com.alejandro.deliverylinks.modules.client.ui.views.payments.mercado_pago.view.form

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.alejandro.deliverylinks.databinding.ActivityClientPaymentsBinding
import com.alejandro.deliverylinks.models.mercadopago.Cardholder
import com.alejandro.deliverylinks.models.mercadopago.MercadoPagoCardTokenBody
import com.alejandro.deliverylinks.modules.client.ui.views.payments.mercado_pago.view.ClientPaymentsInstallmentsActivity
import com.alejandro.deliverylinks.providers.mercadopago.MercadoPagoProvider
import com.google.gson.JsonObject
import io.stormotion.creditcardflow.CardFlowState
import io.stormotion.creditcardflow.CreditCard
import io.stormotion.creditcardflow.CreditCardFlowListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ClientPaymentsActivity : AppCompatActivity() {

    private var mBinding: ActivityClientPaymentsBinding? = null

    //datos de tarjeta
    var mCvv = "" //CVC
    var mCardHolder = ""//titular
    var mCardNumber = ""// numero de tarjeta
    var mCardExpiration = "" //fecha
    var TAG = " ClientPayments"

    //manejo de mercado pago
    var mMercadoPagoProvider: MercadoPagoProvider = MercadoPagoProvider()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityClientPaymentsBinding.inflate(layoutInflater)
        val mView: View = mBinding!!.root
        setContentView(mView)
        card()

    }


    fun card() {

        mBinding!!.mCreditCardFlow.setCreditCardFlowListener(object : CreditCardFlowListener {
            override fun onActiveCardNumberBeforeChangeToNext() {

            }

            override fun onActiveCardNumberBeforeChangeToPrevious() {

            }

            override fun onCardCvvBeforeChangeToNext() {

            }

            override fun onCardCvvBeforeChangeToPrevious() {

            }

            override fun onCardCvvValidatedSuccessfully(cvv: String) {

            }

            override fun onCardCvvValidationFailed(cvv: String) {

            }

            override fun onCardExpiryDateBeforeChangeToNext() {

            }

            override fun onCardExpiryDateBeforeChangeToPrevious() {

            }

            override fun onCardExpiryDateInThePast(expiryDate: String) {

            }

            override fun onCardExpiryDateValidatedSuccessfully(expiryDate: String) {

            }

            override fun onCardExpiryDateValidationFailed(expiryDate: String) {

            }

            override fun onCardHolderBeforeChangeToNext() {

            }

            override fun onCardHolderBeforeChangeToPrevious() {

            }

            override fun onCardHolderValidatedSuccessfully(cardHolder: String) {

            }

            override fun onCardHolderValidationFailed(cardholder: String) {

            }

            override fun onCardNumberValidatedSuccessfully(cardNumber: String) {

            }

            override fun onCardNumberValidationFailed(cardNumber: String) {

            }

            override fun onCreditCardFlowFinished(creditCard: CreditCard) {

                //obtener la informacion de la tarjeta
                mCvv = creditCard.expiryDate.toString()
                mCardHolder = creditCard.cvc.toString()
                mCardNumber = creditCard.number.toString()
                mCardExpiration = creditCard.holderName.toString()

                createCardTokent()

                Log.d(TAG, "CVV:$mCvv")
                Log.d(TAG, "CardHolder:$mCardHolder")
                Log.d(TAG, "mCardNumber:$mCardNumber")
                Log.d(TAG, "CardExpiration:$mCardExpiration")

            }

            override fun onFromActiveToInactiveAnimationStart() {

            }

            override fun onFromInactiveToActiveAnimationStart() {

            }

            override fun onInactiveCardNumberBeforeChangeToNext() {

            }

            override fun onInactiveCardNumberBeforeChangeToPrevious() {

            }

        })
    }

    private fun createCardTokent() {
        //separar año y mes de la tarjeta
        val expiration = mCardExpiration.split("/").toTypedArray()
        val mont = expiration[0]
        //agregar los digitos faltantes al año
        val year = "20${expiration[1]}"
        //eliminar los espacios en blanco de la tarjeta
        mCardNumber = mCardNumber.replace(" ", "")

        val ch = Cardholder(name = mCardHolder)
        val mercadoPagoCardTokenBody = MercadoPagoCardTokenBody(
            securityCode = mCvv,
            expirationYear = year,
            expirationMonth = mont.toInt(),
            cardNumber = mCardNumber,
            cardHolder = ch
        )

        mMercadoPagoProvider.createCardToken(mercadoPagoCardTokenBody)
            ?.enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {

                    if (response.body() != null) {
                        //para traer datos del response
                        val cardToken = response.body()?.get("id")?.asString
                        val firstSixDigits = response.body()?.get("first_six_digits")?.asString
                        goToInstallments(cardToken!!, firstSixDigits!!)
                    }

                    Log.d(TAG, "Response: ${response}")
                    Log.d(TAG, "Body: ${response.body()}")
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Toast.makeText(
                        this@ClientPaymentsActivity,
                        "Error:${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            })
    }

    //para pasar los 6 digitos de la tarjeta a la otra actividad
    private fun goToInstallments(cardToken: String, firstSixDigits: String) {
        val mIntent = Intent(this, ClientPaymentsInstallmentsActivity::class.java)
        //mandar datos
        mIntent.putExtra("cardToken", cardToken)
        mIntent.putExtra("firstSixDigits", firstSixDigits)
        startActivity(mIntent)
    }

    override fun onBackPressed() {
        if (mBinding!!.mCreditCardFlow.currentState() == CardFlowState.ACTIVE_CARD_NUMBER || mBinding!!.mCreditCardFlow.currentState() == CardFlowState.INACTIVE_CARD_NUMBER) {
            finish()
        } else {
            mBinding!!.mCreditCardFlow.previousState()
        }
    }
}