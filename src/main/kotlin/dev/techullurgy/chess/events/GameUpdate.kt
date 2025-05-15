package dev.techullurgy.chess.events

import dev.techullurgy.chess.domain.Color
import dev.techullurgy.chess.events.constants.BaseEventConstants
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName(BaseEventConstants.TYPE_GAME_UPDATE)
data class GameUpdate(
    val board: String,
    val currentTurn: Color,
    val lastMove: String,
    val cutPieces: String? = null,
    val kingInCheckIndex: Int? = null
): SenderBaseEvent
