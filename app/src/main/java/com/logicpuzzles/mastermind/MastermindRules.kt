package com.logicpuzzles.mastermind

data class MastermindScore(
    val exact: Int,
    val misplaced: Int
)

object MastermindRules {
    fun score(secret: List<Int>, guess: List<Int>): MastermindScore {
        require(secret.size == guess.size) { "Secret and guess must have the same number of slots" }

        var exact = 0
        val secretRemaining = HashMap<Int, Int>()
        val guessRemaining = HashMap<Int, Int>()
        for (index in secret.indices) {
            if (secret[index] == guess[index]) {
                exact++
            } else {
                secretRemaining[secret[index]] = secretRemaining.getOrDefault(secret[index], 0) + 1
                guessRemaining[guess[index]] = guessRemaining.getOrDefault(guess[index], 0) + 1
            }
        }

        var misplaced = 0
        for ((color, count) in guessRemaining) {
            misplaced += minOf(count, secretRemaining.getOrDefault(color, 0))
        }
        return MastermindScore(exact, misplaced)
    }
}
