package com.urielsalis.http

object HttpServer {
    val getHandlers = mapOf(
        "/".toRegex() to ::handleRoot
    )

    fun handle(request: Request): Response {
        val response = when (request.method) {
            Method.GET -> getHandler(request, getHandlers)
            else -> null
        }
        if (response != null) {
            return response
        }
        return Response(StatusCode.NOT_FOUND)
    }

    private fun getHandler(
        request: Request, handlers: Map<Regex, (Request) -> Response>
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
}
