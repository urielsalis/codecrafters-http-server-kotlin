package com.urielsalis.http

import java.io.IOException
import java.net.ServerSocket
import java.util.concurrent.Executors

fun main() {
    try {
        val serverSocket = ServerSocket(4221)
        serverSocket.reuseAddress = true
        val executor = Executors.newCachedThreadPool()
        while (true) {
            val handler = ConnectionHandler(serverSocket.accept(), HttpServer::handle)
            executor.submit { handler.handle() }
        }
    } catch (e: IOException) {
        throw RuntimeException(e)
    }
}
