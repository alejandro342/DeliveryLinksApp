package com.alejandro.deliverylinks.modules.client.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.alejandro.deliverylinks.databinding.FragmentClientProfileBinding
import com.alejandro.deliverylinks.models.user.User
import com.alejandro.deliverylinks.modules.client.presenter.ClientHomePresenter
import com.alejandro.deliverylinks.modules.client.ui.dialogs.DialogCloseSession
import com.alejandro.deliverylinks.modules.client.ui.dialogs.DialogSaveUser
import com.alejandro.deliverylinks.utils.SharedPref
import com.google.gson.Gson
import com.squareup.picasso.Picasso

class ClientProfileFragment : Fragment(), View.OnClickListener {

    private var mView: View? = null
    private var _mBinding: FragmentClientProfileBinding? = null
    private val mBinding get() = _mBinding
    private var mClientHomePresenter: ClientHomePresenter? = null
    private var sharedPref: SharedPref? = null
    private var mUser: User? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _mBinding = FragmentClientProfileBinding.inflate(inflater, container, false)
        mView = mBinding?.root

        sharedPref = SharedPref(requireActivity())
        mClientHomePresenter = ClientHomePresenter(requireContext())
        mClientHomePresenter!!.getUserFromSession()
        mBinding?.btnSelectRoll?.setOnClickListener(this)
        mBinding?.btnEditProfile?.setOnClickListener(this)
        mBinding?.imgCloseSession?.setOnClickListener(this)

        getUserFromSession()
        setDataProfile()

        return mView
    }

    @SuppressLint("SetTextI18n")
    fun setDataProfile() {
        mBinding?.txtNameProfile?.text = "${mUser?.name} ${mUser?.lastname}"
        mBinding?.txtEmailProfile?.text = mUser?.email
        mBinding?.txtPhoneProfile?.text = mUser?.phone

        //validacion de imagen
        if (!mUser?.image.isNullOrBlank()) {
            Picasso.get()
                .load(mUser?.image)
                .into(mBinding?.ImgCircleProfileUser)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _mBinding = null
    }

    override fun onClick(mItem: View?) {
        when (mItem) {
            mBinding?.btnSelectRoll -> mClientHomePresenter?.goSelectRoles()
            mBinding?.btnEditProfile -> mClientHomePresenter?.goClientUpdateProfile()
            mBinding?.imgCloseSession -> showDialog()
        }

    }


    fun getUserFromSession() {
        val gson = Gson()
        if (!sharedPref?.getInformation("user").isNullOrBlank()) {
            //si el usuario esta en session
            mUser = gson.fromJson(sharedPref?.getInformation("user"), User::class.java)
            //  Log.d("ClientProfileFragment", "Usuario $mUser")
        }
    }

    fun showDialog() {
        DialogCloseSession { closeSession ->
            mClientHomePresenter?.closeSession()
        }.show(parentFragmentManager, "dialog")
    }
}