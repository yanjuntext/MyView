package com.wyj.widget.dialog.base

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.wyj.widget.R

/**
 *
 *@author abc
 *@time 2019/11/12 10:54
 */
abstract class SDialog(private var builder: SBuilder<*>?) : DialogFragment(),
    DialogInterface.OnShowListener {

    private val mGravity = Gravity.NO_GRAVITY

    companion object {
        private var lastShowTime = 0L
        private var lastShowTag: String? = null
    }

    val mOnShowListeners: MutableList<OnShowListener> by lazy { mutableListOf<OnShowListener>() }

    val mOnDismissListers: MutableList<OnDismissListener> by lazy { mutableListOf<OnDismissListener>() }

    override fun onStart() {
        super.onStart()
        //设置背景透明
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        //设置全屏
        if (builder?.isFullScreent() == true)
            setFullScreent()
        Log.e("SDialog","isFullWidthScreen[${builder?.isFullWidthScreen()}]")
        if (builder?.isFullWidthScreen() == true)
            setFullWidthScreen()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.e("SDialog", "contentView[${builder?.contentView == null}]")
        setStyle(STYLE_NORMAL, builder?.style ?: R.style.SDialog)
        if (builder?.style == R.style.SLightDialog) {
            dialog?.window?.setDimAmount(0f)
        }
        dialog?.window?.setWindowAnimations(builder?.animalStyle ?: 0)
        dialog?.setCanceledOnTouchOutside(builder?.outClickCancleable ?: true)
        isCancelable = builder?.cancleable ?: true
        dialog?.setOnShowListener(this)
        dialog?.setOnDismissListener(this)


        builder?.getLayoutId()?.let {
            return inflater.inflate(it, container, false)
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        if (dialog == null) {
            showsDialog = false
        }

        super.onActivityCreated(savedInstanceState)
        dialog?.window?.setGravity(builder?.setGravity() ?: Gravity.CENTER)


        initView()

    }

    abstract fun initView()

    private fun setFullScreent() {
        val params = dialog?.window?.attributes
        params?.width = WindowManager.LayoutParams.MATCH_PARENT
        params?.height = WindowManager.LayoutParams.MATCH_PARENT
        dialog?.window?.attributes = params
    }

    private fun setFullWidthScreen() {
        dialog?.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT)
        val params = dialog?.window?.attributes
        params?.width = WindowManager.LayoutParams.MATCH_PARENT
        params?.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog?.window?.attributes = params

        Log.e("SDialog","setFullWidthScreen[${dialog?.window == null}]")

    }

    fun show(activity: FragmentActivity) {
        show(activity, activity::class.java.simpleName)
    }

    fun show(activity: FragmentActivity, tag: String) {

        val manager = activity.supportFragmentManager
        val fragment = manager.findFragmentByTag(activity::class.java.name)
        if ((fragment != null) || isAdded || isVisible) {
            manager.beginTransaction().remove(this).commit()
        }
        show(manager, tag)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.e("SDialog", "onDestroyView")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("SDialog", "onDestroy")
    }

    override fun dismiss() {
        dismissAllowingStateLoss()
        this.onDestroyView()
        this.onDestroy()
    }

    private fun isRepeatedShow(tag: String?): Boolean {
        val repeated = (tag == lastShowTag) && (SystemClock.uptimeMillis() - lastShowTime < 500)
        if (!repeated) {
            lastShowTag = tag
            lastShowTime = SystemClock.uptimeMillis()
        }
        return repeated
    }

    override fun show(manager: FragmentManager, tag: String?) {
        if (!isRepeatedShow(tag) && !manager.isStateSaved) {
            super.show(manager, tag)
        }
    }

    override fun show(transaction: FragmentTransaction, tag: String?): Int {
        return if (!isRepeatedShow(tag) && !isStateSaved) {
            super.show(transaction, tag)
        } else -1
    }

    fun addOnShowListener(onShowListener: OnShowListener) {
        mOnShowListeners.add(onShowListener)
    }

    fun addOnShowListeners(onShowListeners: MutableList<OnShowListener>?, clear: Boolean = true) {
        if (clear) mOnShowListeners.clear()
        if (onShowListeners != null)
            mOnShowListeners.addAll(onShowListeners)
    }

    fun addOnDismissListener(onDismissListener: OnDismissListener) {
        mOnDismissListers.add(onDismissListener)
    }

    override fun onShow(dialog: DialogInterface?) {

        mOnShowListeners.forEach {
            it.onShow()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        mOnDismissListers.forEach {
            it.onDismiss()
        }
    }


    interface OnConfirmListrener {
        fun onCaonfirm(dialog: SDialog?, any: Any?)
    }

    interface OnCancleListener {
        fun onCancle(dialog: SDialog?)
    }

    interface OnDismissListener {
        fun onDismiss()
    }

    interface OnShowListener {
        fun onShow()
    }
}