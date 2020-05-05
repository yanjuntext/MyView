package com.base.utils

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.lang.Exception
import java.lang.StringBuilder
import java.util.*
import kotlin.math.log

/**
 *
 *@author abc
 *@time 2019/11/26 16:51
 */
object FileHelper {
    private val TAG = FileHelper::class.java.simpleName

    enum class FileType {
        image, audio, video, download
    }

    private val Q_IMAGE_PATH = "Pictures"
    private val Q_MUSIC_PATH = "Music"
    private val Q_VIDEO_PATH = "Movies"
    private val Q_DOWLOAD_PATH = "Download"

    val IMAGE_FILE = 1
    val VIDEO_FILE = 2

    fun isSDCardValid() = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED

    /**获取*/
    fun getFileNameWithTime(type: Int): String {
        val c = Calendar.getInstance()
        val mYear = c.get(Calendar.YEAR)
        val mMonth = c.get(Calendar.MONTH) + 1
        val mDay = c.get(Calendar.DAY_OF_MONTH)
        val mHour = c.get(Calendar.HOUR_OF_DAY)
        val mMinute = c.get(Calendar.MINUTE)
        val mSec = c.get(Calendar.SECOND)
        val sb = StringBuffer()
        if (type == IMAGE_FILE) {
            sb.append("IMG_")
        } else if (type == VIDEO_FILE) {
            sb.append("MP4_")
        }
        sb.append(mYear)
        if (mMonth < 10)
            sb.append('0')
        sb.append(mMonth)
        if (mDay < 10)
            sb.append('0')
        sb.append(mDay)
        sb.append('_')
        if (mHour < 10)
            sb.append('0')
        sb.append(mHour)
        if (mMinute < 10)
            sb.append('0')
        sb.append(mMinute)
        if (mSec < 10)
            sb.append('0')
        sb.append(mSec)
        if (type == IMAGE_FILE)
            sb.append(".jpg")
        else if (type == VIDEO_FILE)
            sb.append(".mp4")

        return sb.toString()
    }

    /**创建File*/
    fun createFile(vararg grades: String): String? {
        if (!isSDCardValid()) return null
        val sb = StringBuilder()
        sb.append(Environment.getExternalStorageDirectory())
        grades.forEach {
            sb.append(File.separator)
                .append(it)
        }
        sb.append(File.separator)
        val file = File(sb.toString())
        if (!file.exists()) {
            file.mkdirs()
        }
        return file.absolutePath
    }

    //创建文件夹
    fun createAndroidQFiles(
        context: Context,
        fileType: FileType,
        relativePath: String,
        description: String
    ): Uri? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return null
        val resolver = context.contentResolver
        val values = ContentValues()
        return when (fileType) {
            FileType.image -> {
                values.put(MediaStore.Images.Media.DESCRIPTION, description)
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/*")
                values.put(MediaStore.Images.Media.RELATIVE_PATH, "$Q_IMAGE_PATH/$relativePath")
                resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            }
            FileType.audio -> {
                values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/*")
                values.put(MediaStore.Audio.Media.RELATIVE_PATH, "$Q_MUSIC_PATH/$relativePath")
                resolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values)
            }
            FileType.video -> {
                values.put(MediaStore.Video.Media.DESCRIPTION, description)
                values.put(MediaStore.Video.Media.MIME_TYPE, "video/*")
                values.put(MediaStore.Video.Media.RELATIVE_PATH, "$Q_VIDEO_PATH/$relativePath")
                resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values)
            }
            FileType.download -> {
//                values.put(MediaStore.Downloads.MIME_TYPE,"dowmloads/*")
                values.put(MediaStore.Downloads.RELATIVE_PATH, "$Q_DOWLOAD_PATH/$relativePath")
                resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
            }
            else -> null
        }
    }

    //查询某个文件是否存在
    fun isAndroidQFileExists(context: Context, fileType: FileType, fileName: String): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return true

        val projection =
            arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.RELATIVE_PATH,
                MediaStore.Images.Media.MIME_TYPE,
                MediaStore.Images.Media.DISPLAY_NAME
            )

        val args = arrayOf("$fileName")

        val cursor: Cursor? = when (fileType) {
            FileType.image -> {
                val selection = "${MediaStore.Images.Media.DISPLAY_NAME} like ?"
                val external = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                context.contentResolver.query(external, projection, selection, args, null)
            }
            FileType.video -> {
                val external = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                val selection = "${MediaStore.Video.Media.DISPLAY_NAME} like ?"
                context.contentResolver.query(external, projection, selection, args, null)
            }
            FileType.audio -> {
                val external = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                val selection = "${MediaStore.Audio.Media.DISPLAY_NAME} like ?"
                context.contentResolver.query(external, projection, selection, args, null)
            }
            FileType.download -> {
                val external = MediaStore.Downloads.EXTERNAL_CONTENT_URI
                val selection = "${MediaStore.Downloads.DISPLAY_NAME} like ?"
                context.contentResolver.query(external, projection, selection, args, null)
            }

            else -> null
        }
        cursor?.close()
        return cursor != null
    }

    /**
     * 获取某个文件夹下的所有文件
     * 默认按照修改时间排序
     * */
    fun getAndroidQFiles(
        context: Context,
        fileType: FileType,
        relativePath: String
    ): MutableList<QFile> {
        val list = mutableListOf<QFile>()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return list

        val args = arrayOf("%$relativePath%")

        //按照修改时间降序，最新的文件在最前面
        var external: Uri?
        val cursor: Cursor? = when (fileType) {
            FileType.image -> {
                val projection =
                    arrayOf(
                        MediaStore.Images.Media._ID,
                        MediaStore.Images.Media.RELATIVE_PATH,
                        MediaStore.Images.Media.MIME_TYPE,
                        MediaStore.Images.Media.DISPLAY_NAME
                    )
                val selection = "${MediaStore.Images.Media.RELATIVE_PATH} like ?"
                external = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                val sort = MediaStore.Images.Media.DATE_MODIFIED + " desc "
                context.contentResolver.query(external, projection, selection, args, sort)
            }
            FileType.video -> {
                val projection =
                    arrayOf(
                        MediaStore.Video.Media._ID,
                        MediaStore.Video.Media.RELATIVE_PATH,
                        MediaStore.Video.Media.MIME_TYPE,
                        MediaStore.Video.Media.DISPLAY_NAME,
                        MediaStore.Video.Media.DURATION
                    )
                external = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                val selection = "${MediaStore.Video.Media.RELATIVE_PATH} like ?"
                val sort = MediaStore.Video.Media.DATE_MODIFIED + " desc "
                context.contentResolver.query(external, projection, selection, args, sort)
            }
            FileType.audio -> {
                val projection =
                    arrayOf(
                        MediaStore.Audio.Media._ID,
                        MediaStore.Audio.Media.RELATIVE_PATH,
                        MediaStore.Audio.Media.MIME_TYPE,
                        MediaStore.Audio.Media.DISPLAY_NAME,
                        MediaStore.Audio.Media.DURATION
                    )

                external = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                val selection = "${MediaStore.Audio.Media.RELATIVE_PATH} like ?"
                val sort = MediaStore.Audio.Media.DATE_MODIFIED + " desc "
                context.contentResolver.query(external, projection, selection, args, sort)
            }
            FileType.download -> {

                val projection = arrayOf(
                    MediaStore.Downloads._ID,
                    MediaStore.Downloads.RELATIVE_PATH,
                    MediaStore.Downloads.MIME_TYPE,
                    MediaStore.Downloads.DISPLAY_NAME
                )

                external = MediaStore.Downloads.EXTERNAL_CONTENT_URI
                val selection = "${MediaStore.Downloads.RELATIVE_PATH} like ?"
                val sort = MediaStore.Downloads.DATE_MODIFIED + " desc "
                context.contentResolver.query(external, projection, selection, args, sort)
            }

            else -> {
                external = null
                null
            }
        }

        MLog.i(TAG, "cursor size[${cursor?.count}]")
        cursor?.use {
            if (it.moveToFirst()) {
                do {
                    val media = when (fileType) {
                        FileType.image -> {
                            val id = it.getLong(it.getColumnIndex(MediaStore.Images.Media._ID))
                            val path =
                                it.getString(it.getColumnIndex(MediaStore.Images.Media.RELATIVE_PATH))
                            val type =
                                it.getString(it.getColumnIndex(MediaStore.Images.Media.MIME_TYPE))
                            val name =
                                it.getString(it.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME))
                            MLog.i(TAG, "cursor  id[$id],path[$path],type[$type],name[$name]")

                            val duration = if (fileType == FileType.video) {
                                it.getInt(it.getColumnIndex(MediaStore.Video.Media.DURATION))
                            } else {
                                0
                            }
                            val uri = ContentUris.withAppendedId(external, id)
                            list.add(QFile(uri, name, id, duration))
                        }
                        FileType.video -> {
                            val id = it.getLong(it.getColumnIndex(MediaStore.Video.Media._ID))
                            val path =
                                it.getString(it.getColumnIndex(MediaStore.Video.Media.RELATIVE_PATH))
                            val type =
                                it.getString(it.getColumnIndex(MediaStore.Video.Media.MIME_TYPE))
                            val name =
                                it.getString(it.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME))
                            MLog.i(TAG, "cursor  id[$id],path[$path],type[$type],name[$name]")
                            val duration =
                                it.getInt(it.getColumnIndex(MediaStore.Video.Media.DURATION))

                            val uri = ContentUris.withAppendedId(external, id)
                            list.add(QFile(uri, name, id, duration))
                        }
                        FileType.audio -> {
                            val id = it.getLong(it.getColumnIndex(MediaStore.Audio.Media._ID))
                            val path =
                                it.getString(it.getColumnIndex(MediaStore.Audio.Media.RELATIVE_PATH))
                            val type =
                                it.getString(it.getColumnIndex(MediaStore.Audio.Media.MIME_TYPE))
                            val name =
                                it.getString(it.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME))
                            MLog.i(TAG, "cursor  id[$id],path[$path],type[$type],name[$name]")
                            val duration =
                                it.getInt(it.getColumnIndex(MediaStore.Audio.Media.DURATION))

                            val uri = ContentUris.withAppendedId(external, id)
                            list.add(QFile(uri, name, id, duration))
                        }
                        FileType.download -> {

                            val id = it.getLong(it.getColumnIndex(MediaStore.Downloads._ID))
                            val path =
                                it.getString(it.getColumnIndex(MediaStore.Downloads.RELATIVE_PATH))
                            val type =
                                it.getString(it.getColumnIndex(MediaStore.Downloads.MIME_TYPE))
                            val name =
                                it.getString(it.getColumnIndex(MediaStore.Downloads.DISPLAY_NAME))
                            MLog.i(TAG, "cursor  id[$id],path[$path],type[$type],name[$name]")
                            val uri = ContentUris.withAppendedId(external, id)
                            list.add(QFile(uri, name, id, 0))

                        }
                        else -> null
                    }

                } while (it.moveToNext())
            }

        }
        return list
    }

    /**获取文件路径*/
    fun getShareFile(absolutePath: String?, name: String): String? {
        return "${absolutePath ?: ""}${File.separator}${name}"
    }

    /**保存图片*/
    fun saveImage(filePath: String?, bitmap: Bitmap?): Boolean {
        if (filePath.isNullOrEmpty() || bitmap == null) return false
        return FileOutputStream(filePath, false).use { fos ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos)
            fos.flush()
            true
        }
        /*  var fos: FileOutputStream? = null
          return try {
              fos = FileOutputStream(filePath, false)
              bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos)
              fos.flush()
              fos.close()
              true
          } catch (e: Exception) {
              e.printStackTrace()
              false
          } finally {
              try {
                  fos?.close()
              } catch (e: Exception) {
                  e.printStackTrace()
              }
          }
  */
    }

    /**文件目录、uid加密*/
    fun getDevUid(uid: String?): String {
        if (uid.isNullOrEmpty()) return ""
        return if (uid.length > 5) {
            MD5.MD5("${MD5.MD5Scode(uid)}${uid.substring(0, 5)}") ?: ""
        } else MD5.MD5("${MD5.MD5Scode(uid)}") ?: ""
    }

    fun getVideoFileTime(file: File?): String? {
        if (file == null) return null
        return try {
            val mmr = MediaMetadataRetriever()
            mmr.setDataSource(file.path)
            val time =
                mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION).toInt() / 1000
            String.format("%02d:%02d", time / 60, time % 60)
        } catch (e: Exception) {
            e.printStackTrace()
            "00:00"
        }
    }

    /**保存图片*/
    fun saveImageToFile(context: Context, path: String, name: String, bitmap: Bitmap): String {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            saveImage("$path/$name", bitmap)
            "$path${File.separator}$name"
        } else {
            val resolver = context.contentResolver
            val value = ContentValues().also {
                //                it.put(MediaStore.Images.Media.DESCRIPTION)
                it.put(MediaStore.Images.Media.DISPLAY_NAME, name)
                it.put(MediaStore.Images.Media.MIME_TYPE, "image/*")
                it.put(MediaStore.Images.Media.TITLE, name)
                it.put(MediaStore.Images.Media.RELATIVE_PATH, "$Q_IMAGE_PATH/$path")
            }
            val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, value)
            uri?.let {
                resolver.openOutputStream(uri)?.use {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, it)
                }
            }

            "$Q_IMAGE_PATH${File.separator}$path${File.separator}$name"
        }
    }


    data class QFile(
        val uri: Uri,
        val name: String,
        val id: Long,
        val duration: Int
    )


}