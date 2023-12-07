package com.urielsalis.http

data class Request(
    val method: Method,
    val path: String,
    val headers: Map<String, String>,
    val body: String?,
)

data class Response(
    val status: StatusCode,
    val headers: Map<String, String> = mapOf(),
    val body: String? = null,
)

enum class Method {
    GET,
    POST,
    PUT,
    DELETE,
}

enum class StatusCode(val code: Int, val message: String) {
    OK(200, "OK"),
    CREATED(201, "Created"),
    NOT_FOUND(404, "Not Found"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
}
