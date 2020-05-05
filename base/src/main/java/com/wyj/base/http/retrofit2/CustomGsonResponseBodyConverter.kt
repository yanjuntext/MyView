package com.wyj.base.http.retrofit2

import com.base.utils.MLog
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.wyj.base.http.Constants
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Converter
import java.io.ByteArrayInputStream
import java.io.InputStreamReader

class CustomGsonResponseBodyConverter<T> : Converter<ResponseBody, T> {
    val TAG: String by lazy { CustomGsonResponseBodyConverter::class.java.simpleName }
    var gson: Gson? = null
    var adapter: TypeAdapter<T>? = null

    constructor(gson: Gson?, adapter: TypeAdapter<T>?) {
        this.gson = gson
        this.adapter = adapter
    }


    override fun convert(value: ResponseBody): T? {
        val response = value.string()
        MLog.d(TAG, "$response[${gson == null}")
        if (gson == null) throw ApiException(1, "gson is null")

        val httpStatus = gson?.fromJson(response, HttpStatus::class.java)
        MLog.d(TAG, "httpStatus${httpStatus?.code}")
        if (httpStatus?.isCodeInvalid() == true) {
            value.close()
            val apiException =
                ApiException(httpStatus.code ?: Constants.WEB_RESP_CODE_DEFAULT_FAILURE, "")
            MLog.d(TAG, "apiException${apiException.code}")
            throw  apiException
        }
        MLog.e(TAG, "2 ResponseBody == $response")

        val jsonObject = JSONObject(response)
        if (jsonObject.isNull("msg") || jsonObject.getString("msg").isNullOrEmpty()) {
            MLog.e(TAG, "msg is null")
            value.close()
            throw ApiException(Constants.WEB_RESP_CODE_FAILURE, "")
        }

        MLog.e(TAG, "-----------")
        val contentType = value.contentType()
        val charset =
            if (contentType != null) contentType.charset(Charsets.UTF_8) else Charsets.UTF_8

        val inputStream = ByteArrayInputStream(response.toByteArray())
        val reader = InputStreamReader(inputStream, charset)
        val jsonReader = gson?.newJsonReader(reader)
        value.use { value ->
            return adapter?.read(jsonReader)
        }
    }


}