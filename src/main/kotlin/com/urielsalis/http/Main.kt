package com.urielsalis.http

import java.io.IOException
import java.net.ServerSocket

fun main() {
    try {
        val serverSocket = ServerSocket(4221)
        serverSocket.reuseAddress = true
        val handler = ConnectionHandler(serverSocket.accept(), HttpServer::handle)
        handler.handle()
    } catch (e: IOException) {
        throw RuntimeException(e)
    }
}
