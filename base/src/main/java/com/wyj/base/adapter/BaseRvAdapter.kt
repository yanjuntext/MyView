package com.wyj.base.adapter

import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.wyj.base.log
import java.lang.ref.WeakReference

class BaseRvAdapter<T : BaseAdapterItem<T>>(
    context: FragmentActivity,
    var itemList: MutableList<T>?,
    var viewHolder: SparseArray<Class<out BaseViewHolder<T>>>
) : RecyclerView.Adapter<BaseViewHolder<T>>() {

    private var mCaontext: WeakReference<FragmentActivity>? = null

    init {
        mCaontext = WeakReference(context)
    }

    private val layoutInflater by lazy {
        LayoutInflater.from(mCaontext?.get())
    }


    var onRvItemClickListener: OnRvItemClickListener<T>? = null
    var onRvItemLongClickListener: OnRvItemLongClickListener<T>? = null
    var onRvItemSelectListener: OnRvItemSelectListener<T>? = null
    var onRvMultiItemClickListener: OnRvIMultitemClickListener<T>? = null
    var onDiffContentsTheSame: DiffContentsTheSame<T>? = null

    var signalSelectItem = -1
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    /**切换显示状态 例如：MealHolder*/
    var changeHolderStatus = false
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<T> {
        val clazz = viewHolder.get(viewType)
        val annotation = clazz.getAnnotation(HolderAnnotation::class.java)
        val layoutId = annotation!!.layoutId
        val view = layoutInflater.inflate(layoutId, parent, false)
        val constructor = clazz.getConstructor(View::class.java)
        val baseViewHolder = constructor.newInstance(view)
        baseViewHolder.context = mCaontext
        baseViewHolder.onItemClickListener = onRvItemClickListener
        baseViewHolder.onItemLongClickListener = onRvItemLongClickListener
        baseViewHolder.onItemSelectedListener = onRvItemSelectListener
        baseViewHolder.onRvMultiItemClickListener = onRvMultiItemClickListener
        baseViewHolder.changeHolderStatus = changeHolderStatus
        return baseViewHolder
    }

    override fun getItemCount(): Int = itemList?.size ?: 0

    override fun onBindViewHolder(holder: BaseViewHolder<T>, position: Int) {
        itemList?.let {
            val data = it[position]
            holder.changeHolderStatus = changeHolderStatus
            holder.bindView(
                data,
                if (position == 0) null else it[position - 1],
                position,
                signalSelectItem,
                it.size
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        return itemList?.get(position)?.getViewType() ?: ViewType.TYPE_DEFAULT
    }


    //adapter 刷新，刷新不同，局部刷新
    fun notifity(newItemList: MutableList<T>?) {
        log(itemList?.size ?: 0)
        log(newItemList?.size ?: 0)
        if (itemList.isNullOrEmpty() && !newItemList.isNullOrEmpty()) {
            itemList?.addAll(newItemList)
//            itemList = newItemList
            notifyItemRangeChanged(0, itemList?.size ?: 0)
        } else {
            try {
                val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                    override fun getOldListSize(): Int {
                        log((itemList?.size ?: 0).toString())
                        return itemList?.size ?: 0
                    }

                    override fun getNewListSize(): Int {
                        log((newItemList?.size ?: 0).toString())
                        return newItemList?.size ?: 0
                    }

                    override fun areItemsTheSame(
                        oldItemPosition: Int,
                        newItemPosition: Int
                    ): Boolean =
                        itemList?.get(oldItemPosition)?.getViewType() == newItemList?.get(
                            newItemPosition
                        )?.getViewType()

                    override fun areContentsTheSame(
                        oldItemPosition: Int,
                        newItemPosition: Int
                    ): Boolean =
                        onDiffContentsTheSame?.areContentsTheSame(
                            itemList?.get(oldItemPosition),
                            newItemList?.get(newItemPosition)
                        ) ?: false
                })
                itemList?.clear()
                if (!newItemList.isNullOrEmpty())
                    itemList?.addAll(newItemList)
                result.dispatchUpdatesTo(this)
            } catch (e: Exception) {
                notifyDataSetChanged()
            }

        }
    }

    fun notifyItem(odlItemList: MutableList<T>?, newItemList: MutableList<T>?) {
        log(itemList?.size ?: 0)
        log(newItemList?.size ?: 0)
//        val old = mutableListOf<T>()
//        if (!itemList.isNullOrEmpty()) old.addAll(itemList!!)
        itemList?.clear()
        if (!newItemList.isNullOrEmpty())
            itemList?.addAll(newItemList)
        log(odlItemList.toString())
        log(newItemList.toString())
        DiffFactory.createDiff(this, odlItemList, itemList)
    }

    open class Builder<T : BaseAdapterItem<T>>(
        val context: FragmentActivity,
        val itemList: MutableList<T>?,
        val viewHolder: SparseArray<Class<out BaseViewHolder<T>>>
    ) {
        private var onRvItemClickListener: OnRvItemClickListener<T>? = null
        private var onRvItemLongClickListener: OnRvItemLongClickListener<T>? = null
        private var onRvItemSelectListener: OnRvItemSelectListener<T>? = null
        private var onDiffContentsTheSame: DiffContentsTheSame<T>? = null

        fun setOnRvItemClickListener(onRvItemClickListener: OnRvItemClickListener<T>?): Builder<T> {
            this.onRvItemClickListener = onRvItemClickListener
            return this
        }

        fun setOnRvItemLongClickListener(onRvItemLongClickListener: OnRvItemLongClickListener<T>?): Builder<T> {
            this.onRvItemLongClickListener = onRvItemLongClickListener
            return this
        }

        fun setOnRvItemSelectListener(onRvItemSelectListener: OnRvItemSelectListener<T>?): Builder<T> {
            this.onRvItemSelectListener = onRvItemSelectListener
            return this
        }

        fun setDiffContentsTheSame(onDiffContentsTheSame: DiffContentsTheSame<T>?): Builder<T> {
            this.onDiffContentsTheSame = onDiffContentsTheSame
            return this
        }

        fun create(): BaseRvAdapter<T> = BaseRvAdapter(context, itemList, this.viewHolder).also {
            it.onRvItemClickListener = onRvItemClickListener
            it.onRvItemLongClickListener = onRvItemLongClickListener
            it.onRvItemSelectListener = onRvItemSelectListener
            it.onDiffContentsTheSame = onDiffContentsTheSame
        }
    }
}