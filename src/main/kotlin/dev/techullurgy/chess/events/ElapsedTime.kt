package dev.techullurgy.chess.events

import dev.techullurgy.chess.events.constants.BaseEventConstants
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName(BaseEventConstants.TYPE_ELAPSED_TIME)
data class ElapsedTime(
    val whiteTime: Long,
    val blackTime: Long,
): SenderBaseEvent