package com.compi.formularios.util

import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

object ClientRed {
    // Instancia limpia y única para la app
    val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    const val BASE_URL = "http://192.168.1.10:5000"
}