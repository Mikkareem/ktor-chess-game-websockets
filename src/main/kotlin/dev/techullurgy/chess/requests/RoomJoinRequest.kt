package dev.techullurgy.chess.requests

import dev.techullurgy.chess.domain.Color
import kotlinx.serialization.Serializable

@Serializable
data class RoomJoinRequest(
    val username: String,
    val color: Color
)
