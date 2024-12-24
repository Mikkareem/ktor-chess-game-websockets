package dev.techullurgy.chess.domain

import java.util.*
import java.util.concurrent.ConcurrentHashMap

class GameServer {
    private val rooms = ConcurrentHashMap<String, Room>().apply {
        put("3rec", Room("3rec", "test room", "room desc", "Tester"))
    }

    fun createRoom(model: RoomModel): RoomModel {
        val roomId = UUID.randomUUID().toString()
        val room = Room(roomId, model.name, model.description, model.createdBy)
        rooms.put(roomId, room)
        println("New room Created: Id=${roomId}, Total Rooms=${rooms.size}")
        return room.toRoomModel()
    }

    suspend fun joinRoom(player: Player, roomId: String): Room? {
        val room = rooms[roomId] ?: return null
        room.addPlayer(player)
        return room
    }

    suspend fun disconnect(clientId: String): Boolean {
        val room = getRoomForClientId(clientId) ?: return false
        val player = room.getAssignedPlayers().find { player -> player.clientId == clientId } ?: return false
        room.removePlayer(player)
        return true
    }

    fun getRoomForClientId(clientId: String): Room? {
        val room = rooms.values.find { room -> room.getAssignedPlayers().firstOrNull { it.clientId == clientId } != null }
        return room
    }

    internal fun getRoomById(roomId: String): Room? = rooms[roomId]
}