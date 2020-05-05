package com.wyj.widget

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView

class TipDialog(context: Context): Dialog(context, R.style.MyTipDialog) {
    enum class Style {
        NORMAL, LOADING
    }

    var duration: Int = 0

    private var tvTips: TextView? = null

    var tipsMsg: String? = null
        set(value) {
            field = value
            tvTips?.text = field ?: ""
        }

    class Builder(val context: Context) {

        private var style: Style = Style.NORMAL

        private var tipImgResId: Int = 0

        private var tipsMsg: String? = null

        private var tipsMsgColor: Int = if (context == null) Color.parseColor("#FFFFFF") else Color.WHITE

        private var duration: Int = 2000

        private var animation: Int = R.style.anim_one_dialog

        private var cancelable: Boolean = true
        private var canceledOnTouchOutside: Boolean = true

        fun setStyle(style: Style): Builder {
            this@Builder.style = style
            return this@Builder
        }

        fun setTipImgResId(tipImgResId: Int) = run {
            this@Builder.tipImgResId = tipImgResId
            this@Builder
        }

        fun setTipsMsg(tipsMsg: String?) = kotlin.run {
            this@Builder.tipsMsg = tipsMsg
            this@Builder
        }

        fun setTipsMsgColor(tipsMsgColor: Int) = run {
            this@Builder.tipsMsgColor = tipsMsgColor
            this@Builder
        }

        fun setDuration(duration: Int) = run {
            this@Builder.duration = duration
            this@Builder
        }

        fun setAnimation(animation: Int) = run {
            this@Builder.animation = animation
            this@Builder
        }

        fun setCancelable(cancelable: Boolean) = run {
            this@Builder.cancelable = cancelable
            this@Builder
        }

        fun setCanceledOnTouchOutside(canceledOnTouchOutside: Boolean) = run {
            this@Builder.canceledOnTouchOutside = canceledOnTouchOutside
            this@Builder
        }

        fun create(): TipDialog {
            val dialog = TipDialog(context)
            dialog.setCancelable(cancelable)
            dialog.setCanceledOnTouchOutside(canceledOnTouchOutside)
            dialog.window?.setWindowAnimations(animation)
            val mRootView = LayoutInflater.from(context).inflate(R.layout.dialog_tips, null, false)
            val progressBar = mRootView.findViewById<ProgressBar>(R.id.progress)


            val mIvTips = mRootView.findViewById<ImageView>(R.id.iv_tips)
            if (style == Style.LOADING) {
                progressBar.visibility = View.VISIBLE
                mIvTips.visibility = View.GONE
            } else {
                mIvTips.visibility = View.VISIBLE
                progressBar.visibility = View.GONE
                if (tipImgResId > 0) {
                    mIvTips.setImageResource(tipImgResId)
                } else {
                    mIvTips.visibility = View.GONE
                }
            }

            val mTvTips = mRootView.findViewById<TextView>(R.id.tv_tips)
            dialog.tvTips = mTvTips
            tipsMsg?.let {
                mTvTips.text = tipsMsg
                if (tipsMsgColor > 0) {
                    mTvTips.setTextColor(tipsMsgColor)
                }
            }

            dialog.duration = if (duration > 0) {
                duration
            } else {
                0
            }

            dialog.setContentView(mRootView)
            return dialog
        }

    }

    private val handler: Handler = object : MyHandler(context as Activity, Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val activity = activityWeakReference.get()
            activity?.let {
                when (msg.what) {
                    1 -> {
                        if(this@TipDialog.isShowing){
                            this@TipDialog.dismiss()
                        }
                    }
                }
            }
        }
    }

    override fun show() {
        super.show()
        if (duration > 0) {
            handler.sendEmptyMessageDelayed(1, duration.toLong())
        }
    }

    override fun dismiss() {
        super.dismiss()
        handler.removeCallbacksAndMessages(null)
    }

}