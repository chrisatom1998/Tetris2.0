package com.example.tetrisv2

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.random.Random

class GameView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val cols = 10
    private val rows = 20
    private val cellSize: Float
        get() = width / cols.toFloat()

    private val board = Array(rows) { IntArray(cols) }

    private val paint = Paint()

    private var current: Tetromino = Tetromino.random()
    private var curRow = 0
    private var curCol = cols / 2 - 1
    private var rotation = 0

    init {
        postDelayed({ tick() }, 500)
    }

    fun moveLeft() {
        if (canMove(curRow, curCol - 1, rotation)) {
            curCol--
            invalidate()
        }
    }

    fun moveRight() {
        if (canMove(curRow, curCol + 1, rotation)) {
            curCol++
            invalidate()
        }
    }

    fun rotate() {
        val newRot = (rotation + 1) % current.shapes.size
        if (canMove(curRow, curCol, newRot)) {
            rotation = newRot
            invalidate()
        }
    }

    fun drop() {
        while (canMove(curRow + 1, curCol, rotation)) {
            curRow++
        }
        tick()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawBoard(canvas)
        drawCurrent(canvas)
    }

    private fun drawBoard(canvas: Canvas) {
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                if (board[r][c] != 0) {
                    paint.color = board[r][c]
                    canvas.drawRect(
                        c * cellSize,
                        r * cellSize,
                        (c + 1) * cellSize,
                        (r + 1) * cellSize,
                        paint
                    )
                }
            }
        }
    }

    private fun drawCurrent(canvas: Canvas) {
        paint.color = current.color
        for (p in current.shapes[rotation]) {
            val x = curCol + p.first
            val y = curRow + p.second
            canvas.drawRect(
                x * cellSize,
                y * cellSize,
                (x + 1) * cellSize,
                (y + 1) * cellSize,
                paint
            )
        }
    }

    private fun tick() {
        if (canMove(curRow + 1, curCol, rotation)) {
            curRow++
        } else {
            placePiece()
            clearLines()
            spawnPiece()
        }
        invalidate()
        postDelayed({ tick() }, 500)
    }

    private fun canMove(r: Int, c: Int, rot: Int): Boolean {
        for (p in current.shapes[rot]) {
            val x = c + p.first
            val y = r + p.second
            if (x !in 0 until cols || y !in 0 until rows) return false
            if (board[y][x] != 0) return false
        }
        return true
    }

    private fun placePiece() {
        for (p in current.shapes[rotation]) {
            val x = curCol + p.first
            val y = curRow + p.second
            if (y in 0 until rows && x in 0 until cols)
                board[y][x] = current.color
        }
    }

    private fun clearLines() {
        val newBoard = Array(rows) { IntArray(cols) }
        var newRow = rows - 1
        for (r in rows - 1 downTo 0) {
            if (board[r].any { it == 0 }) {
                newBoard[newRow] = board[r]
                newRow--
            }
        }
        for (r in newRow downTo 0) {
            newBoard[r] = IntArray(cols)
        }
        for (r in 0 until rows) board[r] = newBoard[r]
    }

    private fun spawnPiece() {
        current = Tetromino.random()
        curRow = 0
        curCol = cols / 2 - 1
        rotation = 0
        if (!canMove(curRow, curCol, rotation)) {
            // game over
            for (r in 0 until rows) for (c in 0 until cols) board[r][c] = 0
        }
    }
}

private enum class Tetromino(val shapes: Array<Array<Pair<Int, Int>>>, val color: Int) {
    I(
        arrayOf(
            arrayOf(0 to 1, 1 to 1, 2 to 1, 3 to 1),
            arrayOf(2 to 0, 2 to 1, 2 to 2, 2 to 3)
        ), Color.CYAN
    ),
    J(
        arrayOf(
            arrayOf(0 to 0, 0 to 1, 1 to 1, 2 to 1),
            arrayOf(1 to 0, 2 to 0, 1 to 1, 1 to 2),
            arrayOf(0 to 1, 1 to 1, 2 to 1, 2 to 2),
            arrayOf(1 to 0, 1 to 1, 0 to 2, 1 to 2)
        ), Color.BLUE
    ),
    L(
        arrayOf(
            arrayOf(2 to 0, 0 to 1, 1 to 1, 2 to 1),
            arrayOf(1 to 0, 1 to 1, 1 to 2, 2 to 2),
            arrayOf(0 to 1, 1 to 1, 2 to 1, 0 to 2),
            arrayOf(0 to 0, 1 to 0, 1 to 1, 1 to 2)
        ), Color.rgb(255,165,0)
    ),
    O(
        arrayOf(
            arrayOf(1 to 0, 2 to 0, 1 to 1, 2 to 1)
        ), Color.YELLOW
    ),
    S(
        arrayOf(
            arrayOf(1 to 0, 2 to 0, 0 to 1, 1 to 1),
            arrayOf(1 to 0, 1 to 1, 2 to 1, 2 to 2)
        ), Color.GREEN
    ),
    T(
        arrayOf(
            arrayOf(1 to 0, 0 to 1, 1 to 1, 2 to 1),
            arrayOf(1 to 0, 1 to 1, 2 to 1, 1 to 2),
            arrayOf(0 to 1, 1 to 1, 2 to 1, 1 to 2),
            arrayOf(1 to 0, 0 to 1, 1 to 1, 1 to 2)
        ), Color.MAGENTA
    ),
    Z(
        arrayOf(
            arrayOf(0 to 0, 1 to 0, 1 to 1, 2 to 1),
            arrayOf(2 to 0, 1 to 1, 2 to 1, 1 to 2)
        ), Color.RED
    );

    companion object {
        fun random(): Tetromino {
            val values = entries.toTypedArray()
            return values[Random.nextInt(values.size)]
        }
    }
}
