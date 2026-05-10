package com.example.mygame.ClientForServer

import com.example.mygame.ClientForServer.Models.MyclassData
import com.example.mygame.ClientForServer.Models.Result
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json


class GameApiClient {
    private val endpoint = loadServerEndpoint()

    private val root = endpoint.baseUrl.trimEnd('/')

    //private val gson = Gson()

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    encodeDefaults = true
                    isLenient = true
                }
            )
        }
    }

    suspend fun myClass(a: Int, b: Int): Result{
        return client.post("$root/myClass"){
            contentType(ContentType.Application.Json)
            setBody(MyclassData(a1 = a,b1 = b))
        }.body()
    }

    /*
    fun myClass(a: Int, b: Int, onResult: (Int) -> Unit = {}, onError: (Throwable) -> Unit = {}) {
        Thread {
            try {
                val url = URL("${endpoint.baseUrl}/myClass")
                val conn = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    connectTimeout = 5000
                    readTimeout = 5000
                    doOutput = true
                    setRequestProperty("Content-Type", "application/json; charset=utf-8")
                    setRequestProperty("Accept", "application/json")
                }

                val body = gson.toJson(MyclassRequest(a1 = a, b1 = b))
                conn.outputStream.use { out ->
                    out.write(body.toByteArray(Charsets.UTF_8))
                }

                val responseCode = conn.responseCode
                val stream = if (responseCode in 200..299) conn.inputStream else conn.errorStream
                val responseText = stream?.use { s ->
                    BufferedReader(InputStreamReader(s)).readText()
                }.orEmpty()

                if (responseCode in 200..299) {
                    val parsed = gson.fromJson(responseText, MyClassResult::class.java)
                    onResult(parsed.sum)
                } else {
                    onError(IllegalStateException("HTTP $responseCode: $responseText"))
                }
            } catch (t: Throwable) {
                onError(t)
            }
        }.start()
    }
    */
}