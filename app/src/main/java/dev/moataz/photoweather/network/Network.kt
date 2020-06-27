package dev.moataz.photoweather.network

import dev.moataz.photoweather.BuildConfig
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


object Network {

    private lateinit var retrofit: Retrofit
    val apiService: ApiService by lazy {
        retrofit.create(
            ApiService::class.java
        )
    }

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


    //Inject appid and metric uints for all outgoing requests to api
    object ApiInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val appid = BuildConfig.APP_ID
            var request = chain.request()
            val url: HttpUrl = request.url.newBuilder()
                .addQueryParameter("appid", appid)
                .addQueryParameter("units", "metric")
                .build()
            request = request.newBuilder().url(url).build()
            return chain.proceed(request)

        }
    }


}