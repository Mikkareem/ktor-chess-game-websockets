package dev.techullurgy.chess

import dev.techullurgy.chess.domain.Color
import dev.techullurgy.chess.domain.Player
import dev.techullurgy.chess.domain.RoomModel
import dev.techullurgy.chess.domain.toRoomModel
import dev.techullurgy.chess.requests.RoomJoinRequest
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
                    clientId = joinRequest.clientId,
                    roomId = roomId,
                )
            )

            call.respond(HttpStatusCode.Accepted)
        }

        post("/room/{roomId}/leave") {
            val roomId = call.parameters["roomId"]!!
            val room = gameServer.getRoomById(roomId)!!

            room.removePlayer(Player())
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
    }
}