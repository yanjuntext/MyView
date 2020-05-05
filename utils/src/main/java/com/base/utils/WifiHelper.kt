package com.base.utils

import android.app.Service
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Build
import android.util.Log
import android.content.Context.WIFI_SERVICE
import android.graphics.drawable.Drawable
import android.net.wifi.WifiConfiguration
import androidx.core.content.ContextCompat.getSystemService
import kotlin.math.log


/**
 *
 *@author abc
 *@time 2020/1/10 11:27
 */
object WifiHelper {

    enum class WifiCapability {
        WIFI_CIPHER_WEP, WIFI_CIPHER_WPA, WIFI_CIPHER_NO_PASS
    }

    val UNKNOWN_SSID = "unknown ssid"


    /**获取当前WIFI SSID*/
    fun getCurrenWifiSSid(context: Context?): String? {
        if (context == null) return null
        return when {
            Build.VERSION.SDK_INT <= Build.VERSION_CODES.O ||
                    Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1 -> {
                val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE)
                if (wifiManager is WifiManager) {
                    val info = wifiManager.connectionInfo
                    var ssid = formatSsid(info.ssid ?: "")
                    Log.i("WifiHelper", "ssid[$ssid]")
                    if (ssid.contains(UNKNOWN_SSID)) {
                        val networkId = info.networkId
                        run outside@{
                            wifiManager.configuredNetworks.forEach {
                                if (it.networkId == networkId) {
                                    ssid = it.SSID
                                    return@outside
                                }
                            }
                        }
                    }
                    formatSsid(ssid)
                } else null
            }
            Build.VERSION.SDK_INT == Build.VERSION_CODES.O_MR1 -> {
                val wifiManager =
                    context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE)
                if (wifiManager is ConnectivityManager) {
                    val networkInfo = wifiManager.activeNetworkInfo
                    if (networkInfo.isConnected) {
                        var ssid = formatSsid(networkInfo.extraInfo ?: "")
                        if (ssid.contains(UNKNOWN_SSID)) {
                            val service =
                                context.applicationContext.getSystemService(Context.WIFI_SERVICE)
                            if (service is WifiManager) {
                                val info = service.connectionInfo
                                val networkId = info.networkId
                                run outside@{
                                    service.configuredNetworks.forEach {
                                        if (it.networkId == networkId) {
                                            ssid = formatSsid(it.SSID)
                                            return@outside
                                        }
                                    }
                                }
                            }
                        }
                        ssid
                    } else null
                } else null
            }
            else -> {
                val service = context.getSystemService(Context.CONNECTIVITY_SERVICE)
                if (service is ConnectivityManager) {
                    val state = service.getNetworkInfo(ConnectivityManager.TYPE_WIFI).state
                    if (state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING) {
                        val wifiManager = context.applicationContext
                            .getSystemService(Context.WIFI_SERVICE)
                        if (wifiManager is WifiManager) {
                            val wifiInfo = wifiManager.connectionInfo
                            MLog.i("wifNetworkReceiver", "get wifi" + wifiInfo.ssid)
                            formatSsid(
                                wifiInfo.ssid ?: ""
                            )
                        } else null
                    } else null
                } else null
            }
        }
    }

    /**格式化ssid*/
    private fun formatSsid(ssid: String): String {
        var mSsid = ssid
        if (mSsid.startsWith("\"") && mSsid.endsWith("\"")) {
            ssid.substring(1).also {
                mSsid = it.substring(0, it.length - 1)
            }
        }
        return mSsid
    }

    /**判断5G*/
    fun is5GHz(context: Context, ssid: String?): Boolean {
        val list = startScanWifi(context)
        return run outside@{
            list?.forEach {
                if (it.SSID == ssid) {
                    return@outside it.frequency in 4901..5899
                }
            }
            false
        }
    }

    /**扫描wifi列表*/
    fun startScanWifi(context: Context?): MutableList<ScanResult>? {
        val list = mutableListOf<ScanResult>()
        if (context == null) return list
        val service = context.applicationContext.getSystemService(Service.WIFI_SERVICE)
        if (service is WifiManager) {
            if (!service.isWifiEnabled) service.isWifiEnabled = true
            service.startScan()
            list.addAll(service.scanResults ?: mutableListOf())
        }
        return list
    }

    /**获取当前WIFI IP地址*/
    fun getLocalIpAddress(context: Context?): String? =
        context?.let {
            val service = it.getSystemService(Context.WIFI_SERVICE)
            var ipAddress = ""
            if (service is WifiManager) {
                if (!service.isWifiEnabled) service.isWifiEnabled = true
                val wifiInfo = service.connectionInfo
                val address = wifiInfo.ipAddress
                ipAddress =
                    "${address and 0xFF}.${(address shr 8) and 0xFF}.${(address shr 16) and 0xFF}.${(address shr 24) and 0xFF}"
            }
            ipAddress
        }

    /**获取设备列表*/
    fun getWifiList(context: Context?): MutableList<ScanResult> {
        val scanResult = mutableListOf<ScanResult>()
        if (context == null) return scanResult
        val wifiManager = context.getSystemService(WIFI_SERVICE) as WifiManager?
        wifiManager?.let { manager ->
            val list = manager.scanResults
            val map = mutableMapOf<String, Int>()
            list?.mapIndexed { i, it ->
                if (it.SSID.isNotEmpty()) {
                    val key = "${it.SSID} ${it.capabilities}"
                    if (!map.contains(key)) {
                        map[key] = i
                        scanResult.add(it)

                    }
                }
            }
        }
        return scanResult

    }

    /**获取WiFi信号强度*/
    fun getWifiRssiDrawRes(scanResult: ScanResult?): Int {
        val rssi: Int = scanResult?.level ?: 0
        return when {
            rssi > -50 && rssi < 0 -> R.drawable.ic_wifi_4
            rssi > -70 && rssi < -50 -> R.drawable.ic_wifi_3
            rssi > -80 && rssi < -70 -> R.drawable.ic_wifi_2
            rssi > -100 && rssi < -80 -> R.drawable.ic_wifi_1
            else -> R.drawable.ic_wifi_0
        }
    }

    fun getGateWay(context: Context?): String? {
        if (context == null) return null
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE)
        return if (wifiManager is WifiManager) {
            wifiManager.startScan()
            long2Ip(wifiManager.dhcpInfo.gateway)
        } else null
    }

    fun long2Ip(ip: Int): String {
        val b = IntArray(4)
        b[0] = ip shr 24 and 0xff
        b[1] = ip shr 16 and 0xff
        b[2] = ip shr 8 and 0xff
        b[3] = ip and 0xff
        val x: String
        x = (b[3].toString() + "." + b[2].toString() + "."
                + b[1].toString() + "." + b[0].toString())
        return x
    }

    /**连接指定Wifi*/
    fun connectWifi(context: Context?, ssid: String?, psw: String?): Boolean {
        if (context == null || ssid.isNullOrEmpty()) return false

        val manager =
            context.applicationContext.getSystemService(Service.WIFI_SERVICE) as WifiManager
        return startScanWifi(context)?.let { list ->
            val wifi = list.singleOrNull { it.SSID == ssid } ?: return false
            val config =
                manager.configuredNetworks.singleOrNull { it.SSID.replace("\"", "") == ssid }
            if (config != null) {
                manager.enableNetwork(config.networkId, true)
            } else {
                val cWifiConfig = createWifiConfig(
                    manager,
                    wifi.SSID,
                    psw ?: "",
                    getCipherType(wifi.capabilities)
                )
                manager.enableNetwork(manager.addNetwork(cWifiConfig), true)
            }
        } ?: false
    }

    private fun getTargetWifiIsConfig(manage: WifiManager, ssid: String): Boolean {
        return run outside@{
            manage.configuredNetworks?.forEach {
                if (it.SSID == ssid) {
                    return@outside true
                }
            }
            false
        }

    }

    private fun canScanWifi(manager: WifiManager, ssid: String): Boolean {
        manager.startScan()
        return run outside@{
            manager.scanResults?.forEach {
                if (it.SSID == ssid) return@outside true
            }
            false
        }

    }

    private fun createWifiConfig(
        manage: WifiManager,
        ssid: String,
        psw: String,
        type: WifiCapability
    ): WifiConfiguration {
//初始化WifiConfiguration
        val config = WifiConfiguration()
        config.allowedAuthAlgorithms.clear()
        config.allowedGroupCiphers.clear()
        config.allowedKeyManagement.clear()
        config.allowedPairwiseCiphers.clear()
        config.allowedProtocols.clear()

        Log.e("WifiHelper", "wifiHelper ssid[${ssid}]")
        config.priority = 40
        config.status = 1

        //指定对应的SSID
        config.SSID = "\"$ssid\""
        //如果之前有类似的配置
        isExsits(manage, ssid)?.let {
            manage.removeNetwork(it.networkId)
            manage.saveConfiguration()
        }

//        val tempConfig = manage.configuredNetworks.singleOrNull { it.SSID == "\"$ssid\"" }
//        if (tempConfig != null) {
//            //则清除旧有配置  不是自己创建的network 这里其实是删不掉的
//            manage.removeNetwork(tempConfig.networkId)
//            manage.saveConfiguration()
//        }


        when (type) {
            WifiCapability.WIFI_CIPHER_NO_PASS -> {
                //不需要密码的场景
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
            }
            WifiCapability.WIFI_CIPHER_WEP -> {
                //以WEP加密的场景
                config.hiddenSSID = true
                config.wepKeys[0] = "\"" + psw + "\""
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN)
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED)
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
                config.wepTxKeyIndex = 0
            }
            WifiCapability.WIFI_CIPHER_WPA -> {
                //以WPA加密的场景，自己测试时，发现热点以WPA2建立时，同样可以用这种配置连接
                config.preSharedKey = "\"" + psw + "\""
                config.hiddenSSID = true
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN)
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP)
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK)
                config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP)
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP)
                config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP)
                config.status = WifiConfiguration.Status.ENABLED
            }
        }

        return config
    }

    private fun getCipherType(capabilities: String): WifiCapability {
        return when {
            capabilities.contains("WEB") -> {
                WifiCapability.WIFI_CIPHER_WEP
            }
            capabilities.contains("PSK") -> {
                WifiCapability.WIFI_CIPHER_WPA
            }
            capabilities.contains("WPS") -> {
                WifiCapability.WIFI_CIPHER_NO_PASS
            }
            else -> {
                WifiCapability.WIFI_CIPHER_NO_PASS
            }
        }
    }

    fun isExsits(manager: WifiManager?, ssid: String?): WifiConfiguration? {
        if (manager == null || ssid.isNullOrEmpty()) return null
        return manager.configuredNetworks?.let { list ->
            val iterator = list.iterator()
            var existingConfig: WifiConfiguration? = null
            do {
                if (!iterator.hasNext()) {
                    return null
                }
                existingConfig = iterator.next()
            } while (existingConfig?.SSID != "\"$ssid\"")
            existingConfig
        } ?: null
    }

}