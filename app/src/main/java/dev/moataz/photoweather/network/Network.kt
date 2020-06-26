package dev.moataz.photoweather.network

import dev.moataz.photoweather.BuildConfig
import dev.moataz.photoweather.helper.Constant
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit


object Network {

    private lateinit var retrofit: Retrofit
    val apiService: ApiService by lazy { retrofit.create(
        ApiService::class.java) }

    fun init(baseUrl: String, isDebug: Boolean = false) {
        retrofit = Retrofit
            .Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(MoshiConverterFactory.create())

            .client(buildClient(isDebug))
            .build()
    }

    private fun buildClient(isDebug: Boolean): OkHttpClient {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder()
        client.addInterceptor(ApiInterceptor)
        if (isDebug) {
            client.addInterceptor(logging)
        }
        return client.build()
    }


    object ApiInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {

            val appid = BuildConfig.APP_ID

            var request = chain.request()
            val url: HttpUrl = request.url.newBuilder()
                .addQueryParameter("appid", appid)
                .build()
            request = request.newBuilder().url(url).build()

            return chain.proceed(request)

        }
    }


}