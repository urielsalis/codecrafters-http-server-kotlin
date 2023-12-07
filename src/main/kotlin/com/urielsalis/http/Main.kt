package com.urielsalis.http

import java.io.IOException
import java.net.ServerSocket
import java.net.Socket

fun main() {
    var clientSocket: Socket? = null

    try {
        val serverSocket = ServerSocket(4221)
        serverSocket.reuseAddress = true
        val clientSocket = serverSocket.accept()
        println("accepted new connection")
    } catch (e: IOException) {
        println("IOException: " + e.message)
    }
}