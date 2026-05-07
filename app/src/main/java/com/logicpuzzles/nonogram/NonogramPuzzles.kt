package com.logicpuzzles.nonogram

object NonogramPuzzles {
    private val EASY = arrayOf(
        // 1: Heart
        arrayOf(
            intArrayOf(0,1,0,1,0),
            intArrayOf(1,1,1,1,1),
            intArrayOf(1,1,1,1,1),
            intArrayOf(0,1,1,1,0),
            intArrayOf(0,0,1,0,0)
        ),
        // 2: Plus
        arrayOf(
            intArrayOf(0,0,1,0,0),
            intArrayOf(0,0,1,0,0),
            intArrayOf(1,1,1,1,1),
            intArrayOf(0,0,1,0,0),
            intArrayOf(0,0,1,0,0)
        ),
        // 3: X
        arrayOf(
            intArrayOf(1,0,0,0,1),
            intArrayOf(0,1,0,1,0),
            intArrayOf(0,0,1,0,0),
            intArrayOf(0,1,0,1,0),
            intArrayOf(1,0,0,0,1)
        ),
        // 4: Arrow up
        arrayOf(
            intArrayOf(0,0,1,0,0),
            intArrayOf(0,1,1,1,0),
            intArrayOf(1,0,1,0,1),
            intArrayOf(0,0,1,0,0),
            intArrayOf(0,0,1,0,0)
        ),
        // 5: Diamond
        arrayOf(
            intArrayOf(0,0,1,0,0),
            intArrayOf(0,1,1,1,0),
            intArrayOf(1,1,1,1,1),
            intArrayOf(0,1,1,1,0),
            intArrayOf(0,0,1,0,0)
        ),
        // 6: House
        arrayOf(
            intArrayOf(0,0,1,0,0),
            intArrayOf(0,1,1,1,0),
            intArrayOf(1,1,1,1,1),
            intArrayOf(1,1,0,1,1),
            intArrayOf(1,1,1,1,1)
        ),
        // 7: Letter L
        arrayOf(
            intArrayOf(1,0,0,0,0),
            intArrayOf(1,0,0,0,0),
            intArrayOf(1,0,0,0,0),
            intArrayOf(1,0,0,0,0),
            intArrayOf(1,1,1,1,1)
        ),
        // 8: Letter T
        arrayOf(
            intArrayOf(1,1,1,1,1),
            intArrayOf(0,0,1,0,0),
            intArrayOf(0,0,1,0,0),
            intArrayOf(0,0,1,0,0),
            intArrayOf(0,0,1,0,0)
        ),
        // 9: Hourglass
        arrayOf(
            intArrayOf(1,1,1,1,1),
            intArrayOf(0,1,1,1,0),
            intArrayOf(0,0,1,0,0),
            intArrayOf(0,1,1,1,0),
            intArrayOf(1,1,1,1,1)
        ),
        // 10: Square ring
        arrayOf(
            intArrayOf(1,1,1,1,1),
            intArrayOf(1,0,0,0,1),
            intArrayOf(1,0,0,0,1),
            intArrayOf(1,0,0,0,1),
            intArrayOf(1,1,1,1,1)
        ),
        // 11: Crown 6x6
        arrayOf(
            intArrayOf(1,0,1,0,1,0),
            intArrayOf(1,0,1,0,1,0),
            intArrayOf(1,1,1,1,1,1),
            intArrayOf(0,1,1,1,1,0),
            intArrayOf(0,1,1,1,1,0),
            intArrayOf(0,1,1,1,1,0)
        ),
        // 12: Rocket 6x6
        arrayOf(
            intArrayOf(0,0,1,1,0,0),
            intArrayOf(0,1,1,1,1,0),
            intArrayOf(0,1,1,1,1,0),
            intArrayOf(1,1,1,1,1,1),
            intArrayOf(0,1,0,0,1,0),
            intArrayOf(0,1,0,0,1,0)
        ),
        // 13: Stairs 6x6
        arrayOf(
            intArrayOf(1,0,0,0,0,0),
            intArrayOf(1,1,0,0,0,0),
            intArrayOf(1,1,1,0,0,0),
            intArrayOf(1,1,1,1,0,0),
            intArrayOf(1,1,1,1,1,0),
            intArrayOf(1,1,1,1,1,1)
        ),
        // 14: Ring 6x6
        arrayOf(
            intArrayOf(0,1,1,1,1,0),
            intArrayOf(1,1,0,0,1,1),
            intArrayOf(1,0,0,0,0,1),
            intArrayOf(1,0,0,0,0,1),
            intArrayOf(1,1,0,0,1,1),
            intArrayOf(0,1,1,1,1,0)
        ),
        // 15: Lightning 6x6
        arrayOf(
            intArrayOf(0,0,1,1,1,0),
            intArrayOf(0,1,1,1,0,0),
            intArrayOf(0,1,1,0,0,0),
            intArrayOf(0,0,0,1,1,0),
            intArrayOf(0,0,0,1,1,1),
            intArrayOf(0,0,0,0,1,1)
        )
    )

    private val MEDIUM = arrayOf(
        // 1: House
        arrayOf(
            intArrayOf(0,0,0,1,0,0,0),
            intArrayOf(0,0,1,1,1,0,0),
            intArrayOf(0,1,1,1,1,1,0),
            intArrayOf(1,1,1,1,1,1,1),
            intArrayOf(0,1,0,1,0,1,0),
            intArrayOf(0,1,0,1,0,1,0),
            intArrayOf(0,1,1,1,1,1,0)
        ),
        // 2: Smiley
        arrayOf(
            intArrayOf(0,1,1,1,1,1,0),
            intArrayOf(1,0,0,0,0,0,1),
            intArrayOf(1,0,1,0,1,0,1),
            intArrayOf(1,0,0,0,0,0,1),
            intArrayOf(1,0,1,1,1,0,1),
            intArrayOf(1,0,0,0,0,0,1),
            intArrayOf(0,1,1,1,1,1,0)
        ),
        // 3: Big heart
        arrayOf(
            intArrayOf(0,1,1,0,1,1,0),
            intArrayOf(1,1,1,1,1,1,1),
            intArrayOf(1,1,1,1,1,1,1),
            intArrayOf(1,1,1,1,1,1,1),
            intArrayOf(0,1,1,1,1,1,0),
            intArrayOf(0,0,1,1,1,0,0),
            intArrayOf(0,0,0,1,0,0,0)
        ),
        // 4: Tree
        arrayOf(
            intArrayOf(0,0,0,1,0,0,0),
            intArrayOf(0,0,1,1,1,0,0),
            intArrayOf(0,1,1,1,1,1,0),
            intArrayOf(1,1,1,1,1,1,1),
            intArrayOf(0,1,1,1,1,1,0),
            intArrayOf(0,0,0,1,0,0,0),
            intArrayOf(0,0,1,1,1,0,0)
        ),
        // 5: Star
        arrayOf(
            intArrayOf(0,0,0,1,0,0,0),
            intArrayOf(0,0,1,1,1,0,0),
            intArrayOf(1,1,1,1,1,1,1),
            intArrayOf(0,1,1,1,1,1,0),
            intArrayOf(0,1,1,0,1,1,0),
            intArrayOf(1,1,0,0,0,1,1),
            intArrayOf(1,0,0,0,0,0,1)
        ),
        // 6: Letter L
        arrayOf(
            intArrayOf(1,1,0,0,0,0,0),
            intArrayOf(1,1,0,0,0,0,0),
            intArrayOf(1,1,0,0,0,0,0),
            intArrayOf(1,1,0,0,0,0,0),
            intArrayOf(1,1,0,0,0,0,0),
            intArrayOf(1,1,1,1,1,1,1),
            intArrayOf(1,1,1,1,1,1,1)
        ),
        // 7: Letter T
        arrayOf(
            intArrayOf(1,1,1,1,1,1,1),
            intArrayOf(1,1,1,1,1,1,1),
            intArrayOf(0,0,1,1,1,0,0),
            intArrayOf(0,0,1,1,1,0,0),
            intArrayOf(0,0,1,1,1,0,0),
            intArrayOf(0,0,1,1,1,0,0),
            intArrayOf(0,0,1,1,1,0,0)
        ),
        // 8: Letter A
        arrayOf(
            intArrayOf(0,0,0,1,0,0,0),
            intArrayOf(0,0,1,1,1,0,0),
            intArrayOf(0,1,1,0,1,1,0),
            intArrayOf(0,1,1,1,1,1,0),
            intArrayOf(1,1,1,1,1,1,1),
            intArrayOf(1,1,0,0,0,1,1),
            intArrayOf(1,1,0,0,0,1,1)
        ),
        // 9: Apple
        arrayOf(
            intArrayOf(0,0,1,0,1,0,0),
            intArrayOf(0,0,0,1,0,0,0),
            intArrayOf(0,1,1,1,1,1,0),
            intArrayOf(1,1,1,1,1,1,1),
            intArrayOf(1,1,1,1,1,1,1),
            intArrayOf(1,1,1,1,1,1,1),
            intArrayOf(0,1,1,1,1,1,0)
        ),
        // 10: Bow tie
        arrayOf(
            intArrayOf(1,1,0,0,0,1,1),
            intArrayOf(1,1,1,0,1,1,1),
            intArrayOf(1,1,1,1,1,1,1),
            intArrayOf(1,1,1,1,1,1,1),
            intArrayOf(1,1,1,1,1,1,1),
            intArrayOf(1,1,1,0,1,1,1),
            intArrayOf(1,1,0,0,0,1,1)
        ),
        // 11: Hourglass 8x8
        arrayOf(
            intArrayOf(1,1,1,1,1,1,1,1),
            intArrayOf(0,1,1,1,1,1,1,0),
            intArrayOf(0,0,1,1,1,1,0,0),
            intArrayOf(0,0,0,1,1,0,0,0),
            intArrayOf(0,0,0,1,1,0,0,0),
            intArrayOf(0,0,1,1,1,1,0,0),
            intArrayOf(0,1,1,1,1,1,1,0),
            intArrayOf(1,1,1,1,1,1,1,1)
        ),
        // 12: Target 8x8
        arrayOf(
            intArrayOf(0,0,1,1,1,1,0,0),
            intArrayOf(0,1,0,0,0,0,1,0),
            intArrayOf(1,0,0,1,1,0,0,1),
            intArrayOf(1,0,1,0,0,1,0,1),
            intArrayOf(1,0,1,0,0,1,0,1),
            intArrayOf(1,0,0,1,1,0,0,1),
            intArrayOf(0,1,0,0,0,0,1,0),
            intArrayOf(0,0,1,1,1,1,0,0)
        ),
        // 13: Snowflake 8x8
        arrayOf(
            intArrayOf(0,0,0,1,1,0,0,0),
            intArrayOf(0,1,0,1,1,0,1,0),
            intArrayOf(0,0,1,1,1,1,0,0),
            intArrayOf(1,1,1,1,1,1,1,1),
            intArrayOf(1,1,1,1,1,1,1,1),
            intArrayOf(0,0,1,1,1,1,0,0),
            intArrayOf(0,1,0,1,1,0,1,0),
            intArrayOf(0,0,0,1,1,0,0,0)
        ),
        // 14: Arch 8x8
        arrayOf(
            intArrayOf(0,0,1,1,1,1,0,0),
            intArrayOf(0,1,1,0,0,1,1,0),
            intArrayOf(1,1,0,0,0,0,1,1),
            intArrayOf(1,1,0,0,0,0,1,1),
            intArrayOf(1,1,0,0,0,0,1,1),
            intArrayOf(1,1,1,0,0,1,1,1),
            intArrayOf(1,1,1,1,1,1,1,1),
            intArrayOf(1,1,1,1,1,1,1,1)
        ),
        // 15: City 8x8
        arrayOf(
            intArrayOf(0,1,0,0,1,0,1,0),
            intArrayOf(0,1,0,0,1,0,1,0),
            intArrayOf(1,1,1,1,1,1,1,1),
            intArrayOf(1,0,1,0,1,0,1,0),
            intArrayOf(1,1,1,1,1,1,1,1),
            intArrayOf(1,0,1,0,1,0,1,0),
            intArrayOf(1,1,1,1,1,1,1,1),
            intArrayOf(0,0,0,0,0,0,0,0)
        )
    )

    private val HARD = arrayOf(
        // 1: Cat
        arrayOf(
            intArrayOf(1,0,0,0,0,0,0,0,1),
            intArrayOf(1,1,0,0,0,0,0,1,1),
            intArrayOf(1,1,1,1,1,1,1,1,1),
            intArrayOf(1,0,1,0,0,0,1,0,1),
            intArrayOf(1,1,1,1,1,1,1,1,1),
            intArrayOf(1,1,1,0,1,0,1,1,1),
            intArrayOf(1,1,1,1,1,1,1,1,1),
            intArrayOf(0,1,1,1,1,1,1,1,0),
            intArrayOf(0,0,1,1,1,1,1,0,0)
        ),
        // 2: Sailboat
        arrayOf(
            intArrayOf(0,0,0,0,1,0,0,0,0),
            intArrayOf(0,0,0,1,1,0,0,0,0),
            intArrayOf(0,0,1,1,1,0,0,0,0),
            intArrayOf(0,1,1,1,1,0,0,0,0),
            intArrayOf(1,1,1,1,1,1,1,1,0),
            intArrayOf(0,0,0,0,0,0,0,0,0),
            intArrayOf(0,1,1,1,1,1,1,1,0),
            intArrayOf(0,0,1,1,1,1,1,0,0),
            intArrayOf(0,0,0,1,1,1,0,0,0)
        ),
        // 3: Mushroom
        arrayOf(
            intArrayOf(0,0,1,1,1,1,1,0,0),
            intArrayOf(0,1,1,1,1,1,1,1,0),
            intArrayOf(1,1,1,0,1,0,1,1,1),
            intArrayOf(1,1,1,1,1,1,1,1,1),
            intArrayOf(1,1,1,1,1,1,1,1,1),
            intArrayOf(0,0,1,1,1,1,1,0,0),
            intArrayOf(0,0,0,1,1,1,0,0,0),
            intArrayOf(0,0,0,1,1,1,0,0,0),
            intArrayOf(0,0,1,1,1,1,1,0,0)
        ),
        // 4: Butterfly
        arrayOf(
            intArrayOf(1,1,0,0,0,0,0,1,1),
            intArrayOf(1,1,1,0,1,0,1,1,1),
            intArrayOf(1,1,1,1,1,1,1,1,1),
            intArrayOf(1,1,1,1,1,1,1,1,1),
            intArrayOf(0,1,1,1,1,1,1,1,0),
            intArrayOf(0,0,0,1,1,1,0,0,0),
            intArrayOf(0,0,1,1,1,1,1,0,0),
            intArrayOf(0,0,0,1,1,1,0,0,0),
            intArrayOf(0,0,0,0,1,0,0,0,0)
        ),
        // 5: Anchor
        arrayOf(
            intArrayOf(0,0,0,0,1,0,0,0,0),
            intArrayOf(0,0,0,1,1,1,0,0,0),
            intArrayOf(0,0,0,1,1,1,0,0,0),
            intArrayOf(0,0,1,1,1,1,1,0,0),
            intArrayOf(0,0,0,0,1,0,0,0,0),
            intArrayOf(1,0,0,0,1,0,0,0,1),
            intArrayOf(1,0,0,0,1,0,0,0,1),
            intArrayOf(1,1,1,1,1,1,1,1,1),
            intArrayOf(0,1,1,1,1,1,1,1,0)
        ),
        // 6: Frame
        arrayOf(
            intArrayOf(1,1,1,1,1,1,1,1,1),
            intArrayOf(1,0,0,0,0,0,0,0,1),
            intArrayOf(1,0,0,0,0,0,0,0,1),
            intArrayOf(1,0,0,0,0,0,0,0,1),
            intArrayOf(1,0,0,0,0,0,0,0,1),
            intArrayOf(1,0,0,0,0,0,0,0,1),
            intArrayOf(1,0,0,0,0,0,0,0,1),
            intArrayOf(1,0,0,0,0,0,0,0,1),
            intArrayOf(1,1,1,1,1,1,1,1,1)
        ),
        // 7: Big plus
        arrayOf(
            intArrayOf(0,0,0,0,1,0,0,0,0),
            intArrayOf(0,0,0,0,1,0,0,0,0),
            intArrayOf(0,0,0,0,1,0,0,0,0),
            intArrayOf(0,0,0,0,1,0,0,0,0),
            intArrayOf(1,1,1,1,1,1,1,1,1),
            intArrayOf(0,0,0,0,1,0,0,0,0),
            intArrayOf(0,0,0,0,1,0,0,0,0),
            intArrayOf(0,0,0,0,1,0,0,0,0),
            intArrayOf(0,0,0,0,1,0,0,0,0)
        ),
        // 8: Big X
        arrayOf(
            intArrayOf(1,0,0,0,0,0,0,0,1),
            intArrayOf(0,1,0,0,0,0,0,1,0),
            intArrayOf(0,0,1,0,0,0,1,0,0),
            intArrayOf(0,0,0,1,0,1,0,0,0),
            intArrayOf(0,0,0,0,1,0,0,0,0),
            intArrayOf(0,0,0,1,0,1,0,0,0),
            intArrayOf(0,0,1,0,0,0,1,0,0),
            intArrayOf(0,1,0,0,0,0,0,1,0),
            intArrayOf(1,0,0,0,0,0,0,0,1)
        ),
        // 9: Big diamond
        arrayOf(
            intArrayOf(0,0,0,0,1,0,0,0,0),
            intArrayOf(0,0,0,1,1,1,0,0,0),
            intArrayOf(0,0,1,1,1,1,1,0,0),
            intArrayOf(0,1,1,1,1,1,1,1,0),
            intArrayOf(1,1,1,1,1,1,1,1,1),
            intArrayOf(0,1,1,1,1,1,1,1,0),
            intArrayOf(0,0,1,1,1,1,1,0,0),
            intArrayOf(0,0,0,1,1,1,0,0,0),
            intArrayOf(0,0,0,0,1,0,0,0,0)
        ),
        // 10: Letter H
        arrayOf(
            intArrayOf(1,1,0,0,0,0,0,1,1),
            intArrayOf(1,1,0,0,0,0,0,1,1),
            intArrayOf(1,1,0,0,0,0,0,1,1),
            intArrayOf(1,1,0,0,0,0,0,1,1),
            intArrayOf(1,1,1,1,1,1,1,1,1),
            intArrayOf(1,1,0,0,0,0,0,1,1),
            intArrayOf(1,1,0,0,0,0,0,1,1),
            intArrayOf(1,1,0,0,0,0,0,1,1),
            intArrayOf(1,1,0,0,0,0,0,1,1)
        ),
        // 11: Star 10x10
        arrayOf(
            intArrayOf(0,0,0,0,1,1,0,0,0,0),
            intArrayOf(0,0,0,1,1,1,1,0,0,0),
            intArrayOf(1,1,1,1,1,1,1,1,1,1),
            intArrayOf(0,1,1,1,1,1,1,1,1,0),
            intArrayOf(0,0,1,1,1,1,1,1,0,0),
            intArrayOf(0,0,1,1,1,1,1,1,0,0),
            intArrayOf(0,1,1,1,1,1,1,1,1,0),
            intArrayOf(1,1,0,0,1,1,0,0,1,1),
            intArrayOf(1,0,0,0,0,0,0,0,0,1),
            intArrayOf(0,0,0,0,0,0,0,0,0,0)
        ),
        // 12: Owl 10x10
        arrayOf(
            intArrayOf(0,1,1,0,0,0,0,1,1,0),
            intArrayOf(1,1,1,1,1,1,1,1,1,1),
            intArrayOf(1,1,0,1,1,1,1,0,1,1),
            intArrayOf(1,0,0,0,1,1,0,0,0,1),
            intArrayOf(1,1,1,1,1,1,1,1,1,1),
            intArrayOf(1,1,1,0,1,1,0,1,1,1),
            intArrayOf(1,1,1,1,1,1,1,1,1,1),
            intArrayOf(0,1,1,1,1,1,1,1,1,0),
            intArrayOf(0,0,1,1,1,1,1,1,0,0),
            intArrayOf(0,0,1,0,0,0,0,1,0,0)
        ),
        // 13: Rocket 10x10
        arrayOf(
            intArrayOf(0,0,0,0,1,1,0,0,0,0),
            intArrayOf(0,0,0,1,1,1,1,0,0,0),
            intArrayOf(0,0,1,1,1,1,1,1,0,0),
            intArrayOf(0,1,1,1,1,1,1,1,1,0),
            intArrayOf(0,1,1,1,1,1,1,1,1,0),
            intArrayOf(0,1,1,1,1,1,1,1,1,0),
            intArrayOf(1,1,1,1,1,1,1,1,1,1),
            intArrayOf(1,1,1,1,1,1,1,1,1,1),
            intArrayOf(0,1,1,0,0,0,0,1,1,0),
            intArrayOf(0,1,1,0,0,0,0,1,1,0)
        ),
        // 14: Big frame 10x10
        arrayOf(
            intArrayOf(1,1,1,1,1,1,1,1,1,1),
            intArrayOf(1,0,0,0,0,0,0,0,0,1),
            intArrayOf(1,0,0,0,0,0,0,0,0,1),
            intArrayOf(1,0,0,1,1,1,1,0,0,1),
            intArrayOf(1,0,0,1,0,0,1,0,0,1),
            intArrayOf(1,0,0,1,0,0,1,0,0,1),
            intArrayOf(1,0,0,1,1,1,1,0,0,1),
            intArrayOf(1,0,0,0,0,0,0,0,0,1),
            intArrayOf(1,0,0,0,0,0,0,0,0,1),
            intArrayOf(1,1,1,1,1,1,1,1,1,1)
        ),
        // 15: Knight 10x10
        arrayOf(
            intArrayOf(0,0,1,1,0,0,0,0,0,0),
            intArrayOf(0,1,1,1,1,0,0,0,0,0),
            intArrayOf(1,1,1,1,1,1,0,0,0,0),
            intArrayOf(1,1,1,1,1,1,1,0,0,0),
            intArrayOf(0,1,1,1,1,1,1,1,0,0),
            intArrayOf(0,0,1,1,1,1,1,1,1,0),
            intArrayOf(0,0,0,1,1,1,1,1,1,1),
            intArrayOf(0,0,0,0,1,1,1,1,1,1),
            intArrayOf(0,0,0,0,0,1,1,1,1,1),
            intArrayOf(0,0,0,0,0,0,1,1,1,1)
        )
    )

    private val EXPERT = arrayOf(
        // 1: Ship
        arrayOf(
            intArrayOf(0,0,0,0,0,1,0,0,0,0,0),
            intArrayOf(0,0,0,0,1,1,1,0,0,0,0),
            intArrayOf(0,0,0,1,1,1,1,1,0,0,0),
            intArrayOf(0,0,1,1,1,1,1,1,1,0,0),
            intArrayOf(0,1,1,1,1,1,1,1,1,1,0),
            intArrayOf(1,1,1,1,1,1,1,1,1,1,1),
            intArrayOf(0,0,0,0,0,0,0,0,0,0,0),
            intArrayOf(1,1,1,1,1,1,1,1,1,1,1),
            intArrayOf(0,1,1,1,1,1,1,1,1,1,0),
            intArrayOf(0,0,1,1,1,1,1,1,1,0,0),
            intArrayOf(0,0,0,1,1,1,1,1,0,0,0)
        ),
        // 2: Crown
        arrayOf(
            intArrayOf(1,0,0,0,1,0,1,0,0,0,1),
            intArrayOf(1,1,0,1,1,0,1,1,0,1,1),
            intArrayOf(1,1,1,1,1,1,1,1,1,1,1),
            intArrayOf(1,1,1,1,1,1,1,1,1,1,1),
            intArrayOf(1,0,1,1,0,1,0,1,1,0,1),
            intArrayOf(1,1,1,1,1,1,1,1,1,1,1),
            intArrayOf(1,1,1,1,1,1,1,1,1,1,1),
            intArrayOf(1,1,1,1,1,1,1,1,1,1,1),
            intArrayOf(0,1,1,1,1,1,1,1,1,1,0),
            intArrayOf(0,0,1,1,1,1,1,1,1,0,0),
            intArrayOf(0,0,0,1,1,1,1,1,0,0,0)
        ),
        // 3: Owl
        arrayOf(
            intArrayOf(0,1,1,0,0,0,0,0,1,1,0),
            intArrayOf(1,1,1,1,1,1,1,1,1,1,1),
            intArrayOf(1,1,0,1,1,1,1,1,0,1,1),
            intArrayOf(1,0,0,0,1,1,1,0,0,0,1),
            intArrayOf(1,1,1,1,1,1,1,1,1,1,1),
            intArrayOf(1,1,1,1,0,1,0,1,1,1,1),
            intArrayOf(1,1,1,1,1,1,1,1,1,1,1),
            intArrayOf(0,1,1,1,1,1,1,1,1,1,0),
            intArrayOf(0,0,1,1,1,1,1,1,1,0,0),
            intArrayOf(0,0,0,1,1,1,1,1,0,0,0),
            intArrayOf(0,0,0,1,0,1,0,1,0,0,0)
        ),
        // 4: Skull
        arrayOf(
            intArrayOf(0,0,1,1,1,1,1,1,1,0,0),
            intArrayOf(0,1,1,1,1,1,1,1,1,1,0),
            intArrayOf(1,1,1,1,1,1,1,1,1,1,1),
            intArrayOf(1,1,0,0,1,1,1,0,0,1,1),
            intArrayOf(1,1,0,0,1,1,1,0,0,1,1),
            intArrayOf(1,1,1,1,1,0,1,1,1,1,1),
            intArrayOf(1,1,1,1,1,1,1,1,1,1,1),
            intArrayOf(0,1,1,1,1,1,1,1,1,1,0),
            intArrayOf(0,0,1,0,1,0,1,0,1,0,0),
            intArrayOf(0,0,1,0,1,0,1,0,1,0,0),
            intArrayOf(0,0,1,0,1,0,1,0,1,0,0)
        ),
        // 5: Castle
        arrayOf(
            intArrayOf(1,0,1,0,1,0,1,0,1,0,1),
            intArrayOf(1,1,1,1,1,1,1,1,1,1,1),
            intArrayOf(1,1,1,1,1,1,1,1,1,1,1),
            intArrayOf(1,1,0,0,1,1,1,0,0,1,1),
            intArrayOf(1,1,0,0,1,0,1,0,0,1,1),
            intArrayOf(1,1,1,1,1,0,1,1,1,1,1),
            intArrayOf(1,1,1,1,1,1,1,1,1,1,1),
            intArrayOf(1,1,1,0,0,1,0,0,1,1,1),
            intArrayOf(1,1,1,0,0,1,0,0,1,1,1),
            intArrayOf(1,1,1,1,1,1,1,1,1,1,1),
            intArrayOf(1,1,1,1,1,1,1,1,1,1,1)
        ),
        // 6: Frame
        arrayOf(
            intArrayOf(1,1,1,1,1,1,1,1,1,1,1),
            intArrayOf(1,0,0,0,0,0,0,0,0,0,1),
            intArrayOf(1,0,0,0,0,0,0,0,0,0,1),
            intArrayOf(1,0,0,0,0,0,0,0,0,0,1),
            intArrayOf(1,0,0,0,0,0,0,0,0,0,1),
            intArrayOf(1,0,0,0,0,0,0,0,0,0,1),
            intArrayOf(1,0,0,0,0,0,0,0,0,0,1),
            intArrayOf(1,0,0,0,0,0,0,0,0,0,1),
            intArrayOf(1,0,0,0,0,0,0,0,0,0,1),
            intArrayOf(1,0,0,0,0,0,0,0,0,0,1),
            intArrayOf(1,1,1,1,1,1,1,1,1,1,1)
        ),
        // 7: Big plus
        arrayOf(
            intArrayOf(0,0,0,0,0,1,0,0,0,0,0),
            intArrayOf(0,0,0,0,0,1,0,0,0,0,0),
            intArrayOf(0,0,0,0,0,1,0,0,0,0,0),
            intArrayOf(0,0,0,0,0,1,0,0,0,0,0),
            intArrayOf(0,0,0,0,0,1,0,0,0,0,0),
            intArrayOf(1,1,1,1,1,1,1,1,1,1,1),
            intArrayOf(0,0,0,0,0,1,0,0,0,0,0),
            intArrayOf(0,0,0,0,0,1,0,0,0,0,0),
            intArrayOf(0,0,0,0,0,1,0,0,0,0,0),
            intArrayOf(0,0,0,0,0,1,0,0,0,0,0),
            intArrayOf(0,0,0,0,0,1,0,0,0,0,0)
        ),
        // 8: Big X
        arrayOf(
            intArrayOf(1,0,0,0,0,0,0,0,0,0,1),
            intArrayOf(0,1,0,0,0,0,0,0,0,1,0),
            intArrayOf(0,0,1,0,0,0,0,0,1,0,0),
            intArrayOf(0,0,0,1,0,0,0,1,0,0,0),
            intArrayOf(0,0,0,0,1,0,1,0,0,0,0),
            intArrayOf(0,0,0,0,0,1,0,0,0,0,0),
            intArrayOf(0,0,0,0,1,0,1,0,0,0,0),
            intArrayOf(0,0,0,1,0,0,0,1,0,0,0),
            intArrayOf(0,0,1,0,0,0,0,0,1,0,0),
            intArrayOf(0,1,0,0,0,0,0,0,0,1,0),
            intArrayOf(1,0,0,0,0,0,0,0,0,0,1)
        ),
        // 9: Big diamond
        arrayOf(
            intArrayOf(0,0,0,0,0,1,0,0,0,0,0),
            intArrayOf(0,0,0,0,1,1,1,0,0,0,0),
            intArrayOf(0,0,0,1,1,1,1,1,0,0,0),
            intArrayOf(0,0,1,1,1,1,1,1,1,0,0),
            intArrayOf(0,1,1,1,1,1,1,1,1,1,0),
            intArrayOf(1,1,1,1,1,1,1,1,1,1,1),
            intArrayOf(0,1,1,1,1,1,1,1,1,1,0),
            intArrayOf(0,0,1,1,1,1,1,1,1,0,0),
            intArrayOf(0,0,0,1,1,1,1,1,0,0,0),
            intArrayOf(0,0,0,0,1,1,1,0,0,0,0),
            intArrayOf(0,0,0,0,0,1,0,0,0,0,0)
        ),
        // 10: Letter H big
        arrayOf(
            intArrayOf(1,1,1,0,0,0,0,0,1,1,1),
            intArrayOf(1,1,1,0,0,0,0,0,1,1,1),
            intArrayOf(1,1,1,0,0,0,0,0,1,1,1),
            intArrayOf(1,1,1,0,0,0,0,0,1,1,1),
            intArrayOf(1,1,1,1,1,1,1,1,1,1,1),
            intArrayOf(1,1,1,1,1,1,1,1,1,1,1),
            intArrayOf(1,1,1,1,1,1,1,1,1,1,1),
            intArrayOf(1,1,1,0,0,0,0,0,1,1,1),
            intArrayOf(1,1,1,0,0,0,0,0,1,1,1),
            intArrayOf(1,1,1,0,0,0,0,0,1,1,1),
            intArrayOf(1,1,1,0,0,0,0,0,1,1,1)
        ),
        // 11: Dragon 12x12
        arrayOf(
            intArrayOf(0,0,0,0,1,1,1,0,0,0,0,0),
            intArrayOf(0,0,0,1,1,1,1,1,0,0,0,0),
            intArrayOf(0,1,1,1,1,1,1,1,1,0,0,0),
            intArrayOf(1,1,1,1,1,1,1,1,1,1,0,0),
            intArrayOf(1,1,0,1,1,1,1,1,0,1,1,0),
            intArrayOf(1,1,1,1,1,1,1,1,1,1,1,0),
            intArrayOf(0,1,1,1,1,0,1,1,1,1,1,1),
            intArrayOf(0,0,1,1,1,1,1,1,1,1,1,1),
            intArrayOf(0,0,0,1,1,1,1,1,1,1,0,0),
            intArrayOf(0,0,0,0,1,1,1,1,0,0,0,0),
            intArrayOf(0,0,0,0,1,0,0,1,0,0,0,0),
            intArrayOf(0,0,0,0,1,0,0,1,0,0,0,0)
        ),
        // 12: Castle 12x12
        arrayOf(
            intArrayOf(1,0,1,0,1,0,1,0,1,0,1,0),
            intArrayOf(1,1,1,1,1,1,1,1,1,1,1,1),
            intArrayOf(1,1,1,1,1,1,1,1,1,1,1,1),
            intArrayOf(1,1,0,0,0,1,1,0,0,0,1,1),
            intArrayOf(1,1,0,0,0,1,1,0,0,0,1,1),
            intArrayOf(1,1,0,0,0,0,0,0,0,0,1,1),
            intArrayOf(1,1,1,1,1,1,1,1,1,1,1,1),
            intArrayOf(1,1,0,0,1,1,1,1,0,0,1,1),
            intArrayOf(1,1,0,0,1,1,1,1,0,0,1,1),
            intArrayOf(1,1,0,0,0,0,0,0,0,0,1,1),
            intArrayOf(1,1,1,1,1,1,1,1,1,1,1,1),
            intArrayOf(1,1,1,1,1,1,1,1,1,1,1,1)
        ),
        // 13: Sunflower 12x12
        arrayOf(
            intArrayOf(0,0,0,1,1,1,1,1,0,0,0,0),
            intArrayOf(0,0,1,1,0,0,0,1,1,0,0,0),
            intArrayOf(0,1,1,0,0,0,0,0,1,1,0,0),
            intArrayOf(1,1,0,0,1,1,1,0,0,1,1,0),
            intArrayOf(1,0,0,1,1,1,1,1,0,0,0,1),
            intArrayOf(1,0,0,1,1,1,1,1,0,0,0,1),
            intArrayOf(1,0,0,1,1,1,1,1,0,0,0,1),
            intArrayOf(1,1,0,0,1,1,1,0,0,1,1,0),
            intArrayOf(0,1,1,0,0,0,0,0,1,1,0,0),
            intArrayOf(0,0,1,1,0,0,0,1,1,0,0,0),
            intArrayOf(0,0,0,1,1,1,1,1,0,0,0,0),
            intArrayOf(0,0,0,0,1,1,1,0,0,0,0,0)
        ),
        // 14: Knight 12x12
        arrayOf(
            intArrayOf(0,0,1,1,1,0,0,0,0,0,0,0),
            intArrayOf(0,1,1,1,1,1,0,0,0,0,0,0),
            intArrayOf(1,1,1,1,1,1,1,0,0,0,0,0),
            intArrayOf(1,1,1,1,1,1,1,1,0,0,0,0),
            intArrayOf(0,1,1,1,1,1,1,1,1,0,0,0),
            intArrayOf(0,0,1,1,1,1,1,1,1,1,0,0),
            intArrayOf(0,0,0,1,1,1,1,1,1,1,1,0),
            intArrayOf(0,0,0,0,1,1,1,1,1,1,1,1),
            intArrayOf(0,0,0,0,0,1,1,1,1,1,1,1),
            intArrayOf(0,0,0,0,0,0,1,1,1,1,1,1),
            intArrayOf(0,0,0,0,0,0,0,1,1,1,1,1),
            intArrayOf(0,0,0,0,0,0,0,0,1,1,1,1)
        ),
        // 15: Big X 12x12
        arrayOf(
            intArrayOf(1,1,0,0,0,0,0,0,0,0,1,1),
            intArrayOf(1,1,1,0,0,0,0,0,0,1,1,1),
            intArrayOf(0,1,1,1,0,0,0,0,1,1,1,0),
            intArrayOf(0,0,1,1,1,0,0,1,1,1,0,0),
            intArrayOf(0,0,0,1,1,1,1,1,1,0,0,0),
            intArrayOf(0,0,0,0,1,1,1,1,0,0,0,0),
            intArrayOf(0,0,0,0,1,1,1,1,0,0,0,0),
            intArrayOf(0,0,0,1,1,1,1,1,1,0,0,0),
            intArrayOf(0,0,1,1,1,0,0,1,1,1,0,0),
            intArrayOf(0,1,1,1,0,0,0,0,1,1,1,0),
            intArrayOf(1,1,1,0,0,0,0,0,0,1,1,1),
            intArrayOf(1,1,0,0,0,0,0,0,0,0,1,1)
        )
    )

    fun get(difficulty: Int, index: Int): Array<IntArray> {
        val pool = when (difficulty) {
            0 -> EASY
            1 -> MEDIUM
            2 -> HARD
            else -> EXPERT
        }
        return pool[index.coerceIn(0, pool.size - 1)]
    }
}
