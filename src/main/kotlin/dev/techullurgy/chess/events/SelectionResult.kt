package dev.techullurgy.chess.events

import dev.techullurgy.chess.domain.Color
import dev.techullurgy.chess.events.constants.BaseEventConstants
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName(BaseEventConstants.TYPE_SELECTION_RESULT)
data class SelectionResult(
    val availableIndices: List<Int>,
    val selectedIndex: Int,
    val color: Color
): SenderBaseEvent