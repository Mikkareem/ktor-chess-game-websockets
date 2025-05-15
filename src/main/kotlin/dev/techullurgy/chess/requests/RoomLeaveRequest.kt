package dev.techullurgy.chess.requests

import kotlinx.serialization.Serializable

@Serializable
data class RoomLeaveRequest(
    val clientId: String
)
