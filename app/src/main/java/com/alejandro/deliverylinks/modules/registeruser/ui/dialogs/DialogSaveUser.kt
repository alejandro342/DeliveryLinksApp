package com.alejandro.deliverylinks.modules.client.ui.dialogs

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.alejandro.deliverylinks.databinding.DialogSaveUserBinding
import com.alejandro.deliverylinks.modules.registeruser.presenter.RegisterUserPresenter

class DialogSaveUser : DialogFragment() {


    private var mBinding: DialogSaveUserBinding? = null
    private var mDialog: Dialog? = null
    private var mResgisterUSerPresenter: RegisterUserPresenter? = null


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        mBinding = DialogSaveUserBinding.inflate(LayoutInflater.from(context))

        val builder = AlertDialog.Builder(requireContext())
        builder.setView(mBinding?.root)

        mResgisterUSerPresenter = RegisterUserPresenter(requireContext())

        mDialog = builder.create()
        mDialog?.setCanceledOnTouchOutside(false)
        mDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mDialog?.setCancelable(false)

        return mDialog as AlertDialog

    }
}