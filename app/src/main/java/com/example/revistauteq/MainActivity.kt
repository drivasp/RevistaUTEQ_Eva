package com.example.revistauteq

import android.os.Bundle
import android.util.Log
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.RequestQueue
import com.android.volley.toolbox.HurlStack
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import okhttp3.OkHttpClient
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class MainActivity : AppCompatActivity() {

    private val tag = "MainActivity"
    private val apiUrl =
        "https://apiws.uteq.edu.ec/h6RPoSoRaah0Y4Bah28eew/functions/information/entity/5"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        configurarGlideSeguro()

        val lvRevistas = findViewById<ListView>(R.id.lvRevistas)
        val revistas = ArrayList<Revista>()
        val adapter = RevistaAdapter(this, revistas)
        lvRevistas.adapter = adapter

        val queue = crearColaSegura()

        val request = object : JsonArrayRequest(
            Method.GET,
            apiUrl,
            null,
            { response ->
                revistas.clear()
                val total = minOf(response.length(), 10)
                for (i in 0 until total) {
                    val obj = response.getJSONObject(i)
                    revistas.add(
                        Revista(
                            anio = obj.getInt("anio"),
                            mes = obj.getInt("mes"),
                            urlportada = obj.getString("urlportada"),
                            urlpw = obj.getString("urlpw")
                        )
                    )
                }
                adapter.notifyDataSetChanged()
            },
            { error ->
                Log.e(tag, "Error Volley: ${error.message}")
                Toast.makeText(
                    this,
                    "Error al cargar revistas: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                return mutableMapOf("Authorization" to "Bearer ${BuildConfig.API_TOKEN}")
            }
        }

        queue.add(request)
    }

    private fun configurarGlideSeguro() {
        val trustAll = arrayOf<TrustManager>(object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        })
        val sslCtx = SSLContext.getInstance("TLS")
        sslCtx.init(null, trustAll, SecureRandom())
        val okHttp = OkHttpClient.Builder()
            .sslSocketFactory(sslCtx.socketFactory, trustAll[0] as X509TrustManager)
            .hostnameVerifier { _, _ -> true }
            .build()
        Glide.get(this).registry.replace(
            GlideUrl::class.java,
            InputStream::class.java,
            OkHttpUrlLoader.Factory(okHttp)
        )
    }

    private fun crearColaSegura(): RequestQueue {
        val trustAll = arrayOf<TrustManager>(object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        })
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, trustAll, SecureRandom())
        val factory = sslContext.socketFactory

        val hurlStack = object : HurlStack() {
            override fun createConnection(url: URL): HttpURLConnection {
                val conn = super.createConnection(url)
                if (conn is HttpsURLConnection) {
                    conn.sslSocketFactory = factory
                    conn.hostnameVerifier = HostnameVerifier { _, _ -> true }
                }
                return conn
            }
        }
        return Volley.newRequestQueue(this, hurlStack)
    }
}
