package com.base.utils.permission

import android.content.Context
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import java.lang.IllegalArgumentException
import java.util.*

/**
 *
 *@author abc
 *@time 2019/11/26 16:00
 */
class SPermissions private constructor(private val activity: FragmentActivity?) {

    /**权限集合*/
    private var mPermissions = mutableListOf<String>()

    private var mRePermission = false

    companion object {
        fun with(activity: FragmentActivity) = SPermissions(activity)
        fun with(fragment: Fragment) = SPermissions(fragment.activity)


        /**
         * 检查某些权限是否全部授予了
         *
         * @param context     上下文对象
         * @param permissions 需要请求的权限组
         */
        fun isHasPermission(context: Context, vararg permissions: String): Boolean {
            return isHasPermission(context, Arrays.asList(*permissions))
        }

        fun isHasPermission(context: Context, permissions: List<String>): Boolean {
            val failPermissions = PermissionUtils.getFailPermissions(context, permissions)
            return failPermissions == null || failPermissions.isEmpty()
        }

        /**
         * 检查某些权限是否全部授予了
         *
         * @param context     上下文对象
         * @param permissions 需要请求的权限组
         */
        fun isHasPermission(context: Context, vararg permissions: Array<String>): Boolean {
            val permissionList = ArrayList<String>()
            for (group in permissions) {
                permissionList.addAll(Arrays.asList(*group))
            }
            val failPermissions = PermissionUtils.getFailPermissions(context, permissionList)
            return failPermissions == null || failPermissions.isEmpty()
        }

        /**
         * 跳转到应用权限设置页面
         *
         * @param context 上下文对象
         */
        fun gotoPermissionSettings(context: Context) {
            PermissionSettingPage.start(context, false)
        }

        /**
         * 跳转到应用权限设置页面
         *
         * @param context 上下文对象
         * @param newTask 是否使用新的任务栈启动
         */
        fun gotoPermissionSettings(context: Context, newTask: Boolean) {
            PermissionSettingPage.start(context, newTask)
        }

    }

    /**
     * 设置权限组
     */
    fun permission(vararg permissions: String): SPermissions {
        mPermissions.addAll(listOf(*permissions))
        return this
    }


    /**
     * 设置权限组
     */
    fun permission(vararg permissions: Array<String>): SPermissions {
        for (group in permissions) {
            mPermissions.addAll(listOf(*group))
        }
        return this
    }

    /**
     * 设置权限组
     */
    fun permission(permissions: List<String>): SPermissions {
        mPermissions.addAll(permissions)
        return this
    }

    /**
     * 被拒绝后继续申请，直到授权或者永久拒绝
     */
    fun constantRequest(): SPermissions {
        mRePermission = true
        return this
    }

    /**
     * 请求权限
     */
    fun request(call: OnPermission?) {
        // 如果没有指定请求的权限，就使用清单注册的权限进行请求
        if(mPermissions.isEmpty()){
            mPermissions.addAll(PermissionUtils.getManifestPermissions(activity))
        }

        require(mPermissions.isNotEmpty()) { "The requested permission cannot be empty" }
        requireNotNull(activity) { "The requested permission cannot be empty" }
//        require(activity == null) { "The requested permission cannot be empty" }

        PermissionUtils.checkTargetSdkVersion(activity as Context,mPermissions)

        val failPermissions = PermissionUtils.getFailPermissions(activity as Context, mPermissions)
        Log.i("PermissionFragment","failPermissions[${failPermissions.isNullOrEmpty()}]")
        if(failPermissions.isNullOrEmpty()){
            call?.hasPermission(mPermissions,true)
        }else{
            // 检测权限有没有在清单文件中注册
            PermissionUtils.checkPermissions(activity, mPermissions)
            // 申请没有授予过的权限
            PermissionFragment.newInstance(ArrayList(mPermissions), mRePermission)
                .prepareRequest(activity, call)
        }
    }



}