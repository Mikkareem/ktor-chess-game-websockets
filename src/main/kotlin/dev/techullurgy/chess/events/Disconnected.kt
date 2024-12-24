package dev.techullurgy.chess.events

import dev.techullurgy.chess.events.constants.BaseEventConstants
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName(BaseEventConstants.TYPE_DISCONNECT)
data object Disconnected: ReceiverBaseEvent