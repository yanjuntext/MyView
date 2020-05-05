package com.base.utils.permission

/**
 *
 *@author abc
 *@time 2019/11/26 16:12
 */
object Permission {
    /** 8.0及以上应用安装权限  */
    val REQUEST_INSTALL_PACKAGES = "android.permission.REQUEST_INSTALL_PACKAGES"

    /** 6.0及以上悬浮窗权限  */
    val SYSTEM_ALERT_WINDOW = "android.permission.SYSTEM_ALERT_WINDOW"

    /** 读取日程提醒  */
    val READ_CALENDAR = "android.permission.READ_CALENDAR"
    /** 写入日程提醒  */
    val WRITE_CALENDAR = "android.permission.WRITE_CALENDAR"

    /** 拍照权限  */
    val CAMERA = "android.permission.CAMERA"

    /** 读取联系人  */
    val READ_CONTACTS = "android.permission.READ_CONTACTS"
    /** 写入联系人  */
    val WRITE_CONTACTS = "android.permission.WRITE_CONTACTS"
    /** 访问账户列表  */
    val GET_ACCOUNTS = "android.permission.GET_ACCOUNTS"

    /** 获取精确位置  */
    val ACCESS_FINE_LOCATION = "android.permission.ACCESS_FINE_LOCATION"
    /** 获取粗略位置  */
    val ACCESS_COARSE_LOCATION = "android.permission.ACCESS_COARSE_LOCATION"

    /** 录音权限  */
    val RECORD_AUDIO = "android.permission.RECORD_AUDIO"

    /** 读取电话状态  */
    val READ_PHONE_STATE = "android.permission.READ_PHONE_STATE"
    /** 拨打电话  */
    val CALL_PHONE = "android.permission.CALL_PHONE"
    /** 读取通话记录  */
    val READ_CALL_LOG = "android.permission.READ_CALL_LOG"
    /** 写入通话记录  */
    val WRITE_CALL_LOG = "android.permission.WRITE_CALL_LOG"
    /** 添加语音邮件  */
    val ADD_VOICEMAIL = "com.android.voicemail.permission.ADD_VOICEMAIL"
    /** 使用SIP视频  */
    val USE_SIP = "android.permission.USE_SIP"
    /** 处理拨出电话  */
    val PROCESS_OUTGOING_CALLS = "android.permission.PROCESS_OUTGOING_CALLS"
    /** 8.0危险权限：允许您的应用通过编程方式接听呼入电话。要在您的应用中处理呼入电话，您可以使用 acceptRingingCall() 函数  */
    val ANSWER_PHONE_CALLS = "android.permission.ANSWER_PHONE_CALLS"
    /** 8.0危险权限：权限允许您的应用读取设备中存储的电话号码  */
    val READ_PHONE_NUMBERS = "android.permission.READ_PHONE_NUMBERS"

    /** 传感器  */
    val BODY_SENSORS = "android.permission.BODY_SENSORS"

    /** 发送短信  */
    val SEND_SMS = "android.permission.SEND_SMS"
    /** 接收短信  */
    val RECEIVE_SMS = "android.permission.RECEIVE_SMS"
    /** 读取短信  */
    val READ_SMS = "android.permission.READ_SMS"
    /** 接收 WAP PUSH 信息  */
    val RECEIVE_WAP_PUSH = "android.permission.RECEIVE_WAP_PUSH"
    /** 接收彩信  */
    val RECEIVE_MMS = "android.permission.RECEIVE_MMS"

    /** 读取外部存储  */
    val READ_EXTERNAL_STORAGE = "android.permission.READ_EXTERNAL_STORAGE"
    /** 写入外部存储  */
    val WRITE_EXTERNAL_STORAGE = "android.permission.WRITE_EXTERNAL_STORAGE"

    val WRITE_SETTING = "android.permission.WRITE_SETTINGS"
    object Group {

        /** 日历  */
        val CALENDAR = arrayOf(Permission.READ_CALENDAR, Permission.WRITE_CALENDAR)

        /** 联系人  */
        val CONTACTS =
            arrayOf(Permission.READ_CONTACTS, Permission.WRITE_CONTACTS, Permission.GET_ACCOUNTS)

        /** 位置  */
        val LOCATION = arrayOf(Permission.ACCESS_FINE_LOCATION, Permission.ACCESS_COARSE_LOCATION)

        /** 存储  */
        val STORAGE = arrayOf(Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE)
    }
}