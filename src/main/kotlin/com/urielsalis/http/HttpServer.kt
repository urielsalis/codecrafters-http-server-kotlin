package com.urielsalis.http

import java.io.File

class HttpServer(val directory: String?) {
    val getHandlers =
        mapOf(
            "/".toRegex() to ::handleRoot,
            "/echo/.*".toRegex() to ::handleEcho,
            "/user-agent".toRegex() to ::handleUserAgent,
            "/files/.*".toRegex() to ::handleGetFiles,
        )
    val postHandlers =
        mapOf(
            "/files/.*".toRegex() to ::handlePostFiles,
        )

    fun handle(request: Request): Response {
        val response =
            when (request.method) {
                Method.GET -> getHandler(request, getHandlers)
                Method.POST -> getHandler(request, postHandlers)
                else -> null
            }
        if (response != null) {
            return response
        }
        return Response(StatusCode.NOT_FOUND)
    }

    private fun getHandler(
        request: Request,
        handlers: Map<Regex, (Request) -> Response>,
    ) = handlers.firstNotNullOfOrNull {
        if (it.key.matches(request.path)) {
            it.value(request)
        } else {
            null
        }
    }

    private fun handleRoot(request: Request): Response {
        return Response(StatusCode.OK)
    }

    private fun handleEcho(request: Request): Response {
        val path = request.path.removePrefix("/echo/")
        return Response(
            StatusCode.OK,
            mapOf("Content-Type" to "text/plain", "Content-Length" to path.length.toString()),
            body = path,
        )
    }

    private fun handleUserAgent(request: Request): Response {
        val agent = request.headers["User-Agent"] ?: return Response(StatusCode.BAD_REQUEST)
        return Response(
            StatusCode.OK,
            mapOf("Content-Type" to "text/plain", "Content-Length" to agent.length.toString()),
            body = agent,
        )
    }

    private fun handleGetFiles(request: Request): Response {
        val path = request.path.removePrefix("/files/")
        val file = File(directory, path)
        if (!file.exists()) {
            return Response(StatusCode.NOT_FOUND)
        }
        val body = file.readText()
        return Response(
            StatusCode.OK,
            mapOf(
                "Content-Type" to "application/octet-stream",
                "Content-Length" to body.length.toString(),
            ),
            body = body,
        )
    }

    private fun handlePostFiles(request: Request): Response {
        val path = request.path.removePrefix("/files/")
        val file = File(directory, path)
        file.createNewFile()
        file.writeText(request.body ?: "")
        return Response(StatusCode.CREATED)
    }
}
