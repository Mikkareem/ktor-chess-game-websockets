package dev.techullurgy.chess.domain

class Game {
    private val board: MutableList<Piece?> = List(8 * 8) {
        when(it.row) {
            0 -> {
                when(it.column) {
                    0,7 -> Rook(it, Color.White)
                    1,6 -> Knight(it, Color.White)
                    2,5 -> Bishop(it, Color.White)
                    3 -> Queen(it, Color.White)
                    4 -> King(it, Color.White)
                    else -> null
                }
            }
            1 -> {
                Pawn(it, Color.White)
            }
            6 -> {
                Pawn(it, Color.Black)
            }
            7 -> {
                when(it.column) {
                    0,7 -> Rook(it, Color.Black)
                    1,6 -> Knight(it, Color.Black)
                    2,5 -> Bishop(it, Color.Black)
                    3 -> King(it, Color.Black)
                    4 -> Queen(it, Color.Black)
                    else -> null
                }
            }
            else -> null
        }
    }.toMutableList()

    var currentPlayerColor: Color = Color.White
        private set

    var selectedIndexForMove: Int = -1
        private set

    val boardString: String
        get() = board.joinToString("***") { piece ->
            when (piece) {
                is Bishop -> if (piece.color == Color.Black) "BB" else "WB"
                is King -> if (piece.color == Color.Black) "BK" else "WK"
                is Knight -> if (piece.color == Color.Black) "BN" else "WN"
                is Pawn -> if (piece.color == Color.Black) "BP" else "WP"
                is Queen -> if (piece.color == Color.Black) "BQ" else "WQ"
                is Rook -> if (piece.color == Color.Black) "BR" else "WR"
                null -> "##"
            }
        }

    fun move(to: Int) {
        val newPiece = when(val currentPiece = board[selectedIndexForMove]!!) {
            is Bishop -> currentPiece.copy(index = to)
            is King -> currentPiece.copy(index = to)
            is Knight -> currentPiece.copy(index = to)
            is Pawn -> currentPiece.copy(index = to, isFirstMoveDone = true)
            is Queen -> currentPiece.copy(index = to)
            is Rook -> currentPiece.copy(index = to)
        }

        board[selectedIndexForMove] = null
        board[to] = newPiece
    }

    fun cellSelectedForMove(index: Int): List<Int> {
        selectedIndexForMove = index
        return board[selectedIndexForMove]!!.getAvailableIndices(board)
    }

    fun resetSelection() {
        selectedIndexForMove = -1
    }

    fun checkForOppositeKingInCheck(oppositeColor: Color): Int {
        val oppositeKingPosition = board.filterIsInstance<King>().first { it.color == oppositeColor }.index

        board.filterNotNull().filter { it.pieceColor != oppositeColor }
            .forEach {
                if(it.getAvailableIndices(board).contains(oppositeKingPosition)) return oppositeKingPosition
            }

        return -1
    }

    fun checkForOppositeKingCheckMate(oppositeColor: Color): Boolean {
        board.filterNotNull().filter { it.pieceColor == oppositeColor }
            .forEach {
                if(it.getAvailableIndices(board).isNotEmpty()) return false
            }

        return true
    }

    fun changeTurnAndReset() {
        currentPlayerColor = if (currentPlayerColor == Color.White) Color.Black else Color.White
        selectedIndexForMove = -1
    }
}