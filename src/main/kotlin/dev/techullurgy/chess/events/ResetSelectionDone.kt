package dev.techullurgy.chess.events

import dev.techullurgy.chess.events.constants.BaseEventConstants
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName(BaseEventConstants.TYPE_RESET_SELECTION_DONE)
data object ResetSelectionDone: SenderBaseEvent