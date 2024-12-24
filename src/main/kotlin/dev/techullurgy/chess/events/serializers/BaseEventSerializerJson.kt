package dev.techullurgy.chess.events.serializers

import dev.techullurgy.chess.events.ColorAssigned
import dev.techullurgy.chess.events.DestinationSelected
import dev.techullurgy.chess.events.Disconnected
import dev.techullurgy.chess.events.ElapsedTime
import dev.techullurgy.chess.events.GameLoading
import dev.techullurgy.chess.events.GameOver
import dev.techullurgy.chess.events.GameStarted
import dev.techullurgy.chess.events.JoinRoomHandshake
import dev.techullurgy.chess.events.MoveDone
import dev.techullurgy.chess.events.MoveSelection
import dev.techullurgy.chess.events.NextMove
import dev.techullurgy.chess.events.ReceiverBaseEvent
import dev.techullurgy.chess.events.ResetSelection
import dev.techullurgy.chess.events.ResetSelectionDone
import dev.techullurgy.chess.events.SelectionResult
import dev.techullurgy.chess.events.SenderBaseEvent
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule

val receiverBaseEventJson = Json {
    serializersModule = SerializersModule {
        polymorphic(ReceiverBaseEvent::class, JoinRoomHandshake::class, JoinRoomHandshake.serializer())
        polymorphic(ReceiverBaseEvent::class, MoveSelection::class, MoveSelection.serializer())
        polymorphic(ReceiverBaseEvent::class, DestinationSelected::class, DestinationSelected.serializer())
        polymorphic(ReceiverBaseEvent::class, ResetSelection::class, ResetSelection.serializer())
        polymorphic(ReceiverBaseEvent::class, Disconnected::class, Disconnected.serializer())
    }
}

val senderBaseEventJson = Json {
    serializersModule = SerializersModule {
        polymorphic(SenderBaseEvent::class, MoveDone::class, MoveDone.serializer())
        polymorphic(SenderBaseEvent::class, ColorAssigned::class, ColorAssigned.serializer())
        polymorphic(SenderBaseEvent::class, ElapsedTime::class, ElapsedTime.serializer())
        polymorphic(SenderBaseEvent::class, GameLoading::class, GameLoading.serializer())
        polymorphic(SenderBaseEvent::class, GameOver::class, GameOver.serializer())
        polymorphic(SenderBaseEvent::class, GameStarted::class, GameStarted.serializer())
        polymorphic(SenderBaseEvent::class, NextMove::class, NextMove.serializer())
        polymorphic(SenderBaseEvent::class, SelectionResult::class, SelectionResult.serializer())
        polymorphic(SenderBaseEvent::class, ResetSelectionDone::class, ResetSelectionDone.serializer())
    }
}