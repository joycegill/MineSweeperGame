package hu.ait.minesweeper.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import hu.ait.minesweeper.R
import androidx.compose.ui.text.font.FontWeight

@Composable
fun MineSweeperScreen(
    modifier: Modifier,
    viewModel: MineSweeperModel = viewModel()
) {
    val board = viewModel.board
    val gameOver = viewModel.gameOver
    val win = viewModel.win
    var isFlagMode by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.minesweeper_game),
            fontSize = 24.sp,
            modifier = Modifier.padding(16.dp),
            color = Color.Black,
            fontWeight = FontWeight.Bold
        )
        Row(
            modifier = Modifier.padding(10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = if (isFlagMode) stringResource(R.string.place_flag_mode) else stringResource(
                R.string.try_field_mode
            ))
            Checkbox(
                modifier = Modifier.padding(8.dp),
                checked = isFlagMode,
                onCheckedChange = {
                    isFlagMode = it
                    viewModel.gameMode = if (it) GameMode.PLACE_FLAG else GameMode.TRY_FIELD
                }
            )
        }
        Box(
            modifier = Modifier.padding(16.dp)
        ) {
            MineSweeperBoard(
                board = board,
                gameOver = gameOver,
                onCellClick = { row, col ->
                    viewModel.onCellClicked(row, col)
                }
            )
        }
        Button(onClick = {
            viewModel.resetGame()
        }) {
            Text(stringResource(R.string.reset_game))
        }
        if (gameOver) {
            Text(
                text = if (win) stringResource(R.string.you_win) else stringResource(R.string.game_over_try_again),
                modifier = Modifier.padding(16.dp),
                color = if (win) Color.Green else Color.Red
            )
        }
    }
}

@Composable
fun MineSweeperBoard(
    board: Array<Array<Cell>>,
    gameOver: Boolean,
    onCellClick: (Int, Int) -> Unit
) {
    val gridSize = 5

    Column {
        for (row in 0 until gridSize) {
            Row {
                for (col in 0 until gridSize) {
                    val cell = board[row][col]

                    MineSweeperCell(
                        cell = cell,
                        gameOver = gameOver,
                        onClick = { onCellClick(row, col) }
                    )
                }
            }
        }
    }
}

@Composable
fun MineSweeperCell(
    cell: Cell,
    gameOver: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        cell.isRevealed && cell.adjacentMines == 0 -> Color.White
        else -> Color.LightGray
    }
    Box(
        modifier = Modifier
            .padding(1.dp)
            .size(40.dp)
            .border(2.dp, Color.Gray)
            .background(backgroundColor)
            .pointerInput(Unit) {
                detectTapGestures {
                    onClick()
                }
            },
        contentAlignment = Alignment.Center
    ) {
        when {
            gameOver && cell.isMine -> {
                Image(
                    painter = painterResource(id = R.drawable.mine),
                    contentDescription = stringResource(R.string.mine),
                    modifier = Modifier.size(30.dp)
                )
            }
            cell.isFlagged -> {
                Image(
                    painter = painterResource(id = R.drawable.flag),
                    contentDescription = stringResource(R.string.flag),
                    modifier = Modifier.size(30.dp)
                )
            }
            cell.isRevealed -> {
                if (cell.isMine) {
                    Image(
                        painter = painterResource(id = R.drawable.mine),
                        contentDescription = stringResource(R.string.mine),
                        modifier = Modifier.size(30.dp)
                    )
                } else {
                    Text(
                        text = if (cell.adjacentMines > 0) cell.adjacentMines.toString() else "",
                        color = when (cell.adjacentMines) {
                            1 -> Color.Blue
                            2 -> Color.Green
                            3 -> Color.Red
                            else -> Color.Black
                        },
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            else -> {
                Box(
                    modifier = Modifier
                        .background(Color.LightGray)
                        .fillMaxSize()
                )
            }
        }
    }
}