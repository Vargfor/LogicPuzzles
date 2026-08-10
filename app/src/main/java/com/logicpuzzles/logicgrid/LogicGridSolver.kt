package com.logicpuzzles.logicgrid

object LogicGridSolver {
    fun countSolutions(
        puzzle: LogicGridPuzzle,
        omittedClueIndex: Int = -1,
        limit: Int = 2
    ): Int {
        val itemCount = puzzle.items.firstOrNull()?.size ?: return 0
        val categoryCount = puzzle.categories.size
        val constraints = puzzle.typedClues.mapIndexedNotNull { index, clue ->
            clue.constraint.takeUnless { index == omittedClueIndex }
        }
        val permutations = permutations(itemCount)
        val assignment = arrayOfNulls<IntArray>(categoryCount)
        assignment[0] = IntArray(itemCount) { it }
        var count = 0

        fun groupOf(category: Int, item: Int): Int {
            val permutation = assignment[category] ?: return -1
            return permutation.indexOf(item)
        }

        fun constraintsHold(): Boolean {
            for (constraint in constraints) {
                if (assignment[constraint.categoryA] == null || assignment[constraint.categoryB] == null) continue
                val sameGroup = groupOf(constraint.categoryA, constraint.itemA) ==
                    groupOf(constraint.categoryB, constraint.itemB)
                if (sameGroup != constraint.matches) return false
            }
            return true
        }

        fun nextCategory(): Int {
            var bestCategory = -1
            var bestConnections = -1
            for (category in 1 until categoryCount) {
                if (assignment[category] != null) continue
                val connections = constraints.count { constraint ->
                    when (category) {
                        constraint.categoryA -> assignment[constraint.categoryB] != null
                        constraint.categoryB -> assignment[constraint.categoryA] != null
                        else -> false
                    }
                }
                if (connections > bestConnections) {
                    bestConnections = connections
                    bestCategory = category
                }
            }
            return bestCategory
        }

        fun search() {
            if (count >= limit) return
            val category = nextCategory()
            if (category == -1) {
                count++
                return
            }
            for (permutation in permutations) {
                assignment[category] = permutation
                if (constraintsHold()) search()
                assignment[category] = null
                if (count >= limit) return
            }
        }

        search()
        return count
    }

    fun storedSolutionSatisfiesClues(puzzle: LogicGridPuzzle): Boolean {
        for (clue in puzzle.typedClues) {
            val constraint = clue.constraint
            val entryA = puzzle.solution.indexOfFirst { it[constraint.categoryA] == constraint.itemA }
            val entryB = puzzle.solution.indexOfFirst { it[constraint.categoryB] == constraint.itemB }
            if ((entryA == entryB) != constraint.matches) return false
        }
        return true
    }

    private fun permutations(size: Int): List<IntArray> {
        val result = ArrayList<IntArray>()
        val current = IntArray(size)
        val used = BooleanArray(size)

        fun build(position: Int) {
            if (position == size) {
                result.add(current.copyOf())
                return
            }
            for (value in 0 until size) {
                if (used[value]) continue
                used[value] = true
                current[position] = value
                build(position + 1)
                used[value] = false
            }
        }

        build(0)
        return result
    }
}
