package com.example.revistauteq

import android.os.Bundle
import android.util.Log
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley

class MainActivity : AppCompatActivity() {

    private val tag = "MainActivity"
    private val apiUrl =
        "https://apiws.uteq.edu.ec/h6RPoSoRaah0Y4Bah28eew/functions/information/entity/5"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val lvRevistas = findViewById<ListView>(R.id.lvRevistas)
        val revistas = ArrayList<Revista>()
        val adapter = RevistaAdapter(this, revistas)
        lvRevistas.adapter = adapter

        val queue = Volley.newRequestQueue(this)

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
}
