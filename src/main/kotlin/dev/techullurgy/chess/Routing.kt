package dev.techullurgy.chess

import dev.techullurgy.chess.domain.RoomModel
import dev.techullurgy.chess.domain.toRoomModel
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        post("/create") {
            val model = call.receive<RoomModel>()
            val roomModel = gameServer.createRoom(model)
            call.respond(HttpStatusCode.Accepted, roomModel)
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