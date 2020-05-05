package com.wyj.base.adapter


object DiffFactory {
    private val TAG by lazy { DiffFactory::class.java.simpleName }
    fun <T : BaseAdapterItem<T>> createDiff(
        adapter: BaseRvAdapter<T>,
        old: MutableList<T>?,
        new: MutableList<T>?
    ) {
        if (old.isNullOrEmpty() && !new.isNullOrEmpty()) {
            adapter.notifyItemRangeChanged(0, new.size)
        } else {
            val data = when {
                (old?.size ?: 0) > 0 -> old?.get(0)
                (new?.size ?: 0) > 0 -> new?.get(0)
                else -> null
            }
//            logE("DiffFactory []${data is DeviceInfo}")
//            val callback = when (data) {
//                is DeviceInfo -> createDevCallBack(
//                    old as MutableList<DeviceInfo>?,
//                    new as MutableList<DeviceInfo>?
//                )
//                else -> null
//            }
//            if (callback != null) {
//                DiffUtil.calculateDiff(callback).dispatchUpdatesTo(adapter)
//            }
        }
    }

//    private fun createDevCallBack(old: MutableList<DeviceInfo>?, new: MutableList<DeviceInfo>?) =
//        DeviceDiffCallBack(old, new)

}