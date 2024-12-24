package dev.techullurgy.chess.events

import dev.techullurgy.chess.domain.Color
import dev.techullurgy.chess.events.constants.BaseEventConstants
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName(BaseEventConstants.TYPE_PIECE_DESTINATION_SELECTION_DONE)
data class DestinationSelected(
    val color: Color,
    val destinationIndex: Int
): ReceiverBaseEvent
