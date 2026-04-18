package com.fintech.app.network

import com.fintech.app.BuildConfig
import com.fintech.app.data.JawaliConfig
import com.fintech.app.data.SessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 * ★ تعديل جوهري: إضافة Retrofit منفصل لبوابة جوالي
 *
 * في المواصفات الأصلية (PHP SDK + JS SDK)، بوابة جوالي لها BASE_URL منفصل:
 *   JAWALI_BASE_URL=https://82.114.179.89:9493/paygate
 * بينما API المحفظة له BASE_URL مختلف.
 *
 * التعديلات:
 * 1. إضافة @Named("jawali") Retrofit instance بـ BASE_URL منفصل
 * 2. إضافة دعم SSL verification control — مطابق لـ disable_ssl_verification
 * 3. إضافة timeout configurable — مطابق لـ JAWALI_TIMEOUT
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(sessionManager: SessionManager): OkHttpClient {

        // ── Interceptor لـ API أثير (wallet/auth) — يضيف Bearer token ──
        val authInterceptor = Interceptor { chain ->
            val token = runBlocking { sessionManager.token.first() }

            val requestBuilder = chain.request().newBuilder()
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")

            if (!token.isNullOrBlank()) {
                requestBuilder.addHeader("Authorization", "Bearer $token")
            }

            chain.proceed(requestBuilder.build())
        }

        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG)
                HttpLoggingInterceptor.Level.BODY
            else
                HttpLoggingInterceptor.Level.NONE
        }

        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): WalletApiService =
        retrofit.create(WalletApiService::class.java)

    // ═══════════════════════════════════════════════════════════
    // ★ Jawali Gateway — Retrofit instance منفصل
    // ═══════════════════════════════════════════════════════════
    // مطابق لـ JawaliConfig.url.base + disableSslVerification
    // في JS SDK: createAxiosInstance(config.url.disableSslVerification)
    // في PHP SDK: Http::withoutVerifying() إذا disable_ssl_verification = true

    @Provides
    @Singleton
    @Named("jawali")
    fun provideJawaliOkHttpClient(jawaliConfig: JawaliConfig): OkHttpClient {
        val disableSsl = runBlocking { jawaliConfig.disableSsl.first() }
        val timeout = runBlocking { jawaliConfig.timeout.first() }

        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG)
                HttpLoggingInterceptor.Level.BODY
            else
                HttpLoggingInterceptor.Level.NONE
        }

        val builder = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(timeout.toLong(), TimeUnit.SECONDS)
            .readTimeout(timeout.toLong(), TimeUnit.SECONDS)
            .writeTimeout(timeout.toLong(), TimeUnit.SECONDS)

        // ★ تعديل: دعم disableSslVerification — مطابق لـ createAxiosInstance()
        if (disableSsl) {
            try {
                val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                    override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
                    override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
                    override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
                })

                val sslContext = SSLContext.getInstance("SSL")
                sslContext.init(null, trustAllCerts, SecureRandom())

                builder.sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
                builder.hostnameVerifier { _, _ -> true }
            } catch (e: Exception) {
                // إذا فشل إعداد SSL، نستمر بالعميل العادي
            }
        }

        return builder.build()
    }

    @Provides
    @Singleton
    @Named("jawali")
    fun provideJawaliRetrofit(
        @Named("jawali") client: OkHttpClient,
        jawaliConfig: JawaliConfig
    ): Retrofit {
        val baseUrl = runBlocking { jawaliConfig.baseUrl.first() }
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideJawaliGatewayApi(@Named("jawali") retrofit: Retrofit): JawaliGatewayApi =
        retrofit.create(JawaliGatewayApi::class.java)
}
