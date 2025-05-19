package com.example.rellotgejais.data.services



import com.example.rellotgejais.API_DOMAIN
import com.example.rellotgejais.API_PORT
import com.example.rellotgejais.API_PROTOCOL
import com.example.rellotgejais.models.Clock
import com.example.rellotgejais.models.PopulatedMatch
import com.example.rellotgejais.models.Referee
import com.example.rellotgejais.models.RefereeLoad
import com.example.rellotgejais.utils.LocalDateTimeAdapter
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
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

    // to load referee object again
    @POST("/referee/load")
    suspend fun loadReferee(@Body referee: RefereeLoad): Response<Referee>

    // to pair a clock
    @GET("/assignTo/{id}")
    suspend fun assignClock(@Path("id") id: Int, @Body clock: Clock)

    // cancel clock pairing
    @DELETE("/revoke/{id}")
    suspend fun revokeClock(@Path("id") id: Int)

    // to get matches with their data
    @GET("/matches/populated/{id}")
    suspend fun getMatch(@Path("id") id: Int): Response<PopulatedMatch>

    //To get code for QR
    @GET("/clock/generate")
    suspend fun generateCode(): Response<Clock>

}

object RetrofitManager {
    private const val BASE_URL = "$API_PROTOCOL://$API_DOMAIN:$API_PORT"

    //Desde aqui es posible colocar timeouts a las respuestas o asignar un Token si la app necesita uno
    private val client = getUnsafeOkHttpClient()

    private val gson = GsonBuilder()
        .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())
        .create()

    //El retrofitService se inicia a partir de la instancia en ViewModels
    val instance: RetrofitService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
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
        val sslContext = SSLContext.getInstance("SSL")
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