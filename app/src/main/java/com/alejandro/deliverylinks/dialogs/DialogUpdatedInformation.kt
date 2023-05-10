package com.alejandro.deliverylinks.modules.client.ui.dialogs

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.alejandro.deliverylinks.databinding.DialogUpdateInformationBinding
import com.alejandro.deliverylinks.modules.client.presenter.ClientHomePresenter

class DialogUpdatedInformation(
    private val onClickCloSession: (Any?) -> Unit
) : DialogFragment() {

    private var mBinding: DialogUpdateInformationBinding? = null
    private var mClientHomePresenter: ClientHomePresenter? = null
    private var mDialog: Dialog? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        mBinding = DialogUpdateInformationBinding.inflate(LayoutInflater.from(context))

        val builder = AlertDialog.Builder(requireContext())
        builder.setView(mBinding?.root)
        mClientHomePresenter = ClientHomePresenter(requireContext())

        mDialog = builder.create()
        mDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return mDialog as AlertDialog

    }

    fun closeSesion() {

        mBinding?.btnCloseSession?.setOnClickListener {
            onClickCloSession.invoke(
                mClientHomePresenter?.closeSession()
            )
        }
    }
}