package com.wyj.widget.dialog

import android.content.DialogInterface
import android.os.Build
import android.text.InputType
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.FragmentActivity
import com.wyj.widget.R
import com.wyj.widget.dialog.base.SBuilder
import com.wyj.widget.dialog.base.SDialog
import com.base.utils.clickDelay
import kotlinx.android.synthetic.main.dialog_input.*
import kotlinx.android.synthetic.main.include_bottom.*
import kotlinx.android.synthetic.main.include_title.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main

/**
 *
 *@author abc
 *@time 2019/12/25 15:36
 */
class InputDialog private constructor(private var builder: Builder) : SDialog(builder) {

    private var mShowSoftInputJob: Job? = null

    override fun initView() {

        tv_title.visibility = if (builder.title.isNullOrEmpty()) {
            View.GONE
        } else {
            tv_title.setTextColor(builder.titleColor)
            tv_title.text = builder.title ?: ""
            View.VISIBLE
        }

        et_content.hint = builder.hint ?: ""

        this.addOnShowListeners(builder.onShowListeners)

        builder.onDismissListeners?.forEach {
            addOnDismissListener(it)
        }

        builder.onShowListeners?.forEach {
            this.addOnShowListener(it)
        }
//        et_content.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
        if (!builder.content.isNullOrEmpty()) {
            et_content.setText(builder.content ?: "")
            et_content.setTextColor(builder.contentColor)


            et_content_psw.setText(builder.content ?: "")
            et_content_psw.setTextColor(builder.contentColor)
        }
        if (builder.inputStyle == InputType.TYPE_TEXT_VARIATION_PASSWORD) {
            et_content_psw.visibility = View.VISIBLE
            et_content.visibility = View.GONE
            if (!builder.content.isNullOrEmpty()) {
                et_content_psw.requestFocus()
                et_content_psw.setSelection(builder.content?.length ?: 0)
            } else if (!builder.hint.isNullOrEmpty()) {
                et_content_psw.requestFocus()
            }
        } else {
            et_content_psw.visibility = View.GONE
            et_content.visibility = View.VISIBLE
            if (!builder.content.isNullOrEmpty()) {
                et_content.requestFocus()
                et_content.setSelection(builder.content?.length ?: 0)
            } else if (!builder.hint.isNullOrEmpty()) {
                et_content.requestFocus()
            }
        }


//        et_content.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD

        with(builder.cancle) {
            if (this.isNullOrEmpty()) {
                divider_ver.visibility = View.GONE
                tv_cancle.visibility = View.GONE
            } else {
                divider_ver.visibility = View.VISIBLE
                tv_cancle.visibility = View.VISIBLE
            }
            tv_cancle.text = this ?: ""
            tv_cancle.setTextColor(builder.cancleColor)
        }

        tv_confirm.text = builder.confirm ?: ""
        tv_confirm.setTextColor(builder.confirmColor)

        tv_cancle.clickDelay {
            this.dismissAllowingStateLoss()
            builder.onCancleListener?.onCancle(this)
        }

        tv_confirm.clickDelay {
            this.dismissAllowingStateLoss()
            builder.onConfirmListrener?.onCaonfirm(
                this,
                if (builder.inputStyle == InputType.TYPE_TEXT_VARIATION_PASSWORD) et_content_psw.text?.toString() else et_content.text?.toString()
            )
        }


    }

    override fun onShow(dialog: DialogInterface?) {
        mShowSoftInputJob?.cancel()
        mShowSoftInputJob = GlobalScope.launch(Dispatchers.IO) {
            delay(500L)
            showInput()
        }
        super.onShow(dialog)
    }

    private suspend fun showInput() {
        withContext(Main) {
            requireContext().getSystemService(InputMethodManager::class.java)
                ?.showSoftInput(et_content, 0)
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        mShowSoftInputJob?.cancel()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requireContext().getSystemService(InputMethodManager::class.java)
                ?.hideSoftInputFromWindow(et_content.windowToken, 0)
        }
    }


    open class Builder(activity: FragmentActivity) : SBuilder<Builder>(activity) {

        var hint: String? = null

        var inputStyle = InputType.TYPE_CLASS_TEXT

        fun setInputStyle(inputStyle: Int): Builder {

            this.inputStyle = inputStyle
            return this
        }

        fun setHint(hint: String?): Builder {
            this.hint = hint
            return this
        }

        override fun builder(): Builder {
            setContentView(R.layout.dialog_input)
            return this
        }

        override fun show(activity: FragmentActivity): SDialog? {
            val dialog = InputDialog(this)
            dialog.show(activity)
            return dialog
        }

    }
}