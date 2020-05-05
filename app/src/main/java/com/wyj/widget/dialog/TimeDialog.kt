package com.wyj.widget.dialog

import android.view.Gravity
import androidx.fragment.app.FragmentActivity
import com.wyj.widget.R
import com.wyj.widget.dialog.base.SBuilder
import com.wyj.widget.dialog.base.SDialog
import com.wyj.widget.wheel.IWheel
import com.wyj.widget.wheel.WheelItem
import com.base.utils.clickDelay
import kotlinx.android.synthetic.main.dialog_time.*
import kotlinx.android.synthetic.main.include_title_has_btn.*

class TimeDialog private constructor(private var builder: Builder) : SDialog(builder) {
    private val mFirstTime by lazy {
        val list = mutableListOf<IWheel>()
        for(i in 0 until 24){
            list.add(WheelItem("${i}${context?.getString(R.string.Hour)}"))
        }
        list
    }
    private val mSecondTime by lazy {
        val list = mutableListOf<IWheel>()
        for(i in 0 until 24){
            list.add(WheelItem("${i}${context?.getString(R.string.Hour)}"))
        }
        list
    }

    override fun initView() {

        with(tv_cancle){
            this.text = builder.cancle ?: ""
            this.setTextColor(builder.cancleColor)
            this.clickDelay {
                this@TimeDialog.dismissAllowingStateLoss()
            }
            builder.onCancleListener?.onCancle(this@TimeDialog)
        }

        with(tv_sure){
            this.text = builder.confirm ?: ""
            this.setTextColor(builder.confirmColor)
            this.clickDelay {
                builder.onTimeSelectListener?.onTimeSelect(wv_first.getSelectedIndex(),wv_sencod.getSelectedIndex())
                this@TimeDialog.dismissAllowingStateLoss()
            }
        }

        with(tv_title) {
            this.text = builder.title ?: ""
            this.setTextColor(builder.titleColor)
        }

        with(wv_first){
            this.setItems(mFirstTime)
            this.setShowCount(5)
            this.setSelectedIndex(builder.sTime)
        }

        with(wv_sencod){
            this.setItems(mSecondTime)
            this.setShowCount(5)
            this.setSelectedIndex(builder.sTime)
        }
    }

    open class Builder(activity: FragmentActivity) : SBuilder<Builder>(activity) {
        var onTimeSelectListener: OnTimeSelectListener? = null

        var sTime: Int = 0
        var eTime: Int = 0

        override fun setGravity() = Gravity.BOTTOM

        fun addOnTimeSelectListener(onTimeSelectListener: OnTimeSelectListener?): Builder {
            this.onTimeSelectListener = onTimeSelectListener
            return this
        }

        fun setStartTime(sTime: Int): Builder {
            this.sTime = sTime
            return this
        }

        fun setEndTime(eTime: Int): Builder {
            this.eTime = eTime
            return this
        }

        override fun builder(): Builder {
            setContentView(R.layout.dialog_time)
            return this
        }

        override fun show(activity: FragmentActivity): SDialog? {
            val dialog = TimeDialog(this)
            dialog.show(activity)
            return dialog
        }

    }

    interface OnTimeSelectListener {
        fun onTimeSelect(sTime: Int, eTime: Int)
    }

}