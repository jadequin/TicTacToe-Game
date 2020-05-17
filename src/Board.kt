import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min

/*
    - Board will be displayed as an 1D array
    - possible position states:
         0 = empty  ' '
        -1 = Player 'O'
         1 = Player 'X'
    - both turns will be represented by +/-1
 */

class Board(val board: List<Int> = listOf(0,0,0,0,0,0,0,0,0),
            val turn: Int = +1
) {

    //Returns: List with all possible moves as indices of the board.
    //The moves will also be shuffled for a higher random factor in further usages

    fun makeMove(pos: Int) = Board(board = board.mapIndexed { index, i -> if(index == pos) turn else i}, turn = -turn)

    fun makeRandomMove() = possibleMoves().random()

    fun makeBestMove(): Board {
        val moveResults = mutableListOf<Pair<Board, Int>>()

        for(move in possibleMoves()) {
            if(move.result() == turn)
                return move

            val result = minimax(move)
            moveResults.add(Pair(move, result))
        }

        //filter only the 'good' results
        moveResults.filter { it.second == turn }

        return if(moveResults.isNotEmpty())
            moveResults[0].first
        else
            makeRandomMove()
            //no winning option anymore, pick a random move
    }


    fun isGameOver() = !threeInARow().isNullOrEmpty() || possibleMoves().isEmpty()
    fun playerXTurn() = turn == 1 //method for game representation

    //Returns 1 if playerX won, -1 if playerY won and 0 if it's a tie or the game is not over
    fun result(): Int = if(threeInARow().isNullOrEmpty()) 0 else -turn

    private fun possibleMoves() = board.mapIndexed { index, i -> if(i == 0) makeMove(index) else null }.filterNotNull().shuffled()

    //Returns: List with the winning row if there is one otherwise null
    private fun threeInARow(): List<Int>? {
        val rows = listOf(listOf(0,1,2), listOf(3,4,5), listOf(6,7,8), listOf(0,3,6), listOf(1,4,7), listOf(2,5,8), listOf(0,4,8), listOf(2,4,6))
        rows.forEach {
            if(it.sumBy { i -> board.elementAt(i)}.absoluteValue == 3)
                return it
        }
        return null
    }

    //TODO: alpha-beta-pruning
    private fun minimax(board: Board): Int {

        if(board.isGameOver())
            return result()

        var bestEval = if(board.turn == 1) Int.MIN_VALUE else Int.MAX_VALUE
        for(move in board.possibleMoves()) {
            val eval = minimax(move)
            bestEval = if(board.turn == 1) max(eval, bestEval) else min(eval, bestEval)
        }
        return bestEval
    }

    override fun toString(): String {
        var cnt = 1
        return board.joinToString(prefix = "-------\n|", postfix = "-------", separator = "|",
                transform = {
                    (if(it == -1) "O" else if(it == 1) "X" else " ") +
                            (if(cnt++ % 3 == 0) "|\n" else "")
                }//new line with every 3rd position
        )
    }
}