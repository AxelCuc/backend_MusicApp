package com.musica.models

import kotlinx.serialization.Serializable

// Modelos de respuesta HTTP estándar
@Serializable
data class ApiResponse<T>(
    val data: T? = null,
    val status: Int,
    val message: String
)

@Serializable
data class ApiError(
    val error: String,
    val message: String,
    val status: Int,
    val timestamp: Long = System.currentTimeMillis()
)

// Modelo para respuestas de éxito simple
@Serializable
data class SuccessResponse(
    val message: String,
    val id: String? = null
)