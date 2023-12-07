package com.urielsalis.http

import java.io.IOException
import java.net.ServerSocket
import java.util.concurrent.Executors

fun main(args: Array<String>) {
    val directory =
        if (args.size >= 2 && args[0] == "--directory") {
            args[1]
        } else {
            null
        }
    val server = HttpServer(directory)
    try {
        val serverSocket = ServerSocket(4221)
        serverSocket.reuseAddress = true
        val executor = Executors.newCachedThreadPool()
        while (true) {
            val handler = ConnectionHandler(serverSocket.accept(), server::handle)
            executor.submit { handler.handle() }
        }
    } catch (e: IOException) {
        throw RuntimeException(e)
    }
}
