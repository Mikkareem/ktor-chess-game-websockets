package dev.techullurgy.chess.events

import dev.techullurgy.chess.domain.Color
import dev.techullurgy.chess.events.constants.BaseEventConstants
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName(BaseEventConstants.TYPE_NEXT_MOVE)
data class NextMove(
    val by: Color,
    val previousMoveBy: Color,
    val previousMoveFrom: Int,
    val previousMoveTo: Int
): SenderBaseEvent