package com.urielsalis.http

import java.net.Socket

class ConnectionHandler(clientSocket: Socket, val handler: (Request) -> Response) {
    private val input = clientSocket.getInputStream().bufferedReader()
    private val output = clientSocket.getOutputStream()

    fun handle() {
        val lines = mutableListOf<String>()
        var line = input.readLine()
        while (line.isNotBlank()) {
            lines.add(line)
            line = input.readLine()
        }
        val request = parseRequest(lines)
        val response = handler(request)
        write(response)
    }

    private fun parseRequest(lines: List<String>): Request {
        val firstLine = lines[0].split(" ")
        val method = Method.valueOf(firstLine[0])
        val path = firstLine[1]
        val headers =
            lines.drop(1)
                .takeWhile { it.isNotBlank() }
                .map { it.split(": ") }
                .associate { it[0] to it[1] }
        return Request(method, path, headers, null)
    }

    private fun write(response: Response) {
        val builder =
            buildString {
                append("HTTP/1.1 ${response.status.code} ${response.status.message}\r\n")
                response.headers.forEach { (key, value) ->
                    append("$key: $value\r\n")
                }
                append("\r\n")
                response.body?.let { append(it) }
            }
        output.write(builder.toByteArray())
        output.flush()
    }
}
