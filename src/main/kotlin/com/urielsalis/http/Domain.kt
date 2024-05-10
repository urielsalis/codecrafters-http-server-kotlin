package com.urielsalis.http

data class Request(
    val method: Method,
    val path: String,
    val headers: Map<String, String>,
    val body: String?,
) {
    fun isGzipAllowed() = headers.any {
        it.key.lowercase() == "accept-encoding" && it.value.split(",").any { it.trim() == "gzip" }
    }
}

data class Response(
    val status: StatusCode,
    val headers: Map<String, String> = mapOf("Content-Length" to "0"),
    val body: String? = null,
)

enum class Method {
    GET, POST, PUT, DELETE,
}

enum class StatusCode(val code: Int, val message: String) {
    OK(200, "OK"), CREATED(201, "Created"), BAD_REQUEST(400, "Bad Request"), NOT_FOUND(
        404, "Not Found"
    ),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
}
