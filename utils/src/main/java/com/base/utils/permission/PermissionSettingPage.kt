package com.base.utils.permission

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings

/**
 *
 *@author abc
 *@time 2019/11/26 16:48
 */
object PermissionSettingPage {
    private val MARK = Build.MANUFACTURER.toLowerCase()

    /**
     * 跳转到应用权限设置页面
     *
     * @param context 上下文对象
     * @param newTask 是否使用新的任务栈启动
     */
    fun start(context: Context, newTask: Boolean) {

        var intent: Intent? = null
        if (MARK.contains("huawei")) {
            intent = huawei(context)
        } else if (MARK.contains("xiaomi")) {
            intent = xiaomi(context)
        } else if (MARK.contains("oppo")) {
            intent = oppo(context)
        } else if (MARK.contains("vivo")) {
            intent = vivo(context)
        } else if (MARK.contains("meizu")) {
            intent = meizu(context)
        }

        if (intent == null || !hasIntent(context, intent)) {
            intent = google(context)
        }

        if (newTask) {
            // 如果用户在权限设置界面改动了权限，请求权限 Activity 会被重启，加入这个 Flag 就可以避免
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        try {
            context.startActivity(intent)
        } catch (ignored: Exception) {
            intent = google(context)
            context.startActivity(intent)
        }

    }

    fun getSettingIntent(context: Context):Intent?{
        var intent: Intent? = null
        when {
            MARK.contains("huawei") -> {
                intent = huawei(context)
            }
            MARK.contains("xiaomi") -> {
                intent = xiaomi(context)
            }
            MARK.contains("oppo") -> {
                intent = oppo(context)
            }
            MARK.contains("vivo") -> {
                intent = vivo(context)
            }
            MARK.contains("meizu") -> {
                intent = meizu(context)
            }
        }

        if (intent == null || !hasIntent(context, intent)) {
            intent = google(context)
        }
        return intent
    }

    private fun google(context: Context): Intent {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.fromParts("package", context.packageName, null)
        return intent
    }

    private fun huawei(context: Context): Intent {
        val intent = Intent()

        intent.setClassName(
            "com.huawei.systemmanager",
            "com.huawei.permissionmanager.ui.SingleAppActivity"
        )
        if (hasIntent(context, intent)) {
            return intent
        }

        intent.component = ComponentName(
            "com.android.packageinstaller",
            "com.android.packageinstaller.permission.ui.ManagePermissionsActivity"
        )
        if (hasIntent(context, intent)) {
            return intent
        }

        intent.component =
            ComponentName(
                "com.huawei.systemmanager",
                "com.huawei.permissionmanager.ui.MainActivity"
            )
        return if (hasIntent(context, intent)) {
            intent
        } else intent

    }

    private fun xiaomi(context: Context): Intent {
        val intent = Intent("miui.intent.action.APP_PERM_EDITOR")
        intent.putExtra("extra_pkgname", context.packageName)
        if (hasIntent(context, intent)) {
            return intent
        }

        intent.setPackage("com.miui.securitycenter")
        if (hasIntent(context, intent)) {
            return intent
        }

        intent.setClassName(
            "com.miui.securitycenter",
            "com.miui.permcenter.permissions.AppPermissionsEditorActivity"
        )
        if (hasIntent(context, intent)) {
            return intent
        }

        intent.setClassName(
            "com.miui.securitycenter",
            "com.miui.permcenter.permissions.PermissionsEditorActivity"
        )
        return intent
    }

    private fun oppo(context: Context): Intent {
        val intent = Intent()
        intent.putExtra("packageName", context.packageName)

        intent.setClassName(
            "com.color.safecenter",
            "com.color.safecenter.permission.PermissionManagerActivity"
        )
        if (hasIntent(context, intent)) {
            return intent
        }

        intent.setClassName(
            "com.coloros.safecenter",
            "com.coloros.safecenter.permission.PermissionManagerActivity"
        )
        if (hasIntent(context, intent)) {
            return intent
        }

        intent.setClassName(
            "com.coloros.securitypermission",
            "com.coloros.securitypermission.permission.PermissionGroupsActivity"
        )
        if (hasIntent(context, intent)) {
            return intent
        }

        intent.setClassName(
            "com.coloros.securitypermission",
            "com.coloros.securitypermission.permission.PermissionManagerActivity"
        )
        if (hasIntent(context, intent)) {
            return intent
        }

        intent.setClassName("com.oppo.safe", "com.oppo.safe.permission.PermissionAppListActivity")
        return if (hasIntent(context, intent)) {
            intent
        } else intent
    }

    private fun vivo(context: Context): Intent {
        val intent = Intent()
        intent.putExtra("packagename", context.packageName)

        // vivo x7 Y67 Y85
        intent.setClassName(
            "com.iqoo.secure",
            "com.iqoo.secure.safeguard.SoftPermissionDetailActivity"
        )
        if (hasIntent(context, intent)) {
            return intent
        }

        // vivo Y66 x20 x9
        intent.setClassName(
            "com.vivo.permissionmanager",
            "com.vivo.permissionmanager.activity.SoftPermissionDetailActivity"
        )
        if (hasIntent(context, intent)) {
            return intent
        }

        // Y85
        intent.setClassName(
            "com.vivo.permissionmanager",
            "com.vivo.permissionmanager.activity.PurviewTabActivity"
        )
        if (hasIntent(context, intent)) {
            return intent
        }

        // 跳转会报 java.lang.SecurityException: Permission Denial
        intent.setClassName(
            "com.android.packageinstaller",
            "com.android.packageinstaller.permission.ui.ManagePermissionsActivity"
        )
        if (hasIntent(context, intent)) {
            return intent
        }

        intent.component =
            ComponentName(
                "com.iqoo.secure",
                "com.iqoo.secure.safeguard.SoftPermissionDetailActivity"
            )
        return intent
    }

    private fun meizu(context: Context): Intent {
        val intent = Intent("com.meizu.safe.security.SHOW_APPSEC")
        intent.putExtra("packageName", context.packageName)
        intent.component = ComponentName("com.meizu.safe", "com.meizu.safe.security.AppSecActivity")
        return intent
    }

    private fun hasIntent(context: Context, intent: Intent): Boolean {
        return !context.packageManager.queryIntentActivities(
            intent,
            PackageManager.MATCH_DEFAULT_ONLY
        ).isEmpty()
    }

}