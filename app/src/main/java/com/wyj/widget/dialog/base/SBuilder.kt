package com.wyj.widget.dialog.base

import android.view.Gravity
import androidx.annotation.AnimRes
import androidx.annotation.ColorInt
import androidx.annotation.LayoutRes
import androidx.annotation.StyleRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.wyj.widget.R
import com.wyj.widget.dialog.base.AnimStyle
import com.wyj.widget.dialog.base.SDialog

/**
 *
 *@author abc
 *@time 2019/11/12 11:17
 */
abstract class SBuilder<B : SBuilder<B>>(var activity: FragmentActivity) : SBuilderImp {

    fun getLayoutId(): Int? = contentView

    var contentView: Int? = null
    var cancleable: Boolean = true
    var outClickCancleable = true
    var animalStyle: Int = AnimStyle.SCALE
    var style: Int = R.style.SDialog

    var onConfirmListrener: SDialog.OnConfirmListrener? = null
    var onCancleListener: SDialog.OnCancleListener? = null

    var onShowListeners: MutableList<SDialog.OnShowListener>? = null
    var onDismissListeners: MutableList<SDialog.OnDismissListener>? = null

    internal var titleColor: Int = 0
    internal var cancleColor: Int = 0
    internal var confirmColor: Int = 0
    internal var contentColor: Int = 0

    var title: String? = null

    var content: String? = null

    var cancle: String? = null


    var confirm: String? = null

    var mGravity: Int = Gravity.NO_GRAVITY

    init {
        titleColor = ContextCompat.getColor(activity, R.color.dialog_title)
        contentColor = ContextCompat.getColor(activity, R.color.dialog_content)
        cancleColor = ContextCompat.getColor(activity, R.color.dialog_btn)
        confirmColor = ContextCompat.getColor(activity, R.color.dialog_btn)
    }


    fun setContentView(@LayoutRes layoutRes: Int): B {
        this.contentView = layoutRes
        return this as B
    }


    fun setCancleable(cancleable: Boolean): B {
        this.cancleable = cancleable
        return this as B
    }

    fun setOutClickCancleable(outClickCancleable: Boolean): B {
        this.outClickCancleable = outClickCancleable
        return this as B
    }

    fun setAnimalStyle(@AnimRes animal: Int): B {
        this.animalStyle = animal
        return this as B
    }

    fun setConfirmListener(confirmListrener: SDialog.OnConfirmListrener?): B {
        this.onConfirmListrener = confirmListrener
        return this as B
    }

    fun setCancleListener(cancleListener: SDialog.OnCancleListener?): B {
        this.onCancleListener = cancleListener
        return this as B
    }

//    fun setDismissListener(dismissListener: SDialog.OnDismissListener?): B {
//        this.onDismissListener = dismissListener
//        return this as B
//    }

    fun setStyle(@StyleRes style: Int): B {
        this.style = style
        return this as B
    }

    override fun isFullScreent(): Boolean = false

    override fun isFullWidthScreen(): Boolean = false

    override fun setGravity() = Gravity.NO_GRAVITY

    fun setTitle(title: String): B {
        this.title = title
        return this as B
    }

    fun setTitleColor(@ColorInt titleColor: Int): B {
        this.titleColor = titleColor
        return this as B
    }


    fun setContent(content: String): B {
        this.content = content
        return this as B
    }

    fun setContentColor(@ColorInt contentColor: Int): B {
        this.contentColor = contentColor
        return this as B
    }

    fun setCancleColorr(@ColorInt cancleColor: Int): B {
        this.cancleColor = cancleColor
        return this as B
    }

    fun setConfirmColor(@ColorInt confirmColor: Int): B {
        this.confirmColor = confirmColor
        return this as B
    }

    fun setCancle(cancle: String?, cancleListener: SDialog.OnCancleListener? = null): B {
        this.cancle = cancle
        this.onCancleListener = cancleListener
        return this as B
    }

    fun setConfirm(
        confirm: String?,
        confirmListrener: SDialog.OnConfirmListrener? = null
    ): B {
        this.confirm = confirm
        this.onConfirmListrener = confirmListrener
        return this as B
    }

    fun setOnConfirmListener(confirmListrener: SDialog.OnConfirmListrener?): B {
        this.onConfirmListrener = confirmListrener
        return this as B
    }

    fun addOnShowListener(onShowListener: SDialog.OnShowListener?): B {
        if (onShowListeners == null) onShowListeners = mutableListOf()
        if (onShowListener != null) onShowListeners?.add(onShowListener)
        return this as B
    }

    fun addOnDismissListener(onDismissListener: SDialog.OnDismissListener?): B {
        if (onDismissListeners == null) onDismissListeners = mutableListOf()
        if (onDismissListener != null) onDismissListeners?.add(onDismissListener)
        return this as B
    }

    fun removeOnShowListener(onShowListener: SDialog.OnShowListener?): B {
        if (onShowListener != null) onShowListeners?.remove(onShowListener)
        return this as B
    }

    fun removeOnDismissListener(onDismissListener: SDialog.OnDismissListener?): B {
        if (onDismissListener != null) onDismissListeners?.remove(onDismissListener)
        return this as B
    }

    abstract fun builder(): B
    abstract fun show(activity: FragmentActivity): SDialog?
}