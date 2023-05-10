package com.alejandro.deliverylinks.modules.client.ui.dialogs

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.alejandro.deliverylinks.databinding.DialogCloseSessionBinding
import com.alejandro.deliverylinks.modules.client.presenter.ClientHomePresenter

class DialogCloseSession(
    private val onClickCloSession: (Any?) -> Unit
) : DialogFragment() {

    private var mBinding: DialogCloseSessionBinding? = null
    private var mClientHomePresenter: ClientHomePresenter? = null
    private var mDialog: Dialog? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        mBinding = DialogCloseSessionBinding.inflate(LayoutInflater.from(context))

        val builder = AlertDialog.Builder(requireContext())
        builder.setView(mBinding?.root)
        mClientHomePresenter = ClientHomePresenter(requireContext())

        closeSesion()
        cancelCloseSesion()

        mDialog = builder.create()
        mDialog?.setCanceledOnTouchOutside(false)
        mDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mDialog?.setCancelable(false)
        return mDialog as AlertDialog

    }

    fun closeSesion() {

        mBinding?.btnCloseSession?.setOnClickListener {
            onClickCloSession.invoke(
                mClientHomePresenter?.closeSession()
            )
        }
    }

    fun cancelCloseSesion() {

        mBinding?.btnCancelCloseSession?.setOnClickListener {
            mDialog?.dismiss()
        }
    }
}