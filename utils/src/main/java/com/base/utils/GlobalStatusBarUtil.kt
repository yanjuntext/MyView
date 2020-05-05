package com.base.utils

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Environment
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import java.io.Closeable
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.util.*

/**
 * 状态栏
 *@author abc
 *@time 2019/9/3 16:43
 */
object GlobalStatusBarUtil {

    private val BRAND by lazy { Build.BOARD.toLowerCase() }
    private val KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name"
    private val KEY_FLYME_VERSION_NAME = "ro.build.display.id"
    private val FLYME by lazy { "flyme" }
    private val MEIZUBOARD by lazy { arrayOf("m9", "M9", "mx", "MX") }
    private val ZUKZ1 = "zuk z1"
    private val ZTEC2016 = "zte c2016"
    private val sFlymeVersionName: String? by lazy {
        val properties = Properties()

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            // android 8.0，读取 /system/uild.prop 会报 permission denied
            var fileInputStream: FileInputStream? = null
            try {
                fileInputStream =
                    FileInputStream(File(Environment.getRootDirectory(), "build.prop"))
                properties.load(fileInputStream)
            } catch (e: Exception) {
            } finally {
                close(fileInputStream)
            }
        }

        var clzSystemProperties: Class<*>? = null
        try {
            clzSystemProperties = Class.forName("android.os.SystemProperties")
            val getMethod = clzSystemProperties!!.getDeclaredMethod("get", String::class.java)
            //flyme
            getLowerCaseName(properties, getMethod, KEY_FLYME_VERSION_NAME)
        } catch (e: Exception) {
            null
        }

    }
    private val sMiuiVersionName: String? by lazy {
        val properties = Properties()

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            // android 8.0，读取 /system/uild.prop 会报 permission denied
            var fileInputStream: FileInputStream? = null
            try {
                fileInputStream =
                    FileInputStream(File(Environment.getRootDirectory(), "build.prop"))
                properties.load(fileInputStream)
            } catch (e: Exception) {
            } finally {
                close(fileInputStream)
            }
        }

        var clzSystemProperties: Class<*>? = null
        try {
            clzSystemProperties = Class.forName("android.os.SystemProperties")
            val getMethod = clzSystemProperties!!.getDeclaredMethod("get", String::class.java)
            // miui
            getLowerCaseName(properties, getMethod, KEY_MIUI_VERSION_NAME)
        } catch (e: Exception) {
            null
        }

    }

    private val STATUSBAR_TYPE_DEFAULT = 0
    private val STATUSBAR_TYPE_MIUI = 1
    private val STATUSBAR_TYPE_FLYME = 2
    private val STATUSBAR_TYPE_ANDROID6 = 3 // Android 6.0
    private val STATUS_BAR_DEFAULT_HEIGHT_DP = 25 // 大部分状态栏都是25dp
    private var mStatuBarType = STATUSBAR_TYPE_DEFAULT
    var sVirtualDensity = -1f
    var sVirtualDensityDpi = -1f
    private var sStatusbarHeight = -1

    private var sIsTabletChecked = false
    private var sIsTabletValue = false

    fun translucent(activity: AppCompatActivity) {
        translucent(activity.window, 0x40000000)
    }

    fun translucent(window: Window, @ColorInt colorOn5x: Int) {
        if (!supportTranslucent()) return

        if (isNotchOfficialSupport()) {
            handleDisplayCutoutMode(window)
        }
        // 小米和魅族4.4 以上版本支持沉浸式
        // 小米 Android 6.0 ，开发版 7.7.13 及以后版本设置黑色字体又需要 clear FLAG_TRANSLUCENT_STATUS, 因此还原为官方模式
        if (isMeizu() || (isMIUI() && Build.VERSION.SDK_INT < Build.VERSION_CODES.M)) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
            )
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && supportTransclentStatusBar6()) {
                // android 6以后可以改状态栏字体颜色，因此可以自行设置为透明
                // ZUK Z1是个另类，自家应用可以实现字体颜色变色，但没开放接口
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = Color.TRANSPARENT
            } else {
                // android 5不能修改状态栏字体颜色，因此直接用FLAG_TRANSLUCENT_STATUS，nexus表现为半透明
                // 魅族和小米的表现如何？
                // update: 部分手机运用FLAG_TRANSLUCENT_STATUS时背景不是半透明而是没有背景了。。。。。
                //                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

                // 采取setStatusBarColor的方式，部分机型不支持，那就纯黑了，保证状态栏图标可见
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = colorOn5x
            }
        }

    }

    fun handleDisplayCutoutMode(window: Window) {
        window.decorView?.let {
            if (ViewCompat.isAttachedToWindow(it)) {
                realHandleDisplayCutoutMode(window, it)
            } else {
                it.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
                    override fun onViewDetachedFromWindow(v: View?) {
                        v?.removeOnAttachStateChangeListener(this)
                        realHandleDisplayCutoutMode(window, it)
                    }

                    override fun onViewAttachedToWindow(v: View?) {

                    }
                })
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    fun realHandleDisplayCutoutMode(window: Window, decorView: View) {
        if (decorView.rootWindowInsets != null &&
            decorView.rootWindowInsets.displayCutout != null
        ) {
            val params = window.attributes
            params.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            window.attributes = params
        }
    }

    private fun supportTranslucent(): Boolean =
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && !(isEssentialPhone() && Build.VERSION.SDK_INT < 26)

    private fun isEssentialPhone(): Boolean = BRAND.contains("essential")

    private fun isNotchOfficialSupport() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P

    fun isMeizu() = isPhone(MEIZUBOARD) || isFlyme()

    fun isMIUI(): Boolean = !sMiuiVersionName.isNullOrEmpty()

    fun supportTransclentStatusBar6(): Boolean = !(isZTKC2016() || isZUKZ1())

    private fun isZUKZ1(): Boolean {
        val board = Build.MODEL
        return board != null && board.toLowerCase().contains(ZUKZ1)
    }

    private fun isZTKC2016(): Boolean {
        val board = Build.MODEL
        return board != null && board.toLowerCase().contains(ZTEC2016)
    }

    private fun isPhone(boards: Array<String>): Boolean {
        val board = Build.BOARD ?: return false
        for (board1 in boards) {
            if (board == board1) {
                return true
            }
        }
        return false
    }

    private fun isFlyme(): Boolean =
        !sFlymeVersionName.isNullOrEmpty() && sFlymeVersionName?.contains(FLYME) == true

    fun close(c: Closeable?) {
        if (c != null) {
            try {
                c.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun getLowerCaseName(p: Properties, get: Method, key: String): String? {
        var name: String? = p.getProperty(key)
        if (name == null) {
            try {
                name = get.invoke(null, key) as String
            } catch (ignored: Exception) {
            }
        }
        if (name != null) name = name.toLowerCase()
        return name
    }

    /**
     * 设置状态栏黑色字体图标，
     * 支持 4.4 以上版本 MIUI 和 Flyme，以及 6.0 以上版本的其他 Android
     *
     * @param activity 需要被处理的 Activity
     */
    fun setStatusBarLightMode(activity: AppCompatActivity?) {
        if (activity == null) return
        if (isZTKC2016()) return

        if (mStatuBarType != STATUSBAR_TYPE_DEFAULT) {
            setStatusBarLightMode(activity, mStatuBarType)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (isMIUICustomStatusBarLightModeImpl() && MIUISetStatusBarLightMode(
                    activity.window,
                    true
                )
            ) {
                mStatuBarType = STATUSBAR_TYPE_MIUI
            } else if (FlymeSetStatusBarLightMode(activity.window, true)) {
                mStatuBarType = STATUSBAR_TYPE_FLYME
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Android6SetStatusBarLightMode(activity.window, true)
                mStatuBarType = STATUSBAR_TYPE_ANDROID6
            }
        }
    }

    /**
     * 设置状态栏白色字体图标
     * 支持 4.4 以上版本 MIUI 和 Flyme，以及 6.0 以上版本的其他 Android
     */
    fun setStatusBarDarkMode(activity: AppCompatActivity?): Boolean {
        if (activity == null) return false
        if (mStatuBarType == STATUSBAR_TYPE_DEFAULT) {
            // 默认状态，不需要处理
            return true
        }

        return when (mStatuBarType) {
            STATUSBAR_TYPE_MIUI -> MIUISetStatusBarLightMode(activity.window, false)
            STATUSBAR_TYPE_FLYME -> FlymeSetStatusBarLightMode(activity.window, false)
            STATUSBAR_TYPE_ANDROID6 -> Android6SetStatusBarLightMode(activity.window, false)
            else -> true
        }
    }

    private fun setStatusBarLightMode(activity: AppCompatActivity, type: Int) {
        when (type) {
            STATUSBAR_TYPE_MIUI -> MIUISetStatusBarLightMode(activity.window, true)
            STATUSBAR_TYPE_FLYME -> FlymeSetStatusBarLightMode(activity.window, true)
            STATUSBAR_TYPE_ANDROID6 -> Android6SetStatusBarLightMode(activity.window, true);
        }
    }

    /**
     * 设置状态栏字体图标为深色，需要 MIUIV6 以上
     *
     * @param window 需要设置的窗口
     * @param light  是否把状态栏字体及图标颜色设置为深色
     * @return boolean 成功执行返回 true
     */
    fun MIUISetStatusBarLightMode(window: Window?, light: Boolean): Boolean {
        var result = false
        if (window != null) {
            val clazz = window.javaClass
            try {
                val darkModeFlag: Int
                val layoutParams = Class.forName("android.view.MiuiWindowManager\$LayoutParams")
                val field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE")
                darkModeFlag = field.getInt(layoutParams)
                val extraFlagField =
                    clazz.getMethod(
                        "setExtraFlags",
                        Int::class.javaPrimitiveType,
                        Int::class.javaPrimitiveType
                    )
                if (light) {
                    extraFlagField.invoke(window, darkModeFlag, darkModeFlag)//状态栏透明且黑色字体
                } else {
                    extraFlagField.invoke(window, 0, darkModeFlag)//清除黑色字体
                }
                result = true
            } catch (ignored: Exception) {

            }

        }
        return result
    }

    /**
     * 设置状态栏图标为深色和魅族特定的文字风格
     * 可以用来判断是否为 Flyme 用户
     *
     * @param window 需要设置的窗口
     * @param light  是否把状态栏字体及图标颜色设置为深色
     * @return boolean 成功执行返回true
     */
    fun FlymeSetStatusBarLightMode(window: Window?, light: Boolean): Boolean {
        var result = false
        if (window != null) {
            // flyme 在 6.2.0.0A 支持了 Android 官方的实现方案，旧的方案失效
            Android6SetStatusBarLightMode(window, light)

            try {
                val lp = window.attributes
                val darkFlag = WindowManager.LayoutParams::class.java
                    .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON")
                val meizuFlags = WindowManager.LayoutParams::class.java
                    .getDeclaredField("meizuFlags")
                darkFlag.isAccessible = true
                meizuFlags.isAccessible = true
                val bit = darkFlag.getInt(null)
                var value = meizuFlags.getInt(lp)
                if (light) {
                    value = value or bit
                } else {
                    value = value and bit.inv()
                }
                meizuFlags.setInt(lp, value)
                window.attributes = lp
                result = true
            } catch (ignored: Exception) {

            }

        }
        return result
    }

    private fun Android6SetStatusBarLightMode(window: Window, light: Boolean): Boolean {
        val decorView = window.decorView
        var systemUi =
            if (light) View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR else View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        systemUi = changeStatusBarModeRetainFlag(window, systemUi)
        decorView.systemUiVisibility = systemUi
        if (isMIUIV9()) {
            MIUISetStatusBarLightMode(window, light)
        }
        return true
    }

    private fun changeStatusBarModeRetainFlag(window: Window, out: Int): Int {
        var out = out
        out = retainSystemUiFlag(window, out, View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        out = retainSystemUiFlag(window, out, View.SYSTEM_UI_FLAG_FULLSCREEN)
        out = retainSystemUiFlag(window, out, View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
        out = retainSystemUiFlag(window, out, View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        out = retainSystemUiFlag(window, out, View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        out = retainSystemUiFlag(window, out, View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
        return out
    }

    fun retainSystemUiFlag(window: Window, out: Int, type: Int): Int {
        var out = out
        val now = window.decorView.systemUiVisibility
        if (now and type == type) {
            out = out or type
        }
        return out
    }


    /**
     * 更改状态栏图标、文字颜色的方案是否是MIUI自家的， MIUI9 && Android 6 之后用回Android原生实现
     * 见小米开发文档说明：https://dev.mi.com/console/doc/detail?pId=1159
     */
    private fun isMIUICustomStatusBarLightModeImpl(): Boolean {
        return if (isMIUIV9() && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            true
        } else isMIUIV5() || isMIUIV6() ||
                isMIUIV7() || isMIUIV8()
    }

    fun isMIUIV5(): Boolean {
        return "v5" == sMiuiVersionName
    }

    fun isMIUIV6(): Boolean {
        return "v6" == sMiuiVersionName
    }

    fun isMIUIV7(): Boolean {
        return "v7" == sMiuiVersionName
    }

    fun isMIUIV8(): Boolean {
        return "v8" == sMiuiVersionName
    }

    fun isMIUIV9(): Boolean {
        return "v9" == sMiuiVersionName
    }

    fun getStatusbarHeight(context: Context): Int {
        if (sStatusbarHeight == -1) {
            initStatusBarHeight(context)
        }
        return sStatusbarHeight
    }

    private fun initStatusBarHeight(context: Context) {
        val clazz: Class<*>
        var obj: Any? = null
        var field: Field? = null
        try {
            clazz = Class.forName("com.android.internal.R\$dimen")
            obj = clazz.newInstance()
            if (isMeizu()) {
                try {
                    field = clazz.getField("status_bar_height_large")
                } catch (t: Throwable) {
                    t.printStackTrace()
                }

            }
            if (field == null) {
                field = clazz.getField("status_bar_height")
            }
        } catch (t: Throwable) {
            t.printStackTrace()
        }

        if (field != null && obj != null) {
            try {
                val id = Integer.parseInt(field.get(obj).toString())
                sStatusbarHeight = context.resources.getDimensionPixelSize(id)
            } catch (t: Throwable) {
                t.printStackTrace()
            }

        }

        if (isTablet(context) && sStatusbarHeight > DisplayHelper.dp2px(STATUS_BAR_DEFAULT_HEIGHT_DP.toFloat())) {
            //状态栏高度大于25dp的平板，状态栏通常在下方
            sStatusbarHeight = 0
        } else {
            if (sStatusbarHeight <= 0) {
                sStatusbarHeight = if (sVirtualDensity == -1f) {
                    DisplayHelper.dp2px(STATUS_BAR_DEFAULT_HEIGHT_DP.toFloat())
                } else {
                    (STATUS_BAR_DEFAULT_HEIGHT_DP * sVirtualDensity + 0.5f).toInt()
                }
            }
        }
    }


    /**
     * 判断是否为平板设备
     */
    private fun isTablet(context: Context): Boolean {
        if (sIsTabletChecked) {
            return sIsTabletValue
        }
        sIsTabletValue = _isTablet(context)
        sIsTabletChecked = true
        return sIsTabletValue
    }

    private fun _isTablet(context: Context): Boolean {
        return context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE
    }


    fun hideStatusBar(activity: AppCompatActivity) {
        activity.window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    fun showStatusBar(activity: AppCompatActivity) {
        activity.window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }


}