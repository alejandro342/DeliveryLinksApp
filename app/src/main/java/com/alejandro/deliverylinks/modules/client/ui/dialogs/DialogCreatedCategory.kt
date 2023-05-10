package com.alejandro.deliverylinks.modules.client.ui.dialogs

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.alejandro.deliverylinks.databinding.DialogCreatedCategoryBinding

class DialogCreatedCategory : DialogFragment() {


    private var mBinding: DialogCreatedCategoryBinding? = null
    private var mDialog: Dialog? = null


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        mBinding = DialogCreatedCategoryBinding.inflate(LayoutInflater.from(context))

        val builder = AlertDialog.Builder(requireContext())
        builder.setView(mBinding?.root)

        mDialog = builder.create()
        mDialog?.setCanceledOnTouchOutside(false)
        mDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mDialog?.setCancelable(false)

        return mDialog as AlertDialog

    }
}