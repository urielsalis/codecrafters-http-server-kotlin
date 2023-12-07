package com.urielsalis.http

object HttpServer {
    val getHandlers =
        mapOf(
            "/".toRegex() to ::handleRoot,
            "/echo/.*".toRegex() to ::handleEcho,
            "/user-agent".toRegex() to ::handleUserAgent,
        )

    fun handle(request: Request): Response {
        val response =
            when (request.method) {
                Method.GET -> getHandler(request, getHandlers)
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
}
