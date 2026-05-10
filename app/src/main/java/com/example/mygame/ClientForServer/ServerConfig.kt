package com.example.mygame.ClientForServer

import java.util.Properties

data class ServerEndpoint(val baseUrl: String)

fun loadServerEndpoint(): ServerEndpoint {
    val envHost = System.getenv("SERVER_HOST")?.trim()?.takeIf { it.isNotEmpty() }
    val envPort = System.getenv("SERVER_PORT")?.trim()?.takeIf { it.isNotEmpty() }

    val props = Properties()
    Thread.currentThread().contextClassLoader
        .getResourceAsStream("server.properties")
        ?.use { props.load(it) }

    val host = envHost ?: props.getProperty("server.host", "10.0.2.2").trim()
    val port = (envPort ?: props.getProperty("server.port", "8080")).trim().toInt()
    val base = "http://$host:$port/v1/game".trimEnd('/')
    return ServerEndpoint(base)
}
