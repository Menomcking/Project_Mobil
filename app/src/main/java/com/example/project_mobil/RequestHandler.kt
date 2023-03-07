package com.example.project_mobil

import java.io.*
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import kotlin.Throws

object RequestHandler {
    @Throws(IOException::class)
    operator fun get(url: String): Response {
        val conn = setupConnection(url)
        return getResponse(conn)
    }

    @Throws(IOException::class)
    fun post(url: String, data: String): Response {
        val conn = setupConnection(url)
        conn.requestMethod = "POST"
        addRequestBody(conn, data)
        return getResponse(conn)
    }

    @Throws(IOException::class)
    fun put(url: String, data: String): Response {
        val conn = setupConnection(url)
        conn.requestMethod = "PUT"
        addRequestBody(conn, data)
        return getResponse(conn)
    }

    @Throws(IOException::class)
    fun delete(url: String): Response {
        val conn = setupConnection(url)
        conn.requestMethod = "DELETE"
        return getResponse(conn)
    }

    @Throws(IOException::class)
    private fun addRequestBody(conn: HttpURLConnection, data: String) {
        conn.setRequestProperty("Content-Type", "application/json")
        conn.doOutput = true
        val os = conn.outputStream
        val writer = BufferedWriter(OutputStreamWriter(os, StandardCharsets.UTF_8))
        writer.write(data)
        writer.flush()
        writer.close()
        os.close()
    }

    @Throws(IOException::class)
    private fun setupConnection(url: String): HttpURLConnection {
        val urlObj = URL(url)
        val conn = urlObj.openConnection() as HttpURLConnection
        conn.setRequestProperty("Accept", "application/json")
        conn.connectTimeout = 10000
        conn.readTimeout = 10000
        return conn
    }

    @Throws(IOException::class)
    private fun getResponse(conn: HttpURLConnection): Response {
        val responseCode = conn.responseCode
        val `is`: InputStream
        `is` = if (responseCode < 400) {
            conn.inputStream
        } else {
            conn.errorStream
        }
        val builder = StringBuilder()
        val br = BufferedReader(InputStreamReader(`is`))
        var sor = br.readLine()
        while (sor != null) {
            builder.append(sor)
            sor = br.readLine()
        }
        br.close()
        `is`.close()
        return Response(responseCode, builder.toString())
    }
}