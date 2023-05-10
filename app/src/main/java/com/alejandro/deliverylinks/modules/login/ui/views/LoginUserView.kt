package com.alejandro.deliverylinks.modules.login.ui.views

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.alejandro.deliverylinks.databinding.ActivityLoginUserBinding
import com.alejandro.deliverylinks.modules.login.interfaces.LoginUserInterface
import com.alejandro.deliverylinks.modules.login.presenter.LoginPresenter
import com.alejandro.deliverylinks.utils.MyProgressBar
import com.github.ybq.android.spinkit.sprite.Sprite
import com.github.ybq.android.spinkit.style.DoubleBounce
import com.github.ybq.android.spinkit.style.WanderingCubes

class LoginUserView : AppCompatActivity(), View.OnClickListener,MyProgressBar,MyProgressBar.progress {

    private var mBinding: ActivityLoginUserBinding? = null
    private var mLoginPresenter: LoginPresenter? = null
    private var mSpinkitProgress: Sprite? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityLoginUserBinding.inflate(layoutInflater)
        val view: View = mBinding!!.root
        setContentView(view)

        mSpinkitProgress= WanderingCubes()
        mBinding!!.progressBarSpinKit.setIndeterminateDrawable(mSpinkitProgress)
        mLoginPresenter = LoginPresenter(this,this,this)
        mBinding!!.registerUser.setOnClickListener(this)
        mBinding!!.btnLogin.setOnClickListener(this)
        mLoginPresenter!!.getUserFromSessionLogin()
    }

    override fun onClick(p0: View?) {
        when (p0) {
            mBinding!!.registerUser -> mLoginPresenter?.goRegisterUser()
            mBinding!!.btnLogin -> mLoginPresenter?.validateData(
                mBinding!!.editTEmailLogin.text.toString().trim(),
                mBinding!!.editTPasswordLogin.text.toString().trim()
            )
        }
    }

    override fun showProgressBar() {
        mBinding!!.progressBarSpinKit.setVisibility(View.VISIBLE)
    }

    override fun hideProgressBar() {
        mBinding!!.progressBarSpinKit.setVisibility(View.GONE)
    }

    override fun showBottom() {
        mBinding!!.btnLogin.setVisibility(View.VISIBLE)
    }

    override fun hideBottom() {
        mBinding!!.btnLogin.setVisibility(View.GONE)
    }
}