package com.urielsalis.http

import java.io.ByteArrayOutputStream
import java.net.Socket
import java.util.zip.GZIPOutputStream

class ConnectionHandler(clientSocket: Socket, val handler: (Request) -> Response) {
    private val input = clientSocket.getInputStream().bufferedReader()
    private val output = clientSocket.getOutputStream()

    fun handle() {
        val lines = mutableListOf<String>()
        var line = input.readLine()
        var body: String? = null
        while (line.isNotBlank()) {
            lines.add(line)
            line = input.readLine()
        }
        val contentLength = lines.firstOrNull { it.startsWith("Content-Length: ") }
        if (contentLength != null) {
            val length = contentLength.removePrefix("Content-Length: ").toInt()
            val bodyRaw = CharArray(length)
            input.read(bodyRaw)
            body = String(bodyRaw)
        }
        val request = parseRequest(lines, body)
        val response = handler(request)
        if (request.isGzipAllowed()) {
            return write(response.gzipCompress())
        }
        write(response)
    }

    private fun Response.gzipCompress(): Response {
        val compressedBody = body.gzipCompress()
        val newHeaders =
            this.headers.filterNot { it.key.lowercase() == "content-length" }
                .plus("Content-Encoding" to "gzip")
                .plus("Content-Length" to compressedBody.size.toString())
        return this.copy(headers = newHeaders, body = compressedBody)
    }

    private fun parseRequest(
        lines: List<String>,
        body: String?,
    ): Request {
        val firstLine = lines[0].split(" ")
        val method = Method.valueOf(firstLine[0])
        val path = firstLine[1]
        val headers =
            lines.drop(1).takeWhile { it.isNotBlank() }.map { it.split(": ") }
                .associate { it[0] to it[1] }
        return Request(method, path, headers, body)
    }

    private fun write(response: Response) {
        val builder =
            buildString {
                append("HTTP/1.1 ${response.status.code} ${response.status.message}\r\n")
                response.headers.forEach { (key, value) ->
                    append("$key: $value\r\n")
                }
                append("\r\n")
            }
        output.write(builder.toByteArray())
        if (response.body.isNotEmpty()) {
            output.write(response.body)
        }
        output.flush()
    }
}

private fun ByteArray.gzipCompress(): ByteArray {
    val bos = ByteArrayOutputStream()
    val gzip = GZIPOutputStream(bos)
    gzip.write(this)
    gzip.finish()
    return bos.toByteArray()
}
