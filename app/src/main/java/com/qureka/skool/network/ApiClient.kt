package com.qureka.skool.network

import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.chuckerteam.chucker.api.RetentionManager
import com.qureka.skool.BuildConfig
import com.qureka.skool.QurekaSkoolApplication
import com.qureka.skool.ServerConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    private const val CONNECTION_TIME_OUT = 30
    private const val READ_TIME_OUT = 30
    private const val WRITE_TIME_OUT = 30
    private const val BASE_URL = ServerConfig.appBaseUrl
    @JvmStatic
    fun getwithToken(token: String): Retrofit {
        val okHttpClientBuilder = getokHttpClientBuilder(token)

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClientBuilder.build())
            .addConverterFactory(NullOnEmptyConverterFactory())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun getokHttpClientBuilder(token: String): OkHttpClient.Builder {
        val okHttpClientBuilder = OkHttpClient.Builder()
        okHttpClientBuilder.connectTimeout(CONNECTION_TIME_OUT.toLong(), TimeUnit.SECONDS)
        okHttpClientBuilder.readTimeout(READ_TIME_OUT.toLong(), TimeUnit.SECONDS)
        okHttpClientBuilder.writeTimeout(WRITE_TIME_OUT.toLong(), TimeUnit.SECONDS)
        if (BuildConfig.DEBUG) {
            val collector = ChuckerCollector(
                QurekaSkoolApplication.getApplication().applicationContext,
                true,
                RetentionManager.Period.ONE_HOUR
            )
            val chuckerInterceptor: ChuckerInterceptor =
                ChuckerInterceptor.Builder(QurekaSkoolApplication.getApplication().applicationContext)
                    .collector(collector).maxContentLength(250000L)
                    .redactHeaders(emptySet<String>()).alwaysReadResponseBody(true).build()
            okHttpClientBuilder.addInterceptor(chuckerInterceptor)
        }
        okHttpClientBuilder.addInterceptor { chain ->
            val builder = chain.request().newBuilder()
            builder.addHeader("Content-Type", "application/json")
            builder.addHeader("Accept", "application/json")
            builder.addHeader("Authorization", token)
            builder.addHeader("app_id", ServerConfig.APP_ID)
            chain.proceed(builder.build())
        }
        return okHttpClientBuilder
    }


    @JvmStatic
    fun getHttpClient(): OkHttpClient {
        val okHttpClientBuilder = OkHttpClient.Builder()
        okHttpClientBuilder.connectTimeout(CONNECTION_TIME_OUT.toLong(), TimeUnit.SECONDS)
        okHttpClientBuilder.readTimeout(READ_TIME_OUT.toLong(), TimeUnit.SECONDS)
        okHttpClientBuilder.writeTimeout(WRITE_TIME_OUT.toLong(), TimeUnit.SECONDS)
        return okHttpClientBuilder.build()
    }
}