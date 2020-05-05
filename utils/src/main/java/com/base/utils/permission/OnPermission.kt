package com.base.utils.permission

/**
 *
 *@author abc
 *@time 2019/11/26 16:14
 */
interface OnPermission {
    /**
     * 有权限被同意授予时回调
     *
     * @param granted           请求成功的权限组
     * @param isAll             是否全部授予了
     */
    fun hasPermission(granted: List<String>, isAll: Boolean)

    /**
     * 有权限被拒绝授予时回调
     *
     * @param denied            请求失败的权限组
     * @param quick             是否有某个权限被永久拒绝了
     */
    fun noPermission(denied: List<String>, quick: Boolean)
}