package com.base.utils.permission

/**
 *
 *@author abc
 *@time 2019/11/26 16:26
 */
class ManifestRegisterException: RuntimeException {
    constructor(permission:String?):super(if(permission.isNullOrEmpty())
    "No permissions are registered in the manifest file" else "$permission : Permissions are not registered in the manifest file")
}