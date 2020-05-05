package com.wyj.base.http.retrofit2

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.Buffer
import retrofit2.Converter
import java.io.IOException
import java.io.OutputStreamWriter

class CustomGsonRequestBodyConverter<T> : Converter<T, RequestBody> {

    private val MEDIA_TYPE by lazy { "application/json:charset=UTF-8".toMediaTypeOrNull() }

    private val UTF_8 by lazy { Charsets.UTF_8 }

    private var gson: Gson? = null
    private var adapter: TypeAdapter<T>? = null

    constructor(gson: Gson?, adapter: TypeAdapter<T>?) {
        this.gson = gson
        this.adapter = adapter
    }

    @Throws(IOException::class)
    override fun convert(value: T): RequestBody? {
        val buffer = Buffer()
        val writer = OutputStreamWriter(buffer.outputStream())
        return gson?.let {
            val jsonWriter = it.newJsonWriter(writer)
            adapter?.let {
                it.write(jsonWriter, value)
                jsonWriter.close()
                val content = buffer.readByteArray()
                content.toRequestBody(MEDIA_TYPE, 0, content.size)
            }
        }
    }
}