# Logic Puzzles

Logic Puzzles is a native Android puzzle collection written in Kotlin. It currently includes 10 classic logic games, five difficulty levels, local progress tracking, unlockable challenges, configurable color themes, and built-in solution validation.

The app is designed to work locally on the device. Puzzle progress and settings are stored using Android preferences, and the application itself does not request internet access.

## Games

### Nonogram

Use the numeric clues beside each row and column to determine which cells must be filled.

- Each clue describes consecutive groups of filled cells.
- Separate clue groups must have at least one empty cell between them.
- **Tap** a cell to fill or clear it.
- **Long-press** a cell to add or remove an X mark.
- The board supports zooming from **75% to 250%**.
- The puzzle completes automatically when the filled cells match the solution.

### Mastermind

Crack a hidden sequence of colors using feedback from previous guesses.

- Select a slot, then choose a color.
- Submit a complete row to receive feedback.
- **●** means the correct color is in the correct position.
- **○** means the correct color is present but in the wrong position.
- Level settings can vary the number of positions, available colors, allowed guesses, and whether duplicate colors are allowed.
- Solve the code before the available guesses run out.

### Lights Out

Turn every light off by toggling connected cells.

- Tapping a cell toggles that cell plus its orthogonal neighbors: up, down, left, and right.
- The app tracks the number of moves used.
- A reset button restores the original board state.
- The level is solved when every light is off.

### Kakuro

Fill white cells with digits so every horizontal and vertical run matches its clue sum.

- Digits range from **1 to 9**.
- Digits cannot repeat inside the same run.
- Select a white cell and enter a digit using the number pad.
- Clue cells contain horizontal and/or vertical target sums.
- **Check** validates that all cells are filled and every run has the correct sum with no duplicates.

### Logic Grid

Use written clues to determine the correct relationships between multiple categories.

- Each category contains a set of items that must be matched correctly.
- Tap a grid cell to cycle through **blank → ✓ yes → ✗ no**.
- Separate pair grids show the relationships between categories.
- A clue card provides the statements needed to deduce the unique solution.
- **Check Solution** verifies the required positive matches.
- **Reset** clears all marks.

### Slitherlink

Draw one continuous closed loop around the grid while satisfying the numeric clues.

- Tap an edge to toggle it on or off.
- A number inside a cell indicates exactly how many of its four surrounding edges belong to the loop.
- The final result must be one connected loop with no branches and no separate loops.
- The board supports zoom controls for larger puzzles.
- **Check** validates clue counts and loop connectivity.

### Nurikabe

Separate numbered white islands with one connected shaded river.

- Tap non-numbered cells to shade or unshade them.
- Each numbered cell belongs to exactly one white island.
- The island size must exactly match its number.
- Two numbered islands cannot share the same white region.
- All shaded cells must form one connected river.
- A **2×2 block of shaded cells is not allowed**.
- **Check** validates all Nurikabe rules.

### Hidato

Fill the board with consecutive numbers so every number touches the next one.

- Fill the available cells with the complete sequence from **1 to the puzzle maximum**.
- Consecutive numbers may touch horizontally, vertically, or diagonally.
- Some numbers are fixed as starting clues.
- Blocked cells are not part of the path.
- Select a cell and enter numbers with the on-screen keypad.
- Backspace edits the current value and the confirm button clears the current selection.
- **Check** verifies that every number is used once and each consecutive pair is adjacent.

### Futoshiki

Fill a Latin-square-style grid while obeying inequality signs between neighboring cells.

- Every row and column must contain each number from **1 to N** exactly once.
- **<** and **>** constraints indicate which neighboring value must be smaller or larger.
- Vertical inequalities are displayed between rows.
- Select an editable cell and enter a value from the number pad.
- **Check** validates completeness, row/column uniqueness, and all inequalities.

### Skyscraper

Place building heights in the grid while matching visibility clues around the outside.

- Heights can use an extra value above the board size, so rows and columns do not simply contain **1 to N**.
- The tallest value appears once in each row and column.
- Filled heights cannot repeat within the same row or column.
- Harder boards may include one empty lot per row and column.
- Taller buildings hide shorter buildings behind them.
- Outside clues tell you how many buildings are visible when looking from that direction.
- Select an editable cell and enter a height using the number pad.
- **Check** validates empty lots, repeated heights, and every active visibility clue.

## Core functionality

### Five difficulty levels

Every puzzle type is divided into:

1. Easy
2. Medium
3. Hard
4. Expert
5. Master

The current level counts are:

| Difficulty | Levels per game |
| --- | ---: |
| Easy | 15 |
| Medium | 25 |
| Hard | 35 |
| Expert | 45 |
| Master | 55 |
| **Total** | **175** |

With 10 puzzle types, the progression system exposes **1,750 playable levels** in total.

### Progressive unlocking

Levels unlock sequentially inside each difficulty. Completing a level unlocks the next level in that group.

The first three difficulties are available immediately. Higher difficulties require additional progress:

- **Expert** unlocks after completing at least **10 Hard** puzzles for that game.
- **Master** unlocks after completing at least **10 Expert** puzzles for that game.

Completed levels are shown with a star in the level selector.

### Local progress tracking

Progress is stored locally with Android `SharedPreferences`.

The app tracks:

- completed puzzle type
- difficulty
- level number
- selected color theme
- shuffled catalog order after a progress reset

No account or cloud connection is required for normal gameplay.

### Reset and reshuffle

From Settings, **Reset progress and shuffle levels** clears completed-puzzle progress while keeping the selected color theme.

The reset also changes the displayed puzzle order within small sub-difficulty blocks, providing a different progression order without changing the underlying puzzle catalog.

### Themes and accessibility

The app supports selectable color palettes through Settings, including separate groups for:

- normal themes
- colorblind-oriented themes

Puzzle screens use the active palette for backgrounds, text, cells, clues, status colors, buttons, and puzzle-specific accents.

### Built-in validation

Each game contains its own rule validator rather than relying only on a stored answer screen. Examples include:

- duplicate and sum checking in Kakuro
- loop degree/connectivity checks in Slitherlink
- island and river validation in Nurikabe
- adjacency validation in Hidato
- inequality validation in Futoshiki
- visibility calculations in Skyscraper

Solved puzzles are marked as completed and passed to the shared completion flow.

### Responsive puzzle boards

Boards calculate cell sizes from the device display and several larger-grid games provide scrolling or zoom controls. System-bar insets are also applied to the main screen, puzzle menu, and game screens.

### Puzzle verification utility

For development/testing, long-pressing the **Settings** button on the main screen starts the internal `PuzzleVerifier` on a background thread. Verification results are written to Logcat using the `PuzzleVerifier` tag.

## Technical overview

- **Platform:** Android
- **Language:** Kotlin
- **Namespace / application ID:** `com.cyberhub.logicgames`
- **Minimum Android SDK:** 23
- **Compile SDK:** 36
- **Target SDK:** 36
- **Java compatibility:** Java 17
- **UI:** Android Views, `LinearLayout`, `GridLayout`, `CardView`, and custom programmatic puzzle boards
- **Persistence:** Android `SharedPreferences`
- **Dependencies:** AndroidX Core KTX, AppCompat, and CardView

## Project structure

```text
app/src/main/java/com/logicpuzzles/
├── MainActivity.kt
├── PuzzleMenuActivity.kt
├── futoshiki/
├── hidato/
├── kakuro/
├── lightsout/
├── logicgrid/
├── mastermind/
├── nonogram/
├── nurikabe/
├── skyscraper/
├── slitherlink/
└── utils/
```

Each puzzle package contains the game screen and its puzzle data/generator logic. Shared progress, theming, completion handling, formatting, verification, and UI helpers live under the shared utility layer.

## Development

Open the project in a recent version of Android Studio with JDK 17 support, sync Gradle, and run the `app` configuration on an emulator or Android device running API 23 or newer.

For release builds, review signing, versioning, minification, testing, and store metadata before publishing.
