package com.wyj.widget.dialog

import android.content.Context
import android.view.Gravity
import androidx.fragment.app.FragmentActivity
import com.base.utils.clickDelay
import com.wyj.widget.R
import com.wyj.widget.dialog.base.AnimStyle
import com.wyj.widget.dialog.base.SBuilder
import com.wyj.widget.dialog.base.SDialog
import com.wyj.widget.wheel.IWheel
import com.wyj.widget.wheel.WheelItemView
import kotlinx.android.synthetic.main.dialog_circle.*
import kotlinx.android.synthetic.main.include_bottom.tv_cancle
import kotlinx.android.synthetic.main.include_title_has_btn.*

/**
 *
 *@author abc
 *@time 2020/1/7 13:51
 */
class CircleDialog private constructor(private var builder: Builder) : SDialog(builder),
    WheelItemView.OnSelectedListener {


    override fun initView() {
        with(tv_cancle) {
            this.text = builder.cancle ?: ""
            this.setTextColor(builder.cancleColor)
            this.clickDelay {
                this@CircleDialog.dismissAllowingStateLoss()
            }
        }

        with(tv_sure) {
            this.text = builder.confirm ?: ""
            this.setTextColor(builder.confirmColor)
            this.clickDelay {
                builder.onSelectListener?.onSelectListener(wv.getSelectedIndex())
                this@CircleDialog.dismissAllowingStateLoss()
            }
        }

        with(tv_title) {
            this.text = builder.title ?: ""
            this.setTextColor(builder.titleColor)
        }

        with(wv) {
            this.setItems(builder.mLables)
            this.setSelectedIndex(builder.mSelectLablePosition)
            this.OnSelectedListener(this@CircleDialog)
        }
    }

    override fun onSelected(context: Context, selectedIndex: Int) {
        builder.mSelectLablePosition = selectedIndex
    }


    open class Builder(activity: FragmentActivity) : SBuilder<Builder>(activity) {

        var mLables: MutableList<IWheel> = mutableListOf()
        var mSelectLablePosition: Int = 0
        var onSelectListener: OnSelectListener? = null
        fun setLables(lables: MutableList<IWheel>): Builder {
            this.mLables.addAll(lables)
            return this
        }

        fun setSelectLablePosition(position: Int): Builder {
            this.mSelectLablePosition = position
            return this
        }

        fun setOnSelectListener(onSelectListener: OnSelectListener?): Builder {
            this.onSelectListener = onSelectListener
            return this
        }

        override fun builder(): Builder {
            setContentView(R.layout.dialog_circle)
            setAnimalStyle(AnimStyle.BOTTOM)
            return this
        }

        override fun isFullWidthScreen(): Boolean = true

        override fun setGravity() = Gravity.BOTTOM

        override fun show(activity: FragmentActivity): SDialog? {
            val dialog = CircleDialog(this)
            dialog.show(activity)
            return dialog
        }

    }

    interface OnSelectListener {
        fun onSelectListener(position: Int)
    }

}