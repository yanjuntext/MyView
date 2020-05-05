package com.wyj.widget.dialog

import androidx.fragment.app.FragmentActivity
import com.wyj.widget.R
import com.wyj.widget.dialog.base.AnimStyle
import com.wyj.widget.dialog.base.SBuilder
import com.wyj.widget.dialog.base.SDialog
import com.base.utils.clickDelay
import kotlinx.android.synthetic.main.dialog_s_tip.*

/**
 *
 *@author abc
 *@time 2020/3/4 19:10
 */
class STipDialog private constructor(val builder: Builder) : SDialog(builder) {

    enum class Type {
        error, complete
    }

    override fun initView() {
        tv_content.text = builder.content ?: ""
        iv_war.setImageResource(if (builder.type == Type.error) R.drawable.warning else R.drawable.confirm)
        main.clickDelay {
            dismissAllowingStateLoss()
        }
    }

    open class Builder(activity: FragmentActivity) : SBuilder<Builder>(activity) {

        var type: Type = Type.error
        fun setType(type: Type): Builder {
            this.type = type
            return this
        }

        override fun builder(): Builder {
            setContentView(R.layout.dialog_s_tip)
            this.setAnimalStyle(AnimStyle.SCALE)
            return this
        }

        override fun show(activity: FragmentActivity): SDialog? {
            return STipDialog(this).also {
               it.show(activity)
            }
        }

    }

}