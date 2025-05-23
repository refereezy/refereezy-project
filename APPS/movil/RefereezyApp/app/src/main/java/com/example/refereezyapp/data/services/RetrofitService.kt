package com.example.refereezyapp.data.services

import com.example.refereezyapp.Config
import com.example.refereezyapp.data.models.Clock
import com.example.refereezyapp.data.models.Match
import com.example.refereezyapp.data.models.PopulatedMatch
import com.example.refereezyapp.data.models.Referee
import com.example.refereezyapp.data.models.RefereeLoad
import com.example.refereezyapp.data.models.RefereeLogin
import com.example.refereezyapp.data.models.RefereeUpdate
import com.example.refereezyapp.utils.LocalDateTimeAdapter
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.time.LocalDateTime
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

interface RetrofitService {
    // test
    @GET("/")
    suspend fun testConnection(): Response<String>

    // to login
    @POST("/referee/login")
    suspend fun login(@Body credentials: RefereeLogin): Response<Referee>

    // to load referee object again
    @POST("/referee/load")
    suspend fun loadReferee(@Body token: RefereeLoad): Response<Referee>

    // to update password
    @PATCH("/referee/{id}/password")
    suspend fun changePassword(@Path("id") id: Int, @Body update: RefereeUpdate): Response<Referee>

    // to pair a clock
    @POST("/assignTo/{id}")
    suspend fun assignClock(@Path("id") id: Int, @Body clock: Clock)

    // cancel clock pairing
    @DELETE("/revoke/{id}")
    suspend fun revokeClock(@Path("id") id: Int)

    // to get referee matches
    @GET("/referee/{id}/matches")
    suspend fun getRefereeMatches(@Path("id") id: Int): Response<List<Match>>

    // to get matches with their data
    @GET("/matches/populated/{id}")
    suspend fun getMatch(@Path("id") id: Int): Response<PopulatedMatch>

}


object RetrofitManager {
    private const val API_URL = "${Config.API_PROTOCOL}://${Config.API_DOMAIN}:${Config.API_PORT}"

    //Desde aqui es posible colocar timeouts a las respuestas o asignar un Token si la app necesita uno

    private val gson = GsonBuilder()
        .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())
        .create()

    //El retrofitService se inicia a partir de la instancia en ViewModels
    val instance: RetrofitService by lazy {
        Retrofit.Builder()
            .baseUrl(API_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(getUnsafeOkHttpClient())
            .build()
            .create(RetrofitService::class.java)
    }

}

private fun getUnsafeOkHttpClient(): OkHttpClient {
    try {
        // Create a trust manager that does not validate certificate chains
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            @Throws(CertificateException::class)
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
            }

            @Throws(CertificateException::class)
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
            }

            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return arrayOf()
            }
        }
        )

        // Install the all-trusting trust manager
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, trustAllCerts, SecureRandom())
        // Create an ssl socket factory with our all-trusting manager
        val sslSocketFactory: SSLSocketFactory = sslContext.socketFactory

        val builder = OkHttpClient.Builder()
        builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
        builder.hostnameVerifier { hostname, session -> true }

        val okHttpClient = builder.build()
        return okHttpClient
    } catch (e: Exception) {
        throw RuntimeException(e)
    }
}