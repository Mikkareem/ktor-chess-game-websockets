package dev.techullurgy.chess.sessions

import kotlinx.serialization.Serializable

@Serializable
data class GameSession(
    val clientId: String,
    val sessionId: String,
)