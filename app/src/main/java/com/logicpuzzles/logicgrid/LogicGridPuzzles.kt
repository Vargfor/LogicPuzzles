package com.logicpuzzles.logicgrid

data class LogicGridPuzzle(
    val title: String,
    val description: String,
    val categories: List<String>,
    val items: List<List<String>>,
    val clues: List<String>,
    // solution[entry][cat] = item index in items[cat]
    val solution: List<List<Int>>
)

object LogicGridPuzzles {

    private val PUZZLES = listOf(
        LogicGridPuzzle(
            title = "Pets and Houses",
            description = "Three friends each own one pet and live in a house of one color.",
            categories = listOf("Person", "Pet", "Color"),
            items = listOf(
                listOf("Alice", "Bob", "Carol"),
                listOf("Cat", "Dog", "Fish"),
                listOf("Red", "Blue", "Green")
            ),
            clues = listOf(
                "1. Alice does not own the dog.",
                "2. The cat owner lives in the blue house.",
                "3. Bob lives in the red house.",
                "4. Carol owns the fish."
            ),
            solution = listOf(listOf(0, 0, 1), listOf(1, 1, 0), listOf(2, 2, 2))
        ),
        LogicGridPuzzle(
            title = "Jobs and Hobbies",
            description = "Three neighbors with different jobs and hobbies.",
            categories = listOf("Person", "Job", "Hobby"),
            items = listOf(
                listOf("Emma", "Jack", "Lisa"),
                listOf("Teacher", "Doctor", "Artist"),
                listOf("Reading", "Painting", "Gaming")
            ),
            clues = listOf(
                "1. Emma is not the doctor.",
                "2. The artist loves painting.",
                "3. Jack is the teacher.",
                "4. Lisa does not read."
            ),
            solution = listOf(listOf(0, 2, 1), listOf(1, 0, 0), listOf(2, 1, 2))
        ),
        LogicGridPuzzle(
            title = "Fruits and Sports",
            description = "Three kids each have one favorite fruit and one sport.",
            categories = listOf("Kid", "Fruit", "Sport"),
            items = listOf(
                listOf("Tom", "Sara", "Ben"),
                listOf("Apple", "Banana", "Cherry"),
                listOf("Football", "Tennis", "Swimming")
            ),
            clues = listOf(
                "1. Tom likes football.",
                "2. The cherry fan plays tennis.",
                "3. Sara does not like apples.",
                "4. Ben swims."
            ),
            solution = listOf(listOf(0, 0, 0), listOf(1, 2, 1), listOf(2, 1, 2))
        ),
        LogicGridPuzzle(
            title = "Cars and Cities",
            description = "Three drivers each own a car and live in a city.",
            categories = listOf("Driver", "Car", "City"),
            items = listOf(
                listOf("Mia", "Noah", "Olive"),
                listOf("Sedan", "SUV", "Coupe"),
                listOf("Paris", "Rome", "Madrid")
            ),
            clues = listOf(
                "1. Mia drives a coupe.",
                "2. The sedan driver lives in Rome.",
                "3. Noah lives in Paris.",
                "4. Olive does not live in Madrid."
            ),
            solution = listOf(listOf(0, 2, 2), listOf(1, 1, 0), listOf(2, 0, 1))
        ),
        LogicGridPuzzle(
            title = "Books and Drinks",
            description = "Three readers each like one book genre and one drink.",
            categories = listOf("Reader", "Genre", "Drink"),
            items = listOf(
                listOf("Ana", "Leo", "Zoe"),
                listOf("Sci-Fi", "Mystery", "Romance"),
                listOf("Tea", "Coffee", "Juice")
            ),
            clues = listOf(
                "1. Ana likes mystery.",
                "2. The sci-fi fan drinks tea.",
                "3. Leo drinks juice.",
                "4. Zoe does not like romance."
            ),
            solution = listOf(listOf(0, 1, 1), listOf(1, 2, 2), listOf(2, 0, 0))
        ),
        LogicGridPuzzle(
            title = "Sports and Schools",
            description = "Three athletes attend different schools and play different sports.",
            categories = listOf("Athlete", "Sport", "School"),
            items = listOf(
                listOf("Mark", "Lily", "Sam"),
                listOf("Soccer", "Basketball", "Tennis"),
                listOf("West", "East", "North")
            ),
            clues = listOf(
                "1. Mark plays soccer.",
                "2. The tennis player is from West.",
                "3. Sam attends North.",
                "4. Lily is not from East."
            ),
            solution = listOf(listOf(0, 0, 1), listOf(1, 2, 0), listOf(2, 1, 2))
        ),
        LogicGridPuzzle(
            title = "Music and Genres",
            description = "Three musicians each play an instrument and prefer a genre.",
            categories = listOf("Musician", "Instrument", "Genre"),
            items = listOf(
                listOf("Eve", "Max", "Ivy"),
                listOf("Piano", "Guitar", "Drums"),
                listOf("Jazz", "Rock", "Pop")
            ),
            clues = listOf(
                "1. Eve plays piano.",
                "2. The drummer plays rock.",
                "3. Ivy does not play jazz.",
                "4. Max does not play guitar."
            ),
            solution = listOf(listOf(0, 0, 0), listOf(1, 2, 1), listOf(2, 1, 2))
        ),
        LogicGridPuzzle(
            title = "Travel Days",
            description = "Three tourists visit different countries on different days.",
            categories = listOf("Tourist", "Country", "Day"),
            items = listOf(
                listOf("Anna", "Karl", "Rita"),
                listOf("France", "Italy", "Spain"),
                listOf("Monday", "Wednesday", "Friday")
            ),
            clues = listOf(
                "1. Anna travels on Monday.",
                "2. The France traveler goes on Wednesday.",
                "3. Rita visits Spain.",
                "4. Karl does not visit Italy."
            ),
            solution = listOf(listOf(0, 1, 0), listOf(1, 0, 1), listOf(2, 2, 2))
        ),
        LogicGridPuzzle(
            title = "Pets and Names",
            description = "Three owners each have a pet with a unique name.",
            categories = listOf("Owner", "Pet", "Name"),
            items = listOf(
                listOf("Owen", "Nora", "Theo"),
                listOf("Hamster", "Parrot", "Rabbit"),
                listOf("Bubbles", "Ziggy", "Hop")
            ),
            clues = listOf(
                "1. Owen owns the parrot.",
                "2. The rabbit is named Hop.",
                "3. Nora does not have a rabbit.",
                "4. Bubbles is the hamster's name."
            ),
            solution = listOf(listOf(0, 1, 1), listOf(1, 0, 0), listOf(2, 2, 2))
        ),
        LogicGridPuzzle(
            title = "Lunch Orders",
            description = "Three diners each order a meal and a drink.",
            categories = listOf("Diner", "Meal", "Drink"),
            items = listOf(
                listOf("Pip", "Quin", "Reed"),
                listOf("Pasta", "Salad", "Pizza"),
                listOf("Water", "Soda", "Tea")
            ),
            clues = listOf(
                "1. Pip orders pizza.",
                "2. The pasta eater drinks water.",
                "3. Quin orders salad.",
                "4. The salad eater drinks tea."
            ),
            solution = listOf(listOf(0, 2, 1), listOf(1, 1, 2), listOf(2, 0, 0))
        )
    )

    fun get(difficulty: Int, index: Int): LogicGridPuzzle {
        return PUZZLES[index.coerceIn(0, PUZZLES.size - 1)]
    }
}
