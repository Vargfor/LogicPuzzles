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

    // EASY: 3 categories x 3 items, many direct clues
    private val EASY = listOf(
        LogicGridPuzzle(
            title = "Pets and Houses",
            description = "Three friends each own one pet and live in a house of one color.",
            categories = listOf("Person", "Pet", "Color"),
            items = listOf(listOf("Alice","Bob","Carol"), listOf("Cat","Dog","Fish"), listOf("Red","Blue","Green")),
            clues = listOf("1. Alice does not own the dog.","2. The cat owner lives in the blue house.","3. Bob lives in the red house.","4. Carol owns the fish."),
            solution = listOf(listOf(0,0,1), listOf(1,1,0), listOf(2,2,2))
        ),
        LogicGridPuzzle(
            title = "Jobs and Hobbies",
            description = "Three neighbors with different jobs and hobbies.",
            categories = listOf("Person", "Job", "Hobby"),
            items = listOf(listOf("Emma","Jack","Lisa"), listOf("Teacher","Doctor","Artist"), listOf("Reading","Painting","Gaming")),
            clues = listOf("1. Emma is not the doctor.","2. The artist loves painting.","3. Jack is the teacher.","4. Lisa does not read."),
            solution = listOf(listOf(0,2,1), listOf(1,0,0), listOf(2,1,2))
        ),
        LogicGridPuzzle(
            title = "Fruits and Sports",
            description = "Three kids each have one favorite fruit and one sport.",
            categories = listOf("Kid", "Fruit", "Sport"),
            items = listOf(listOf("Tom","Sara","Ben"), listOf("Apple","Banana","Cherry"), listOf("Football","Tennis","Swimming")),
            clues = listOf("1. Tom likes football.","2. The cherry fan plays tennis.","3. Sara does not like apples.","4. Ben swims."),
            solution = listOf(listOf(0,0,0), listOf(1,2,1), listOf(2,1,2))
        ),
        LogicGridPuzzle(
            title = "Cars and Cities",
            description = "Three drivers each own a car and live in a city.",
            categories = listOf("Driver", "Car", "City"),
            items = listOf(listOf("Mia","Noah","Olive"), listOf("Sedan","SUV","Coupe"), listOf("Paris","Rome","Madrid")),
            clues = listOf("1. Mia drives a coupe.","2. The sedan driver lives in Rome.","3. Noah lives in Paris.","4. Olive does not live in Madrid."),
            solution = listOf(listOf(0,2,2), listOf(1,1,0), listOf(2,0,1))
        ),
        LogicGridPuzzle(
            title = "Books and Drinks",
            description = "Three readers each like one book genre and one drink.",
            categories = listOf("Reader", "Genre", "Drink"),
            items = listOf(listOf("Ana","Leo","Zoe"), listOf("Sci-Fi","Mystery","Romance"), listOf("Tea","Coffee","Juice")),
            clues = listOf("1. Ana likes mystery.","2. The sci-fi fan drinks tea.","3. Leo drinks juice.","4. Zoe does not like romance."),
            solution = listOf(listOf(0,1,1), listOf(1,2,2), listOf(2,0,0))
        ),
        LogicGridPuzzle(
            title = "Sports and Schools",
            description = "Three athletes attend different schools and play different sports.",
            categories = listOf("Athlete", "Sport", "School"),
            items = listOf(listOf("Mark","Lily","Sam"), listOf("Soccer","Basketball","Tennis"), listOf("West","East","North")),
            clues = listOf("1. Mark plays soccer.","2. The tennis player is from West.","3. Sam attends North.","4. Lily is not from East."),
            solution = listOf(listOf(0,0,1), listOf(1,2,0), listOf(2,1,2))
        ),
        LogicGridPuzzle(
            title = "Music and Genres",
            description = "Three musicians each play an instrument and prefer a genre.",
            categories = listOf("Musician", "Instrument", "Genre"),
            items = listOf(listOf("Eve","Max","Ivy"), listOf("Piano","Guitar","Drums"), listOf("Jazz","Rock","Pop")),
            clues = listOf("1. Eve plays piano.","2. The drummer plays rock.","3. Ivy does not play jazz.","4. Max does not play guitar."),
            solution = listOf(listOf(0,0,0), listOf(1,2,1), listOf(2,1,2))
        ),
        LogicGridPuzzle(
            title = "Travel Days",
            description = "Three tourists visit different countries on different days.",
            categories = listOf("Tourist", "Country", "Day"),
            items = listOf(listOf("Anna","Karl","Rita"), listOf("France","Italy","Spain"), listOf("Monday","Wednesday","Friday")),
            clues = listOf("1. Anna travels on Monday.","2. The France traveler goes on Wednesday.","3. Rita visits Spain.","4. Karl does not visit Italy."),
            solution = listOf(listOf(0,1,0), listOf(1,0,1), listOf(2,2,2))
        ),
        LogicGridPuzzle(
            title = "Pets and Names",
            description = "Three owners each have a pet with a unique name.",
            categories = listOf("Owner", "Pet", "Name"),
            items = listOf(listOf("Owen","Nora","Theo"), listOf("Hamster","Parrot","Rabbit"), listOf("Bubbles","Ziggy","Hop")),
            clues = listOf("1. Owen owns the parrot.","2. The rabbit is named Hop.","3. Nora does not have a rabbit.","4. Bubbles is the hamster's name."),
            solution = listOf(listOf(0,1,1), listOf(1,0,0), listOf(2,2,2))
        ),
        LogicGridPuzzle(
            title = "Lunch Orders",
            description = "Three diners each order a meal and a drink.",
            categories = listOf("Diner", "Meal", "Drink"),
            items = listOf(listOf("Pip","Quin","Reed"), listOf("Pasta","Salad","Pizza"), listOf("Water","Soda","Tea")),
            clues = listOf("1. Pip orders pizza.","2. The pasta eater drinks water.","3. Quin orders salad.","4. The salad eater drinks tea."),
            solution = listOf(listOf(0,2,1), listOf(1,1,2), listOf(2,0,0))
        ),
        // Levels 11-15: 3x3, slightly more inference
        LogicGridPuzzle(
            title = "Flowers and Months",
            description = "Three friends each have a favorite flower and birth month.",
            categories = listOf("Person", "Flower", "Month"),
            items = listOf(listOf("Dan","Eve","Fay"), listOf("Rose","Lily","Daisy"), listOf("Jan","Mar","Jun")),
            clues = listOf("1. Dan does not like roses.","2. The rose lover was born in June.","3. Eve was born in January.","4. Fay does not like daisy.","5. The lily lover is not Eve."),
            solution = listOf(listOf(0,1,1), listOf(1,2,0), listOf(2,0,2))
        ),
        LogicGridPuzzle(
            title = "Animals and Colors",
            description = "Three kids each have a favorite animal and color.",
            categories = listOf("Kid", "Animal", "Color"),
            items = listOf(listOf("Gus","Hana","Ian"), listOf("Bear","Fox","Wolf"), listOf("Red","Blue","Yellow")),
            clues = listOf("1. The bear fan likes yellow.","2. The fox owner likes blue.","3. Hana's favorite color is red.","4. Ian does not have a fox.","5. Hana does not have a bear."),
            solution = listOf(listOf(0,1,1), listOf(1,2,0), listOf(2,0,2))
        ),
        LogicGridPuzzle(
            title = "Foods and Days",
            description = "Three friends each have a favorite food and day of the week.",
            categories = listOf("Person", "Food", "Day"),
            items = listOf(listOf("Jo","Kim","Liz"), listOf("Pizza","Sushi","Tacos"), listOf("Mon","Wed","Fri")),
            clues = listOf("1. Jo eats pizza.","2. The sushi eater eats on Friday.","3. Kim eats on Monday.","4. Liz does not eat tacos."),
            solution = listOf(listOf(0,0,1), listOf(1,2,0), listOf(2,1,2))
        ),
        LogicGridPuzzle(
            title = "Colors and Hobbies",
            description = "Three friends each have a favorite color and hobby.",
            categories = listOf("Person", "Color", "Hobby"),
            items = listOf(listOf("Meg","Nick","Olga"), listOf("Red","Blue","Green"), listOf("Cooking","Dancing","Hiking")),
            clues = listOf("1. Nick's favorite color is red.","2. The blue lover hikes.","3. Olga dances.","4. Meg does not cook."),
            solution = listOf(listOf(0,1,2), listOf(1,0,0), listOf(2,2,1))
        ),
        LogicGridPuzzle(
            title = "Sports and Schools",
            description = "Three students each play a sport and attend a school.",
            categories = listOf("Student", "Sport", "School"),
            items = listOf(listOf("Pat","Quinn","Rosa"), listOf("Cricket","Hockey","Rugby"), listOf("Alpha","Beta","Gamma")),
            clues = listOf("1. The hockey player goes to Beta.","2. The cricket player goes to Gamma.","3. Quinn goes to Alpha.","4. Rosa does not play hockey."),
            solution = listOf(listOf(0,1,1), listOf(1,2,0), listOf(2,0,2))
        )
    )

    // MEDIUM: 3 categories x 3 items, moderate inference chains
    private val MEDIUM = listOf(
        LogicGridPuzzle(
            title = "Gems and Years",
            description = "Three collectors each found a gem in a different year.",
            categories = listOf("Person", "Gem", "Year"),
            items = listOf(listOf("Amy","Ben","Cara"), listOf("Ruby","Pearl","Jade"), listOf("2020","2021","2022")),
            clues = listOf("1. Amy found her gem in 2021.","2. The ruby was found in 2022.","3. Ben found his gem in 2020.","4. Cara's gem is not jade.","5. Ben's gem is not pearl."),
            solution = listOf(listOf(0,1,1), listOf(1,2,0), listOf(2,0,2))
        ),
        LogicGridPuzzle(
            title = "Vehicles and Days",
            description = "Three people each prefer a vehicle and travel on a certain day.",
            categories = listOf("Person", "Vehicle", "Day"),
            items = listOf(listOf("Dan","Eve","Fay"), listOf("Car","Bike","Bus"), listOf("Mon","Wed","Fri")),
            clues = listOf("1. Eve travels on Monday.","2. The bus rider travels on Friday.","3. Fay does not ride a car.","4. Dan does not take the bike."),
            solution = listOf(listOf(0,2,2), listOf(1,0,0), listOf(2,1,1))
        ),
        LogicGridPuzzle(
            title = "Languages and Sports",
            description = "Three students each study a language and play a sport.",
            categories = listOf("Person", "Language", "Sport"),
            items = listOf(listOf("Gus","Hana","Ian"), listOf("French","German","Spanish"), listOf("Football","Judo","Swimming")),
            clues = listOf("1. Gus does not study French.","2. The German student practices judo.","3. Ian studies French.","4. Hana does not practice judo."),
            solution = listOf(listOf(0,1,1), listOf(1,2,2), listOf(2,0,0))
        ),
        LogicGridPuzzle(
            title = "Trees and Parks",
            description = "Three hikers each like a tree and visit a park.",
            categories = listOf("Person", "Tree", "Park"),
            items = listOf(listOf("Jo","Kim","Liz"), listOf("Oak","Pine","Elm"), listOf("North","South","East")),
            clues = listOf("1. Jo's favorite tree is not oak.","2. The elm lover visits East Park.","3. Kim visits North Park.","4. Liz's tree is not elm."),
            solution = listOf(listOf(0,2,2), listOf(1,0,0), listOf(2,1,1))
        ),
        LogicGridPuzzle(
            title = "Drinks and Glasses",
            description = "Three friends each prefer a drink and a glass type.",
            categories = listOf("Person", "Drink", "Glass"),
            items = listOf(listOf("Meg","Nick","Olga"), listOf("Cola","Tea","Milk"), listOf("Tall","Short","Wide")),
            clues = listOf("1. Nick prefers cola.","2. The milk drinker uses a wide glass.","3. Olga does not use a tall glass.","4. Meg does not drink cola."),
            solution = listOf(listOf(0,2,2), listOf(1,0,0), listOf(2,1,1))
        ),
        LogicGridPuzzle(
            title = "Planets and Missions",
            description = "Three researchers each study a planet and have a mission number.",
            categories = listOf("Person", "Planet", "Mission"),
            items = listOf(listOf("Pat","Quinn","Rosa"), listOf("Mars","Venus","Jupiter"), listOf("One","Two","Three")),
            clues = listOf("1. Pat's planet is not Mars.","2. The Mars mission is number three.","3. Quinn's mission is number one.","4. Rosa does not study Jupiter."),
            solution = listOf(listOf(0,1,1), listOf(1,2,0), listOf(2,0,2))
        ),
        LogicGridPuzzle(
            title = "Hats and Clubs",
            description = "Three students each wear a hat and belong to a club.",
            categories = listOf("Person", "Hat", "Club"),
            items = listOf(listOf("Sam","Tina","Uma"), listOf("Cap","Beret","Fedora"), listOf("Art","Music","Sports")),
            clues = listOf("1. Sam does not wear a beret.","2. The fedora wearer joins Sports Club.","3. Tina joins Art Club.","4. Uma does not wear a cap."),
            solution = listOf(listOf(0,2,2), listOf(1,0,0), listOf(2,1,1))
        ),
        LogicGridPuzzle(
            title = "Cakes and Parties",
            description = "Three friends each like a cake flavor and have a party day.",
            categories = listOf("Person", "Cake", "Day"),
            items = listOf(listOf("Val","Will","Xena"), listOf("Chocolate","Vanilla","Lemon"), listOf("Fri","Sat","Sun")),
            clues = listOf("1. Val's cake is not chocolate.","2. The lemon cake party is on Friday.","3. Xena does not like vanilla.","4. Will does not have a Sunday party.","5. Will does not like chocolate."),
            solution = listOf(listOf(0,1,1), listOf(1,2,0), listOf(2,0,2))
        ),
        LogicGridPuzzle(
            title = "Stones and Rooms",
            description = "Three decorators each use a stone type and work on a room.",
            categories = listOf("Person", "Stone", "Room"),
            items = listOf(listOf("Yara","Zack","Adna"), listOf("Granite","Marble","Slate"), listOf("Kitchen","Living","Bedroom")),
            clues = listOf("1. Yara does not use granite.","2. The slate floor goes in the kitchen.","3. Zack does not use marble.","4. Adna's room is not the living room."),
            solution = listOf(listOf(0,1,1), listOf(1,2,0), listOf(2,0,2))
        ),
        LogicGridPuzzle(
            title = "Birds and Times",
            description = "Three birdwatchers each spot a bird at a different time.",
            categories = listOf("Person", "Bird", "Time"),
            items = listOf(listOf("Bea","Carl","Dora"), listOf("Eagle","Hawk","Sparrow"), listOf("AM","Noon","PM")),
            clues = listOf("1. Carl spots eagles in the morning.","2. The hawk watcher goes at noon.","3. Bea does not go in the morning.","4. Dora does not watch sparrows."),
            solution = listOf(listOf(0,2,2), listOf(1,0,0), listOf(2,1,1))
        ),
        LogicGridPuzzle(
            title = "Books and Shelves",
            description = "Three readers each like a book type and use a certain shelf.",
            categories = listOf("Person", "Book", "Shelf"),
            items = listOf(listOf("Ed","Fay","Gus"), listOf("Novel","Poem","Play"), listOf("Top","Mid","Bottom")),
            clues = listOf("1. Ed's book is not a novel.","2. The play is on the top shelf.","3. Fay does not read poems.","4. Gus's book is not on the bottom shelf."),
            solution = listOf(listOf(0,2,0), listOf(1,0,2), listOf(2,1,1))
        ),
        LogicGridPuzzle(
            title = "Clouds and Heights",
            description = "Three meteorologists each study a cloud type at a certain altitude.",
            categories = listOf("Person", "Cloud", "Height"),
            items = listOf(listOf("Hana","Ian","Jo"), listOf("Cumulus","Stratus","Cirrus"), listOf("Low","Mid","High")),
            clues = listOf("1. Cirrus clouds are at high altitude.","2. Hana studies stratus clouds.","3. Jo does not study high-altitude clouds.","4. Stratus clouds are at low altitude."),
            solution = listOf(listOf(0,1,0), listOf(1,2,2), listOf(2,0,1))
        ),
        LogicGridPuzzle(
            title = "Coins and Values",
            description = "Three collectors each own a coin type with a different value.",
            categories = listOf("Person", "Coin", "Value"),
            items = listOf(listOf("Kim","Liz","Meg"), listOf("Gold","Silver","Bronze"), listOf("1","5","10")),
            clues = listOf("1. The gold coin is worth 10.","2. Kim's coin is not bronze.","3. Liz's coin is worth 1.","4. Meg does not own silver."),
            solution = listOf(listOf(0,1,1), listOf(1,2,0), listOf(2,0,2))
        ),
        LogicGridPuzzle(
            title = "Lenses and Uses",
            description = "Three photographers each use a lens type for a different purpose.",
            categories = listOf("Person", "Lens", "Use"),
            items = listOf(listOf("Nick","Olga","Pat"), listOf("Wide","Normal","Tele"), listOf("Photo","Film","Art")),
            clues = listOf("1. The telephoto lens is used for art.","2. Pat uses the wide-angle lens.","3. Nick does not use the wide-angle lens.","4. Olga's work is not photography."),
            solution = listOf(listOf(0,1,1), listOf(1,2,2), listOf(2,0,0))
        ),
        LogicGridPuzzle(
            title = "Doors and Locks",
            description = "Three security guards each manage a door type with a lock type.",
            categories = listOf("Person", "Door", "Lock"),
            items = listOf(listOf("Quinn","Rosa","Sam"), listOf("Wood","Metal","Glass"), listOf("Key","Code","Card")),
            clues = listOf("1. The glass door uses a code lock.","2. Rosa's door is not metal.","3. Sam does not use a key lock.","4. Quinn's door is not made of wood."),
            solution = listOf(listOf(0,2,1), listOf(1,0,0), listOf(2,1,2))
        )
    )

    // HARD: 4 categories x 3 items, requires more deductive steps
    private val HARD = listOf(
        LogicGridPuzzle(
            title = "Pets, Colors, and Cities",
            description = "Three friends each have a pet, favorite color, and live in a city.",
            categories = listOf("Person", "Pet", "Color", "City"),
            items = listOf(listOf("Al","Beth","Cy"), listOf("Cat","Dog","Fish"), listOf("Red","Blue","Green"), listOf("NY","LA","Chicago")),
            clues = listOf("1. The cat owner likes red.","2. The dog owner lives in New York.","3. Cy's favorite color is green.","4. Beth does not live in Los Angeles.","5. Al does not live in Los Angeles.","6. Beth does not own a dog."),
            solution = listOf(listOf(0,1,1,0), listOf(1,0,0,2), listOf(2,2,2,1))
        ),
        LogicGridPuzzle(
            title = "Sports, Seasons, and Countries",
            description = "Three athletes each play a sport, travel in a season, and visit a country.",
            categories = listOf("Person", "Sport", "Season", "Country"),
            items = listOf(listOf("Dan","Eve","Fay"), listOf("Tennis","Golf","Swim"), listOf("Spring","Summer","Autumn"), listOf("UK","USA","AUS")),
            clues = listOf("1. Dan plays golf.","2. The swimmer travels in spring.","3. Fay visits UK.","4. The golf player goes in autumn.","5. Eve does not visit UK.","6. Eve visits Australia."),
            solution = listOf(listOf(0,1,2,1), listOf(1,2,0,2), listOf(2,0,1,0))
        ),
        LogicGridPuzzle(
            title = "Gems, Shapes, and Numbers",
            description = "Three collectors each have a gem with a shape and a lucky number.",
            categories = listOf("Person", "Gem", "Shape", "Number"),
            items = listOf(listOf("Gus","Hana","Ian"), listOf("Ruby","Opal","Zircon"), listOf("Round","Square","Oval"), listOf("One","Two","Three")),
            clues = listOf("1. Ian found the ruby.","2. The ruby is round-shaped.","3. Hana's number is one.","4. The oval gem is number one.","5. Hana does not own opal.","6. Ian's number is three."),
            solution = listOf(listOf(0,1,1,1), listOf(1,2,2,0), listOf(2,0,0,2))
        ),
        LogicGridPuzzle(
            title = "Flowers, Days, and Rooms",
            description = "Three gardeners each grow a flower, water on a day, and have a favorite room.",
            categories = listOf("Person", "Flower", "Day", "Room"),
            items = listOf(listOf("Jo","Kim","Liz"), listOf("Rose","Tulip","Daisy"), listOf("Mon","Wed","Fri"), listOf("Garden","Kitchen","Patio")),
            clues = listOf("1. Jo grows daisy.","2. The rose grower waters on Wednesday.","3. Kim waters on Monday.","4. The daisy grower prefers the patio.","5. Liz does not grow tulip.","6. Kim's favorite room is the garden."),
            solution = listOf(listOf(0,2,2,2), listOf(1,0,0,0), listOf(2,1,1,1))
        ),
        LogicGridPuzzle(
            title = "Books, Times, and Seats",
            description = "Three readers each like a book type, read at a certain time, and sit in a seat.",
            categories = listOf("Person", "Book", "Time", "Seat"),
            items = listOf(listOf("Meg","Nick","Olga"), listOf("Novel","Comic","Poetry"), listOf("AM","Noon","PM"), listOf("Chair","Sofa","Floor")),
            clues = listOf("1. Meg reads novels.","2. The comic reader reads at noon.","3. Olga reads in the afternoon.","4. The novel reader sits in a chair.","5. Nick does not sit on the sofa.","6. Olga does not read comics."),
            solution = listOf(listOf(0,0,0,0), listOf(1,1,1,2), listOf(2,2,2,1))
        ),
        LogicGridPuzzle(
            title = "Cars, Routes, and Stops",
            description = "Three drivers each own a car type, take a route, and make a stop.",
            categories = listOf("Person", "Car", "Route", "Stop"),
            items = listOf(listOf("Pat","Quinn","Rosa"), listOf("Sedan","SUV","Van"), listOf("North","South","East"), listOf("Mall","Park","Station")),
            clues = listOf("1. Pat drives a sedan.","2. The SUV takes the south route.","3. Rosa stops at the station.","4. The sedan takes the north route.","5. Quinn does not stop at the park.","6. Pat does not stop at the mall."),
            solution = listOf(listOf(0,0,0,1), listOf(1,1,1,0), listOf(2,2,2,2))
        ),
        LogicGridPuzzle(
            title = "Jobs, Floors, and Meals",
            description = "Three workers have different jobs, work on different floors, and eat different meals.",
            categories = listOf("Person", "Job", "Floor", "Meal"),
            items = listOf(listOf("Sam","Tina","Uma"), listOf("Chef","Nurse","Guard"), listOf("First","Second","Third"), listOf("Salad","Soup","Sandwich")),
            clues = listOf("1. Sam is the chef.","2. The nurse works on the second floor.","3. Uma eats salad.","4. The chef works on the first floor.","5. Tina does not eat soup.","6. Sam does not eat sandwich."),
            solution = listOf(listOf(0,0,0,0), listOf(1,1,1,2), listOf(2,2,2,1))
        ),
        LogicGridPuzzle(
            title = "Animals, Sizes, and Colors",
            description = "Three zookeepers each care for an animal of a certain size and color.",
            categories = listOf("Person", "Animal", "Size", "Color"),
            items = listOf(listOf("Val","Will","Xena"), listOf("Lion","Bear","Fox"), listOf("Large","Medium","Small"), listOf("Brown","White","Orange")),
            clues = listOf("1. Val cares for the fox.","2. The lion is large.","3. Xena's animal is white.","4. The fox is small.","5. Will's animal is not orange.","6. Will does not care for the bear."),
            solution = listOf(listOf(0,2,2,2), listOf(1,0,0,0), listOf(2,1,1,1))
        ),
        LogicGridPuzzle(
            title = "Planets, Distances, and Moons",
            description = "Three astronomers each study a planet with different distance and moon count.",
            categories = listOf("Person", "Planet", "Distance", "Moons"),
            items = listOf(listOf("Yara","Zack","Adna"), listOf("Mars","Saturn","Neptune"), listOf("Near","Mid","Far"), listOf("Two","Many","One")),
            clues = listOf("1. Yara studies Mars.","2. Mars is the nearest planet.","3. Saturn has many moons.","4. Zack's planet is not Saturn.","5. Adna studies a far planet.","6. Zack's planet is at medium distance."),
            solution = listOf(listOf(0,0,0,0), listOf(1,2,1,2), listOf(2,1,2,1))
        ),
        LogicGridPuzzle(
            title = "Sports, Teams, and Scores",
            description = "Three players each play a sport, are on a team, and have a score.",
            categories = listOf("Person", "Sport", "Team", "Score"),
            items = listOf(listOf("Bea","Carl","Dora"), listOf("Soccer","Tennis","Golf"), listOf("Red","Blue","Green"), listOf("Low","Mid","High")),
            clues = listOf("1. Bea plays soccer.","2. The tennis player is on the Blue team.","3. Dora has a high score.","4. The soccer player has a mid score.","5. Carl is not on the Green team.","6. Carl does not play golf."),
            solution = listOf(listOf(0,0,0,1), listOf(1,1,1,0), listOf(2,2,2,2))
        ),
        LogicGridPuzzle(
            title = "Music, Venues, and Times",
            description = "Three musicians play a genre, perform at a venue, and play at a certain time.",
            categories = listOf("Person", "Genre", "Venue", "Time"),
            items = listOf(listOf("Ed","Fay","Gus"), listOf("Jazz","Rock","Classical"), listOf("Club","Hall","Park"), listOf("Eve","Night","Midday")),
            clues = listOf("1. Ed plays jazz.","2. Jazz is performed at the club.","3. Fay performs at midday.","4. Rock is performed at night.","5. Gus does not perform at the park.","6. Classical is performed in the hall."),
            solution = listOf(listOf(0,0,0,0), listOf(1,2,2,1), listOf(2,1,1,2))
        ),
        LogicGridPuzzle(
            title = "Foods, Tables, and Sizes",
            description = "Three chefs each prepare a food, use a table, and serve a portion size.",
            categories = listOf("Person", "Food", "Table", "Size"),
            items = listOf(listOf("Hana","Ian","Jo"), listOf("Pasta","Pizza","Salad"), listOf("Round","Square","Long"), listOf("Small","Medium","Large")),
            clues = listOf("1. Hana prepares pasta.","2. The pasta chef uses a round table.","3. Jo serves large portions.","4. The pizza chef uses a square table.","5. Ian does not serve small portions.","6. Jo does not prepare pizza."),
            solution = listOf(listOf(0,0,0,0), listOf(1,1,1,1), listOf(2,2,2,2))
        ),
        LogicGridPuzzle(
            title = "Hobbies, Days, and Weather",
            description = "Three hobbyists each have a hobby, do it on a day, and in certain weather.",
            categories = listOf("Person", "Hobby", "Day", "Weather"),
            items = listOf(listOf("Kim","Liz","Meg"), listOf("Painting","Running","Fishing"), listOf("Sat","Sun","Mon"), listOf("Sunny","Cloudy","Rainy")),
            clues = listOf("1. Kim goes fishing.","2. Fishing is done on Monday.","3. Liz does her hobby on Sunday.","4. Running needs sunny weather.","5. Meg does not run.","6. Liz does not like rainy weather."),
            solution = listOf(listOf(0,2,2,2), listOf(1,0,1,0), listOf(2,1,0,1))
        ),
        LogicGridPuzzle(
            title = "Schools, Clubs, and Trips",
            description = "Three students each attend a school, join a club, and go on a trip.",
            categories = listOf("Person", "School", "Club", "Trip"),
            items = listOf(listOf("Nick","Olga","Pat"), listOf("West","East","North"), listOf("Drama","Science","Art"), listOf("Museum","Beach","Mountain")),
            clues = listOf("1. Nick attends West School.","2. The drama club goes to the museum.","3. Olga joins the art club.","4. West School students join drama club.","5. Pat does not go to the beach.","6. Olga's trip is not to the mountain."),
            solution = listOf(listOf(0,0,0,0), listOf(1,2,2,1), listOf(2,1,1,2))
        ),
        LogicGridPuzzle(
            title = "Drinks, Snacks, and Places",
            description = "Three friends each prefer a drink, snack, and place to relax.",
            categories = listOf("Person", "Drink", "Snack", "Place"),
            items = listOf(listOf("Quinn","Rosa","Sam"), listOf("Coffee","Tea","Juice"), listOf("Chips","Nuts","Fruit"), listOf("Cafe","Park","Home")),
            clues = listOf("1. Quinn drinks coffee.","2. Coffee drinkers go to the cafe.","3. Rosa eats fruit.","4. The tea drinker goes to the park.","5. Sam does not eat chips.","6. Sam drinks juice."),
            solution = listOf(listOf(0,0,0,0), listOf(1,2,2,1), listOf(2,1,1,2))
        )
    )

    // EXPERT: 4 categories x 4 items, complex multi-step deduction
    private val EXPERT = listOf(
        LogicGridPuzzle(
            title = "Colors, Pets, and Cities",
            description = "Four friends each have a favorite color, pet, and live in a city.",
            categories = listOf("Person", "Color", "Pet", "City"),
            items = listOf(listOf("Anya","Ben","Cody","Dina"), listOf("Red","Blue","Green","Yellow"), listOf("Cat","Dog","Fish","Bird"), listOf("NY","LA","Chicago","Miami")),
            clues = listOf("1. Anya's pet is a dog.","2. The dog owner lives in Chicago.","3. Ben's favorite color is red.","4. The cat owner lives in Los Angeles.","5. Cody's pet is a bird.","6. The bird owner lives in New York.","7. Dina's color is green.","8. Ben lives in Los Angeles.","9. Cody's color is yellow."),
            solution = listOf(listOf(0,1,1,2), listOf(1,0,0,1), listOf(2,3,3,0), listOf(3,2,2,3))
        ),
        LogicGridPuzzle(
            title = "Jobs, Hours, and Transport",
            description = "Four workers have a job, work hours, and take a transport.",
            categories = listOf("Person", "Job", "Hours", "Transport"),
            items = listOf(listOf("Emil","Faye","Greg","Hope"), listOf("Doctor","Teacher","Chef","Nurse"), listOf("8h","9h","10h","12h"), listOf("Bus","Train","Bike","Car")),
            clues = listOf("1. Emil is the doctor.","2. The doctor works 12 hours.","3. Faye takes the train.","4. The teacher works 9 hours.","5. Greg is the chef.","6. The chef takes a bike.","7. Hope is the nurse.","8. The bus rider works 8 hours.","9. Faye works 10 hours."),
            solution = listOf(listOf(0,0,3,3), listOf(1,1,2,1), listOf(2,2,1,2), listOf(3,3,0,0))
        ),
        LogicGridPuzzle(
            title = "Seasons, Fruits, and Sports",
            description = "Four athletes each have a favorite season, fruit, and sport.",
            categories = listOf("Person", "Season", "Fruit", "Sport"),
            items = listOf(listOf("Iris","Jake","Kara","Leo"), listOf("Spring","Summer","Autumn","Winter"), listOf("Apple","Banana","Cherry","Date"), listOf("Tennis","Soccer","Golf","Swim")),
            clues = listOf("1. Iris loves spring.","2. The spring person plays tennis.","3. Jake's fruit is banana.","4. The summer person plays soccer.","5. Kara loves autumn.","6. The autumn person eats cherry.","7. Leo plays golf.","8. The winter person eats date.","9. Jake loves summer."),
            solution = listOf(listOf(0,0,0,0), listOf(1,1,1,1), listOf(2,2,2,3), listOf(3,3,3,2))
        ),
        LogicGridPuzzle(
            title = "Instruments, Genres, and Ages",
            description = "Four musicians play an instrument, prefer a genre, and are different ages.",
            categories = listOf("Person", "Instrument", "Genre", "Age"),
            items = listOf(listOf("Mona","Nate","Ona","Pete"), listOf("Piano","Guitar","Drums","Violin"), listOf("Jazz","Rock","Pop","Classical"), listOf("20","25","30","35")),
            clues = listOf("1. Mona plays piano.","2. The pianist plays jazz.","3. Nate is 25 years old.","4. The guitarist plays rock.","5. Ona plays violin.","6. The violinist is 35 years old.","7. Pete plays drums.","8. The drummer plays pop.","9. Mona is 20 years old."),
            solution = listOf(listOf(0,0,0,0), listOf(1,1,1,1), listOf(2,3,3,3), listOf(3,2,2,2))
        ),
        LogicGridPuzzle(
            title = "Countries, Languages, and Foods",
            description = "Four travelers visit countries, speak a language, and eat a local food.",
            categories = listOf("Person", "Country", "Language", "Food"),
            items = listOf(listOf("Quinn","Rana","Seth","Tara"), listOf("France","Japan","Mexico","India"), listOf("French","Japanese","Spanish","Hindi"), listOf("Crepe","Sushi","Taco","Curry")),
            clues = listOf("1. Quinn visits France.","2. The France visitor speaks French.","3. Rana visits Japan.","4. The Japan visitor eats sushi.","5. Seth visits Mexico.","6. The Mexico visitor speaks Spanish.","7. Tara visits India.","8. The India visitor eats curry.","9. The France visitor eats crepes."),
            solution = listOf(listOf(0,0,0,0), listOf(1,1,1,1), listOf(2,2,2,2), listOf(3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Rooms, Colors, and Furniture",
            description = "Four designers each decorate a room with a color and a furniture piece.",
            categories = listOf("Person", "Room", "Color", "Furniture"),
            items = listOf(listOf("Uma","Vera","Will","Xara"), listOf("Kitchen","Bedroom","Living","Bath"), listOf("White","Grey","Blue","Red"), listOf("Table","Bed","Sofa","Shelf")),
            clues = listOf("1. Uma decorates the kitchen.","2. The kitchen uses white.","3. Vera decorates the bedroom.","4. The bedroom is grey.","5. Will decorates the living room.","6. The living room is blue.","7. The kitchen has a table.","8. The bedroom has a bed.","9. The living room has a sofa."),
            solution = listOf(listOf(0,0,0,0), listOf(1,1,1,1), listOf(2,2,2,2), listOf(3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Animals, Habitats, and Diets",
            description = "Four zoologists each study an animal in a habitat with a diet.",
            categories = listOf("Person", "Animal", "Habitat", "Diet"),
            items = listOf(listOf("Yael","Zane","Abby","Bram"), listOf("Eagle","Shark","Tiger","Frog"), listOf("Sky","Ocean","Forest","Swamp"), listOf("Fish","Meat","Fruit","Insects")),
            clues = listOf("1. Yael studies the eagle.","2. The eagle lives in the sky.","3. Zane studies the shark.","4. The shark eats fish.","5. Abby studies the tiger.","6. The tiger lives in the forest.","7. Bram studies the frog.","8. The frog lives in the swamp.","9. The eagle eats meat."),
            solution = listOf(listOf(0,0,0,1), listOf(1,1,1,0), listOf(2,2,2,2), listOf(3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Hobbies, Days, and Venues",
            description = "Four friends each have a hobby, practice on a day, and use a venue.",
            categories = listOf("Person", "Hobby", "Day", "Venue"),
            items = listOf(listOf("Cara","Dale","Elsa","Finn"), listOf("Chess","Dance","Paint","Code"), listOf("Mon","Tue","Wed","Thu"), listOf("Club","Studio","Gallery","Cafe")),
            clues = listOf("1. Cara plays chess.","2. Chess is played at the club.","3. Dale dances.","4. Dance is on Tuesday.","5. Elsa paints.","6. Painting is done at the gallery.","7. Finn codes.","8. Coding is done at the cafe.","9. Chess is on Monday."),
            solution = listOf(listOf(0,0,0,0), listOf(1,1,1,1), listOf(2,2,3,2), listOf(3,3,2,3))
        ),
        LogicGridPuzzle(
            title = "Medals, Events, and Countries",
            description = "Four athletes each win a medal in an event and represent a country.",
            categories = listOf("Person", "Medal", "Event", "Country"),
            items = listOf(listOf("Gina","Hugo","Ida","Jack"), listOf("Gold","Silver","Bronze","Iron"), listOf("Sprint","Jump","Swim","Throw"), listOf("USA","UK","AUS","CAN")),
            clues = listOf("1. Gina wins gold.","2. The gold medalist sprints.","3. Hugo wins silver.","4. The silver medalist jumps.","5. Ida wins bronze.","6. The bronze medalist swims.","7. Jack wins iron.","8. USA wins gold.","9. UK wins silver."),
            solution = listOf(listOf(0,0,0,0), listOf(1,1,1,1), listOf(2,2,2,2), listOf(3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Planets, Sizes, and Rings",
            description = "Four astronomers each study a planet with a size and ring count.",
            categories = listOf("Person", "Planet", "Size", "Rings"),
            items = listOf(listOf("Kyla","Liam","Maya","Noah"), listOf("Mars","Saturn","Neptune","Venus"), listOf("Small","Huge","Large","Tiny"), listOf("None","Many","Few","One")),
            clues = listOf("1. Kyla studies Mars.","2. Mars is small.","3. Liam studies Saturn.","4. Saturn is huge.","5. Maya studies Neptune.","6. Neptune is large.","7. Noah studies Venus.","8. Venus is tiny.","9. Saturn has many rings."),
            solution = listOf(listOf(0,0,0,0), listOf(1,1,1,1), listOf(2,2,2,2), listOf(3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Books, Genres, and Length",
            description = "Four readers each have a book, genre, and different reading length.",
            categories = listOf("Person", "Book", "Genre", "Length"),
            items = listOf(listOf("Olga","Paul","Quin","Rita"), listOf("Atlas","Brief","Canon","Draft"), listOf("History","Science","Fiction","Poetry"), listOf("Short","Medium","Long","Epic")),
            clues = listOf("1. Olga reads Atlas.","2. Atlas is a history book.","3. Paul reads Brief.","4. Brief is a science book.","5. Quin reads Canon.","6. Canon is fiction.","7. Rita reads Draft.","8. Draft is poetry.","9. Atlas is short."),
            solution = listOf(listOf(0,0,0,0), listOf(1,1,1,1), listOf(2,2,2,2), listOf(3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Cafes, Drinks, and Snacks",
            description = "Four cafe-goers each visit a cafe, order a drink, and eat a snack.",
            categories = listOf("Person", "Cafe", "Drink", "Snack"),
            items = listOf(listOf("Sean","Tess","Ulric","Vera"), listOf("Bean","Brew","Cup","Drip"), listOf("Espresso","Latte","Mocha","Tea"), listOf("Cake","Cookie","Muffin","Scone")),
            clues = listOf("1. Sean visits Bean cafe.","2. Bean cafe serves espresso.","3. Tess visits Brew cafe.","4. Brew cafe serves latte.","5. Ulric visits Cup cafe.","6. Cup cafe serves mocha.","7. Vera visits Drip cafe.","8. Drip cafe serves tea.","9. Bean cafe sells cake."),
            solution = listOf(listOf(0,0,0,0), listOf(1,1,1,1), listOf(2,2,2,2), listOf(3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Schools, Clubs, and Awards",
            description = "Four students each attend a school, join a club, and win an award.",
            categories = listOf("Person", "School", "Club", "Award"),
            items = listOf(listOf("Walt","Xena","Yara","Zack"), listOf("Alpha","Beta","Gamma","Delta"), listOf("Chess","Art","Music","Drama"), listOf("Star","Moon","Sun","Cloud")),
            clues = listOf("1. Walt goes to Alpha.","2. Alpha students are in chess club.","3. Xena goes to Beta.","4. Beta students join art club.","5. Yara goes to Gamma.","6. Gamma students are in music club.","7. Zack goes to Delta.","8. Delta students join drama club.","9. Alpha students win the Star award."),
            solution = listOf(listOf(0,0,0,0), listOf(1,1,1,1), listOf(2,2,2,2), listOf(3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Bikes, Speeds, and Routes",
            description = "Four cyclists each have a bike type, a speed, and a route.",
            categories = listOf("Person", "Bike", "Speed", "Route"),
            items = listOf(listOf("Abby","Bart","Cass","Drew"), listOf("Road","Trail","City","BMX"), listOf("Fast","Slow","Mid","Sprint"), listOf("Hill","Flat","Urban","Park")),
            clues = listOf("1. Abby rides a road bike.","2. Road bikes are ridden fast.","3. Bart rides a trail bike.","4. Trail bikes go slow.","5. Cass rides a city bike.","6. City bikes go at mid speed.","7. Drew rides BMX.","8. BMX riders sprint.","9. Road bikes go on hills."),
            solution = listOf(listOf(0,0,0,0), listOf(1,1,1,1), listOf(2,2,2,2), listOf(3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Shops, Products, and Prices",
            description = "Four shoppers each visit a shop, buy a product, and pay a price.",
            categories = listOf("Person", "Shop", "Product", "Price"),
            items = listOf(listOf("Elsa","Fred","Gina","Hans"), listOf("Tech","Food","Cloth","Book"), listOf("Phone","Fruit","Shirt","Novel"), listOf("High","Low","Mid","Free")),
            clues = listOf("1. Elsa shops at Tech.","2. Tech shop sells phones.","3. Fred shops at Food.","4. Food shop sells fruit.","5. Gina shops at Cloth.","6. Cloth shop sells shirts.","7. Hans shops at Book.","8. Book shop sells novels.","9. Tech products are high-priced."),
            solution = listOf(listOf(0,0,0,0), listOf(1,1,1,1), listOf(2,2,2,2), listOf(3,3,3,3))
        )
    )

    fun get(difficulty: Int, index: Int): LogicGridPuzzle {
        val pool = when (difficulty) {
            0 -> EASY; 1 -> MEDIUM; 2 -> HARD; else -> EXPERT
        }
        return pool[index.coerceIn(0, pool.size - 1)]
    }
}
