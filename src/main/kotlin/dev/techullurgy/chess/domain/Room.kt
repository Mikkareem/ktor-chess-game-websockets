package dev.techullurgy.chess.domain

import dev.techullurgy.chess.events.*
import dev.techullurgy.chess.events.serializers.senderBaseEventJson
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.serialization.encodeToString
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration.Companion.seconds


class Room(
    val id: String,
    val name: String,
    val description: String,
    val createdBy: String
) {
    private val game = Game()

    private val players = ConcurrentHashMap<Color, Player>()

    private var timerJob: Job? = null

    fun getAssignedPlayers(): Set<Player> = players.values.toSet()

    suspend fun addPlayer(player: Player) {
        if (players.size in 0 until 2) {
            player.colorAssigned = if(players.isEmpty()) Color.White else Color.Black
            players.put(player.colorAssigned, player)
            if(player.colorAssigned == Color.White) {
                whitePlayerSend(GameLoading)
            } else {
                blackPlayerSend(GameLoading)
            }
        }
        if (players.size == 2) {
            whitePlayerSend(ColorAssigned(Color.White))
            blackPlayerSend(ColorAssigned(Color.Black))
            startGame()
        }
    }

    suspend fun removePlayer(player: Player) {
        players.remove(player.colorAssigned)
        broadcast(GameLoading)
    }

    fun runTimer() {
        timerJob?.cancel()
        timerJob = CoroutineScope(Dispatchers.Default).launch {
            val activePlayer = players.values.find { it.colorAssigned == game.currentPlayerColor } ?: return@launch
            val inactivePlayer = players.values.find { it.colorAssigned != game.currentPlayerColor } ?: return@launch

            while(isActive) {
                activePlayer.timeLeft -= 1.seconds
                val elapsedTime = ElapsedTime(
                    whiteTime = if(activePlayer.colorAssigned == Color.White) activePlayer.timeLeft.inWholeSeconds else inactivePlayer.timeLeft.inWholeSeconds,
                    blackTime = if(activePlayer.colorAssigned == Color.Black) activePlayer.timeLeft.inWholeSeconds else inactivePlayer.timeLeft.inWholeSeconds
                )
                broadcast(elapsedTime)
                delay(1000)
            }
        }
    }

    private suspend fun startGame() {
        runTimer()
        broadcast(GameStarted(boardString = game.boardString))
    }

    suspend fun cellSelectedForMove(data: MoveSelection) {
        if(data.color != game.currentPlayerColor) return

        val availableIndices = game.cellSelectedForMove(data.selectedIndex)
        sendToCurrentPlayer(SelectionResult(availableIndices, data.selectedIndex, data.color))
    }

    suspend fun cellDestinationSelectedForMove(data: DestinationSelected) {
        if(data.color != game.currentPlayerColor) return

        if(game.selectedIndexForMove == -1) return

        game.move(data.destinationIndex)

        val moveDone = MoveDone(
            by = game.currentPlayerColor,
            from = game.selectedIndexForMove,
            to = data.destinationIndex
        )
        broadcast(moveDone)

        val oppositeColor = if(game.currentPlayerColor == Color.White) Color.Black else Color.White
        if(game.checkForOppositeKingCheckMate(oppositeColor)) {
            val gameOver = GameOver(winner = game.currentPlayerColor)
            broadcast(gameOver)
            return
        }
        val kingCheckPosition = game.checkForOppositeKingInCheck(oppositeColor)

        game.changeTurnAndReset()
        val nextMove = NextMove(
            by = game.currentPlayerColor,
            previousMoveBy = moveDone.by,
            previousMoveFrom = moveDone.from,
            previousMoveTo = moveDone.to,
            oppositeKingInCheckIndex = kingCheckPosition
        )
        broadcast(nextMove)
        runTimer()
    }

    suspend fun resetSelection() {
        game.resetSelection()
        sendToCurrentPlayer(ResetSelectionDone)
    }

    private suspend fun broadcast(data: SenderBaseEvent) {
        val message = senderBaseEventJson.encodeToString<SenderBaseEvent>(data)
        players[Color.White]?.let {
            if(it.socket.isActive) {
                it.socket.send(message)
            }
        }
        players[Color.Black]?.let {
            if(it.socket.isActive) {
                it.socket.send(message)
            }
        }
    }

    private suspend fun sendToCurrentPlayer(data: SenderBaseEvent) {
        if(game.currentPlayerColor == Color.White) {
            whitePlayerSend(data)
        } else {
            blackPlayerSend(data)
        }
    }

    private suspend fun whitePlayerSend(data: SenderBaseEvent) {
        val message = senderBaseEventJson.encodeToString<SenderBaseEvent>(data)
        val player = players[Color.White]!!
        if(player.socket.isActive) {
            player.socket.send(message)
        }
    }

    private suspend fun blackPlayerSend(data: SenderBaseEvent) {
        val message = senderBaseEventJson.encodeToString<SenderBaseEvent>(data)
        val player = players[Color.Black]!!
        if(player.socket.isActive) {
            player.socket.send(message)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Room

        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}