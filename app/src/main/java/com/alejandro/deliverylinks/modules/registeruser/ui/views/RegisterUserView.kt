package com.alejandro.deliverylinks.modules.registeruser.ui.views

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.alejandro.deliverylinks.databinding.ActivityRegisterUserBinding
import com.alejandro.deliverylinks.modules.registeruser.presenter.RegisterUserPresenter

class RegisterUserView : AppCompatActivity(), View.OnClickListener {

    var mBinding: ActivityRegisterUserBinding? = null
    var mRegisterUserPresenter: RegisterUserPresenter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityRegisterUserBinding.inflate(layoutInflater)
        val view: View = mBinding!!.root
        setContentView(view)
        mRegisterUserPresenter = RegisterUserPresenter(this)
        mBinding!!.backLogin.setOnClickListener(this)
        mBinding!!.btnSaveUser.setOnClickListener(this)
    }

    override fun onClick(mOption: View?) {
        when (mOption) {
            mBinding!!.backLogin -> mRegisterUserPresenter!!.backLogin()

            mBinding!!.btnSaveUser -> mRegisterUserPresenter!!.validateData(
                mBinding!!.txtRegisterNameUser.text.toString().trim(),
                mBinding!!.txtRegisterLastnameUser.text.toString().trim(),
                mBinding!!.txtRegisterEmailUser.text.toString().trim(),
                mBinding!!.txtRegisterTelephoneUser.text.toString().trim(),
                mBinding!!.txtRegisterPasswordUser.text.toString().trim(),
                mBinding!!.txtRegisterRepeatPasswordUser.text.toString().trim()
            )
        }
    }
}