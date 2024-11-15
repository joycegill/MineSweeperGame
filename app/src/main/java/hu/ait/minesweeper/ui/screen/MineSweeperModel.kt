package hu.ait.minesweeper.ui.screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlin.random.Random

enum class GameMode {
    TRY_FIELD, PLACE_FLAG
}

data class Cell(
    val isMine: Boolean = false,
    var isFlagged: Boolean = false,
    var isRevealed: Boolean = false,
    var adjacentMines: Int = 0
)

class MineSweeperModel : ViewModel() {
    var board by mutableStateOf(
        Array(5) { Array(5) { Cell() } }
    )

    var gameMode by mutableStateOf(GameMode.TRY_FIELD)
    var gameOver by mutableStateOf(false)
    var win by mutableStateOf(false)

    init {
        resetGame()
    }

    private fun placeMines() {
        var minesPlaced = 0
        val totalMines = 3
        while (minesPlaced < totalMines) {
            val row = Random.nextInt(0, 5)
            val col = Random.nextInt(0, 5)
            if (!board[row][col].isMine) {
                board[row][col] = board[row][col].copy(isMine = true)
                minesPlaced++
            }
            board = board.copyOf()
        }
    }

    private fun calculateAdjacentMines() {
        for (row in 0 until 5) {
            for (col in 0 until 5) {
                if (!board[row][col].isMine) {
                    board[row][col].adjacentMines = countAdjacentMines(row, col)
                }
            }
        }
    }

    private fun countAdjacentMines(row: Int, col: Int): Int {
        var count = 0
        for (i in maxOf(0, row - 1)..minOf(4, row + 1)) {
            for (j in maxOf(0, col - 1)..minOf(4, col + 1)) {
                if (board[i][j].isMine) {
                    count++
                }
            }
        }
        return count
    }

    fun onCellClicked(row: Int, col: Int) {
        if (gameOver || board[row][col].isRevealed) return

        when (gameMode) {
            GameMode.TRY_FIELD -> {
                tryField(row, col)
                checkWin()
            }
            GameMode.PLACE_FLAG -> {
                placeFlag(row, col)
            }
        }
    }

    private fun tryField(row: Int, col: Int) {
        if (board[row][col].isMine) {
            gameOver = true
            revealAllMines()
        } else {
            revealCell(row, col)
        }
        board = board.copyOf()
    }

    private fun placeFlag(row: Int, col: Int) {
        if (!board[row][col].isRevealed) {
            board[row][col] = board[row][col].copy(isFlagged = !board[row][col].isFlagged)
        }
        board = board.copyOf()
    }

    private fun revealCell(row: Int, col: Int) {
        if (board[row][col].isRevealed) return

        board[row][col] = board[row][col].copy(isRevealed = true)

        if (board[row][col].adjacentMines == 0) {
            for (i in maxOf(0, row - 1)..minOf(4, row + 1)) {
                for (j in maxOf(0, col - 1)..minOf(4, col + 1)) {
                    if (i != row || j != col) {
                        revealCell(i, j)
                    }
                }
            }
        }
        board = board.copyOf()
    }

    private fun revealAllMines() {
        for (row in 0 until 5) {
            for (col in 0 until 5) {
                if (board[row][col].isMine) {
                    board[row][col] = board[row][col].copy(isRevealed = true)
                }
            }
        }
    }

    private fun checkWin() {
        if (board.all { row ->
                row.all { cell ->
                    (cell.isRevealed && cell.adjacentMines == 0) ||
                            (cell.isRevealed && cell.adjacentMines > 0) ||
                            (cell.isMine && (cell.isFlagged || !cell.isRevealed))
                }
            }) {
            win = true
            gameOver = true
        }
    }

    fun resetGame() {
        board = Array(5) { Array(5) { Cell() } }
        gameOver = false
        win = false
        placeMines()
        calculateAdjacentMines()
    }
}
