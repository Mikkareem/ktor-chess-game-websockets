package dev.techullurgy.chess.domain

import io.ktor.server.websocket.DefaultWebSocketServerSession
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

data class Player(
    val name: String,
    val socket: DefaultWebSocketServerSession,
    val clientId: String,
) {
    lateinit var colorAssigned: Color
        internal set
    var timeLeft: Duration = 30.minutes
}