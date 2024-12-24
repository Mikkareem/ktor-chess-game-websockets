package dev.techullurgy.chess

import dev.techullurgy.chess.domain.GameServer
import dev.techullurgy.chess.domain.Player
import dev.techullurgy.chess.domain.decodeBaseModel
import dev.techullurgy.chess.domain.getType
import dev.techullurgy.chess.events.*
import dev.techullurgy.chess.events.constants.BaseEventConstants
import dev.techullurgy.chess.sessions.GameSession
import io.ktor.serialization.kotlinx.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.serialization.json.Json

val gameServer = GameServer()

fun Application.configureSockets() {
    install(WebSockets) {
        contentConverter = KotlinxWebsocketSerializationConverter(Json)
    }

    routing {
        route("/join/ws") {
            standardWebsocket { socket, clientId, payload ->
                when(payload) {
                    is JoinRoomHandshake -> {
                        val newPlayer = Player(
                            name = payload.username,
                            socket = socket,
                            clientId = clientId
                        )
                        gameServer.joinRoom(newPlayer, payload.roomId)
                    }

                    is DestinationSelected -> {
                        val room = gameServer.getRoomForClientId(clientId) ?: return@standardWebsocket
                        room.cellDestinationSelectedForMove(payload)
                    }
                    Disconnected -> TODO()
                    is MoveSelection -> {
                        val room = gameServer.getRoomForClientId(clientId) ?: return@standardWebsocket
                        room.cellSelectedForMove(payload)
                    }

                    ResetSelection -> {
                        val room = gameServer.getRoomForClientId(clientId) ?: return@standardWebsocket
                        room.resetSelection()
                    }
                }
            }
        }
    }
}

fun Route.standardWebsocket(
    handleFrame: suspend (
        socket: DefaultWebSocketServerSession,
        clientId: String,
        payload: ReceiverBaseEvent
    ) -> Unit
) {
    webSocket {
        val session = call.sessions.get<GameSession>()
        if(session == null) {
            close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No Session found"))
            return@webSocket
        }

        try {
            for(frame in incoming) {
                if(frame is Frame.Text) {
                    val message = frame.readText()
                    val messageType = message.getType()

                    val payload = when(messageType) {
                        BaseEventConstants.TYPE_JOIN_ROOM_HANDSHAKE -> decodeBaseModel<JoinRoomHandshake>(message)
                        BaseEventConstants.TYPE_SELECTION_FOR_MOVE_DONE -> decodeBaseModel<MoveSelection>(message)
                        BaseEventConstants.TYPE_PIECE_DESTINATION_SELECTION_DONE -> decodeBaseModel<DestinationSelected>(message)
                        BaseEventConstants.TYPE_RESET_SELECTION -> decodeBaseModel<ResetSelection>(message)
                        BaseEventConstants.TYPE_DISCONNECT -> decodeBaseModel<Disconnected>(message)
                        else -> TODO()
                    }

                    handleFrame(this, session.clientId, payload)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            gameServer.disconnect(session.clientId)
        }
    }
}