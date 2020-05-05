package com.wyj.base.http.okhttp

import com.base.utils.MD5
import com.base.utils.TimeHelper
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.wyj.base.http.LoginToken
import com.wyj.base.http.retrofit2.CustomGsonConverterFactory
import com.wyj.base.log
import com.wyj.base.util.WheelServiceUtils
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.io.IOException
import java.util.concurrent.TimeUnit

object RetrofitUtils {
    private val mTimeOut: Long by lazy { 10L }

    private val okHttpClient: OkHttpClient by lazy {
        getOkHttpClient(
            getRequestInterceptor(),
            getLoggingInterceptor()
        )
    }

    fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("")
            .client(okHttpClient)
            .addConverterFactory(CustomGsonConverterFactory.create(getGson()))
//            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    fun getRetrofit(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(CustomGsonConverterFactory.create(getGson()))
            .build()
    }



    fun getNoTokenRetrofit(baseUrl: String):Retrofit{
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(getOkHttpClient( getRequestInterceptor(false),
                getLoggingInterceptor()))
            .addConverterFactory(CustomGsonConverterFactory.create(getGson()))
            .build()
    }

    private fun getGson(): Gson = GsonBuilder().setDateFormat("yyyy-MM-dd hh:mm:ss").create()

    private fun getRequestInterceptor(hasToken:Boolean = true): Interceptor {
        return object : Interceptor {
            @Throws(IOException::class)
            override fun intercept(chain: Interceptor.Chain): Response {
                val originalRequest = chain.request()
                val method = originalRequest.method
                /***
                 * token为空时，无需设置apiToken
                 */
                Interceptor.log(originalRequest.url.toString())

//                MLog.e("Interceptor", "Interceptor[" + originalRequest.url.toString() + "]")
                RetrofitUtils.log("token[${LoginToken.mLogined}]")
                return if (LoginToken.mLogined && hasToken) {
                    when (method) {
                        "GET" -> {
                            getGetRequest(originalRequest, chain)
                        }
                        "POST" -> {
                            getPostResponse(originalRequest, chain)
                        }
                        else -> {
                            chain.proceed(originalRequest)
                        }
                    }
                } else {
                    val originalResponse = chain.proceed(originalRequest)
                    originalResponse.newBuilder().build()
                }
            }
        }
    }

    private fun getLoggingInterceptor(): HttpLoggingInterceptor {
        val loggingInterceptor = HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
            override fun log(message: String) {
                "OkHttpClient".log(message)
            }
        })
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return loggingInterceptor
    }

    private fun getOkHttpClient(
        requestInterceptor: Interceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ) =
        OkHttpClient.Builder()
            .retryOnConnectionFailure(true)
            .connectTimeout(5L, TimeUnit.SECONDS)
            .readTimeout(mTimeOut, TimeUnit.SECONDS)
            .writeTimeout(mTimeOut, TimeUnit.SECONDS)
//            .addInterceptor(loggingInterceptor)
//            .addNetworkInterceptor(requestInterceptor)
            .addInterceptor(requestInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()


    private fun getGetRequest(originalRequest: Request, chain: Interceptor.Chain): Response {
        val builder = originalRequest.url.newBuilder()
        val newUrl = builder
            .addQueryParameter("userid", LoginToken.mUser ?: "")
            .addQueryParameter("platform", LoginToken.mPlatform ?: "")
            .addQueryParameter(
                "scode",
                MD5.MD5Scode(LoginToken.mRandom?:"")
            )
            .addQueryParameter("appn", "cengcen")
            .addQueryParameter("tzsec", TimeHelper.getCurrentDiffTimeZoneSec().toString())
            .build()
        return chain.proceed(originalRequest.newBuilder().url(newUrl).build())
    }

    private fun getPostResponse(originalRequest: Request, chain: Interceptor.Chain): Response {
        val body = originalRequest.body
        val newBody = when (body) {
            is FormBody -> {
                addParamsToFormBody(body)
            }
            is MultipartBody -> {
                addParamsToMultipartBody(body)
            }
            else -> {
                body
            }
        }
        return if (newBody != null) {
            val newRequest = originalRequest.newBuilder()
                .url(originalRequest.url)
                .method(originalRequest.method, newBody)
                .build()
            chain.proceed(newRequest)
        } else {
            chain.proceed(originalRequest)
        }
    }

    /**为FormBody类型请求体添加参数*/
    private fun addParamsToFormBody(body: FormBody?): FormBody {
        val builder = FormBody.Builder()

        builder.add("userid", LoginToken.mUser ?: "")
        builder.add("platform", LoginToken.mPlatform ?: "")
        builder.add("scode", MD5.MD5Scode(LoginToken.mRandom?:"")?:"")
        builder.add("appn", "cengcen")
        builder.add("tzsec",TimeHelper.getCurrentDiffTimeZoneSec().toString())
        body?.let {
            val size = it.size
            for (i in 0 until size) {
                builder.addEncoded(it.encodedName(i), it.encodedValue(i))
            }
        }
        return builder.build()
    }

    /**为MultipartBody类型请求体添加参数*/
    private fun addParamsToMultipartBody(body: MultipartBody?): MultipartBody {
        val builder = MultipartBody.Builder()
        builder.setType(MultipartBody.FORM)

        builder.addFormDataPart("userid", LoginToken.mUser ?: "")
        builder.addFormDataPart("platform", LoginToken.mPlatform ?: "")
        builder.addFormDataPart("scode", MD5.MD5Scode(LoginToken.mRandom?:"")?:"")
        builder.addFormDataPart("appn","cengcen")
        builder.addFormDataPart("tzsec",TimeHelper.getCurrentDiffTimeZoneSec().toString())
        body?.let {
            val size = it.size
            for (i in 0 until size) {
                builder.addPart(it.part(i))
            }
        }
        return builder.build()
    }
}