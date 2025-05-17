package dev.techullurgy.chess

import dev.techullurgy.chess.domain.Color
import dev.techullurgy.chess.domain.Player
import dev.techullurgy.chess.domain.RoomModel
import dev.techullurgy.chess.domain.toRoomModel
import dev.techullurgy.chess.requests.RoomJoinRequest
import dev.techullurgy.chess.requests.RoomLeaveRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        post("/room/create") {
            val model = call.receive<RoomModel>()
            val roomModel = gameServer.createRoom(model)
            call.respond(HttpStatusCode.Accepted, roomModel)
        }

        post("/room/{roomId}/join") {
            val roomId = call.parameters["roomId"]!!
            val clientId = call.parameters["client_id"]!!
            val room = gameServer.getRoomById(roomId)!!

            val assignedPlayers = room.getAssignedPlayers()

            if(assignedPlayers.size >= 2) {
                call.respond(HttpStatusCode.Conflict, "Room is full")
                return@post
            }

            val joinRequest = call.receive<RoomJoinRequest>()

            val assignableColor = assignedPlayers
                .takeIf { it.size == 1 }
                ?.let {
                    if(it.first().colorAssigned == Color.Black) Color.White else Color.Black
                }


            room.addPlayer(
                Player(
                    name = joinRequest.username,
                    colorAssigned = assignableColor ?: joinRequest.color,
                    clientId = clientId,
                    roomId = roomId,
                )
            )

            call.respond(HttpStatusCode.Accepted)
        }

        post("/room/{roomId}/leave") {
            val roomId = call.parameters["roomId"]!!
            val room = gameServer.getRoomById(roomId)!!

            val request = call.receive<RoomLeaveRequest>()

            room.removePlayer(request.clientId)
        }

        get("/room/{roomId}") {
            val roomId = call.parameters["roomId"]!!
            val room = gameServer.getRoomById(roomId)

            room?.let {
                call.respond(HttpStatusCode.OK, it.toRoomModel())
            } ?: let {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        get("/rooms/{clientId}") {
            val clientId = call.parameters["clientId"]!!
            val associatedRooms = gameServer.getRoomsForClientId(clientId).map { it.toRoomModel() }

            call.respond(HttpStatusCode.OK, associatedRooms)
        }

        post("room/{roomId}/start") {
            val roomId = call.parameters["roomId"]!!
            val room = gameServer.getRoomById(roomId)!!

            room.startGame()
        }
    }
}