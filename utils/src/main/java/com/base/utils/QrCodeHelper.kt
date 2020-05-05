package com.base.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.lang.Exception
import java.util.*

/**
 *
 *@author abc
 *@time 2020/1/16 11:05
 */
object QrCodeHelper {

    private val UTF_8 = "utf-8"

    private val QR_CODE_BLACK = Color.BLACK
    private val QR_CODE_BACKGROUND = Color.WHITE

    fun createQrCode(
        content: String,
        width: Int,
        height: Int,
        logo: Bitmap? = null,
        background: Bitmap? = null,
        topLeftColor: Int = QR_CODE_BLACK,
        topRightColor: Int = QR_CODE_BLACK,
        bottomLeftColor: Int = QR_CODE_BLACK,
        bottomRightColor: Int = QR_CODE_BLACK
    ): Bitmap? {

        return createBackQrCode(
            addLogo(
                createFlower(
                    content,
                    width,
                    height,
                    topLeftColor,
                    topRightColor,
                    bottomLeftColor,
                    bottomRightColor
                ), width, height, logo
            ), background
        )
    }

    /**添加背景*/
    private fun createBackQrCode(qrCode: Bitmap?, background: Bitmap?): Bitmap? {
        if (qrCode == null || background == null) return qrCode
        val bWidth = background.width
        val bHeight = background.height

        val qWidth = qrCode.width
        val qHeight = qrCode.height

        val newBitmap = Bitmap.createBitmap(bWidth, bHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(newBitmap)
        canvas.drawBitmap(background, 0f, 0f, null)
        canvas.drawBitmap(
            qrCode,
            (bWidth - qWidth) * 1.0f / 2,
            (bHeight - qHeight) * 3.0F / 5 + 70,
            null
        )
        canvas.save()
        canvas.restore()
        return newBitmap
    }

    /**四种颜色*/
    private fun createFlower(
        content: String,
        width: Int,
        height: Int,
        topLeftColor: Int = QR_CODE_BLACK,
        topRightColor: Int = QR_CODE_BLACK,
        bottomLeftColor: Int = QR_CODE_BLACK,
        bottomRightColor: Int = QR_CODE_BLACK
    ): Bitmap? {
        if (width == 0 || height == 0) return null
        val hints =
            Hashtable<EncodeHintType, Any>()
        hints[EncodeHintType.CHARACTER_SET] = UTF_8
        hints[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.H// 容错率
        hints[EncodeHintType.MARGIN] = 2 // default is 4
        val matrix =
            MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints)

        val pixels = IntArray(width * height)

        for (y in 0 until height)
            for (x in 0 until width) {

                pixels[y * width + x] = when {
                    x < width / 2 && y < height / 2 -> if (matrix.get(
                            x,
                            y
                        )
                    ) topLeftColor else QR_CODE_BACKGROUND
                    x >= width / 2 && y < height / 2 -> if (matrix.get(
                            x,
                            y
                        )
                    ) topRightColor else QR_CODE_BACKGROUND
                    x < width / 2 && y >= height / 2 -> if (matrix.get(
                            x,
                            y
                        )
                    ) bottomLeftColor else QR_CODE_BACKGROUND
                    else -> if (matrix.get(x, y)) bottomRightColor else QR_CODE_BACKGROUND
                }

            }
        val bitmap = Bitmap.createBitmap(
            width,
            height,
            Bitmap.Config.ARGB_8888
        )
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return bitmap
    }

    /**添加logo*/
    private fun addLogo(bitmap: Bitmap?, width: Int, height: Int, logo: Bitmap?): Bitmap? {
        if (logo == null) return bitmap
        bitmap?.let {
            val logoWidth = logo.width
            val logoHeight = logo.height

            if (logoWidth == 0 || logoHeight == 0) return@let

            val scaleFactor = width * 1.0f / 2 / logoWidth
            try {
                val canvas = Canvas(bitmap)
                canvas.drawBitmap(bitmap, 0F, 0F, null)
                canvas.scale(scaleFactor, scaleFactor, width * 1.0f / 2, height * 1.0f / 2)
                canvas.drawBitmap(
                    logo,
                    (width - logoWidth) * 1.0f / 2,
                    (height - logoHeight) * 1.0f / 2,
                    null
                )
                canvas.save()
                canvas.restore()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return bitmap
    }
}