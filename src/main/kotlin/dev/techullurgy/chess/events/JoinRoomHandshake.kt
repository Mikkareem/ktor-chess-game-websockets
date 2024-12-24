package dev.techullurgy.chess.events

import dev.techullurgy.chess.events.constants.BaseEventConstants
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName(BaseEventConstants.TYPE_JOIN_ROOM_HANDSHAKE)
data class JoinRoomHandshake(
    val username: String,
    val roomId: String
): ReceiverBaseEvent