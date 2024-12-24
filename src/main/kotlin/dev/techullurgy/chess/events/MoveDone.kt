package dev.techullurgy.chess.events

import dev.techullurgy.chess.domain.Color
import dev.techullurgy.chess.events.constants.BaseEventConstants
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName(BaseEventConstants.TYPE_MOVE_DONE)
data class MoveDone (
    val by: Color,
    val from: Int,
    val to: Int,
): SenderBaseEvent