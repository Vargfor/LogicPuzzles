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
    ) + listOf(
        // MEDIUM puzzles 16-25
        LogicGridPuzzle(
            title = "Tools and Materials",
            description = "Three craftsmen each use a tool and work with a material.",
            categories = listOf("Person", "Tool", "Material"),
            items = listOf(listOf("Abe","Beth","Cruz"), listOf("Hammer","Saw","Drill"), listOf("Wood","Metal","Stone")),
            clues = listOf("1. Abe does not use a hammer.","2. The saw cuts through stone.","3. Beth uses a drill.","4. Cruz does not work with wood.","5. Beth does not work with stone."),
            solution = listOf(listOf(0,1,2), listOf(1,2,0), listOf(2,0,1))
        ),
        LogicGridPuzzle(
            title = "Stars and Colors",
            description = "Three astronomers each study a star and note its color.",
            categories = listOf("Person", "Star", "Color"),
            items = listOf(listOf("Dana","Eli","Fern"), listOf("Vega","Sirius","Rigel"), listOf("Blue","White","Red")),
            clues = listOf("1. Dana studies Vega.","2. Sirius is a white star.","3. Eli does not study Rigel.","4. Rigel is a red star.","5. Dana's star is not white."),
            solution = listOf(listOf(0,0,0), listOf(1,1,1), listOf(2,2,2))
        ),
        LogicGridPuzzle(
            title = "Hobbies and Seasons",
            description = "Three friends each have a hobby and practice it in a favorite season.",
            categories = listOf("Person", "Hobby", "Season"),
            items = listOf(listOf("Glen","Hope","Iris"), listOf("Knitting","Surfing","Hiking"), listOf("Winter","Summer","Spring")),
            clues = listOf("1. Glen does not surf.","2. Surfing is a summer hobby.","3. Hope loves winter.","4. Hiking is a spring activity.","5. Glen does not knit."),
            solution = listOf(listOf(0,2,2), listOf(1,0,0), listOf(2,1,1))
        ),
        LogicGridPuzzle(
            title = "Shapes and Colors",
            description = "Three designers each work with a shape and a color.",
            categories = listOf("Person", "Shape", "Color"),
            items = listOf(listOf("Jay","Kate","Lars"), listOf("Circle","Triangle","Square"), listOf("Orange","Purple","Teal")),
            clues = listOf("1. Jay works with circles.","2. The triangle designer uses purple.","3. Kate does not use teal.","4. Lars does not work with triangles.","5. Jay does not use orange."),
            solution = listOf(listOf(0,0,2), listOf(1,1,1), listOf(2,2,0))
        ),
        LogicGridPuzzle(
            title = "Songs and Bands",
            description = "Three music fans each follow a band and enjoy a genre.",
            categories = listOf("Person", "Band", "Genre"),
            items = listOf(listOf("Mira","Ned","Opal"), listOf("Echoes","Flares","Gust"), listOf("Pop","Jazz","Folk")),
            clues = listOf("1. Mira's favorite band is Echoes.","2. Flares plays jazz.","3. Ned does not like Gust.","4. Opal does not listen to pop.","5. Echoes does not play folk."),
            solution = listOf(listOf(0,0,0), listOf(1,1,1), listOf(2,2,2))
        ),
        LogicGridPuzzle(
            title = "Insects and Habitats",
            description = "Three entomologists each study an insect and its habitat.",
            categories = listOf("Person", "Insect", "Habitat"),
            items = listOf(listOf("Pam","Rex","Sue"), listOf("Bee","Moth","Ant"), listOf("Garden","Forest","Underground")),
            clues = listOf("1. Pam studies bees.","2. Bees live in gardens.","3. Rex does not study ants.","4. Moths live in forests.","5. Pam does not work underground."),
            solution = listOf(listOf(0,0,0), listOf(1,1,1), listOf(2,2,2))
        ),
        LogicGridPuzzle(
            title = "Cheeses and Countries",
            description = "Three food critics each review a cheese and identify its country of origin.",
            categories = listOf("Person", "Cheese", "Country"),
            items = listOf(listOf("Tom","Uma","Vera"), listOf("Brie","Gouda","Feta"), listOf("France","Netherlands","Greece")),
            clues = listOf("1. Tom reviews Brie.","2. Gouda comes from the Netherlands.","3. Uma does not review Feta.","4. Brie originates in France.","5. Tom does not review Dutch cheese."),
            solution = listOf(listOf(0,0,0), listOf(1,1,1), listOf(2,2,2))
        ),
        LogicGridPuzzle(
            title = "Rivers and Continents",
            description = "Three geographers each study a river and the continent it flows through.",
            categories = listOf("Person", "River", "Continent"),
            items = listOf(listOf("Walt","Xena","Yuki"), listOf("Amazon","Nile","Ganges"), listOf("Americas","Africa","Asia")),
            clues = listOf("1. Walt studies the Amazon.","2. The Nile flows through Africa.","3. Xena does not study the Ganges.","4. The Amazon is in the Americas.","5. Walt does not study an Asian river."),
            solution = listOf(listOf(0,0,0), listOf(1,1,1), listOf(2,2,2))
        ),
        LogicGridPuzzle(
            title = "Shoes and Occasions",
            description = "Three stylists each recommend a shoe type for a different occasion.",
            categories = listOf("Person", "Shoe", "Occasion"),
            items = listOf(listOf("Zara","Alex","Beau"), listOf("Boots","Heels","Sneakers"), listOf("Hiking","Formal","Casual")),
            clues = listOf("1. Zara recommends boots.","2. Heels are for formal occasions.","3. Alex does not recommend sneakers.","4. Sneakers are for casual wear.","5. Zara does not style for formal events."),
            solution = listOf(listOf(0,0,0), listOf(1,1,1), listOf(2,2,2))
        ),
        LogicGridPuzzle(
            title = "Spices and Flavors",
            description = "Three chefs each specialize in a spice and its dominant flavor profile.",
            categories = listOf("Person", "Spice", "Flavor"),
            items = listOf(listOf("Cleo","Dax","Elara"), listOf("Cumin","Saffron","Paprika"), listOf("Earthy","Floral","Smoky")),
            clues = listOf("1. Cleo uses cumin.","2. Saffron has a floral flavor.","3. Dax does not use paprika.","4. Paprika has a smoky taste.","5. Cleo's spice is not floral."),
            solution = listOf(listOf(0,0,0), listOf(1,1,1), listOf(2,2,2))
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
    ) + listOf(
        // HARD puzzles 16-35
        LogicGridPuzzle(
            title = "Cities, Jobs, and Transport",
            description = "Three workers live in different cities, have different jobs, and use different transport.",
            categories = listOf("Person", "Job", "City", "Transport"),
            items = listOf(listOf("Abe","Beth","Cruz"), listOf("Teacher","Nurse","Chef"), listOf("Oslo","Lima","Cairo"), listOf("Bus","Train","Bike")),
            clues = listOf("1. Abe is a teacher.","2. The teacher lives in Oslo.","3. Beth is a nurse.","4. The nurse lives in Lima.","5. Cruz takes a bike.","6. The Oslo resident takes the bus."),
            solution = listOf(listOf(0,0,0,0), listOf(1,1,1,1), listOf(2,2,2,2))
        ),
        LogicGridPuzzle(
            title = "Foods, Days, and Seasons",
            description = "Three friends each eat a favorite food, on a preferred day, in a season.",
            categories = listOf("Person", "Food", "Day", "Season"),
            items = listOf(listOf("Dana","Eli","Fern"), listOf("Soup","Salad","Stew"), listOf("Mon","Wed","Fri"), listOf("Winter","Spring","Autumn")),
            clues = listOf("1. Dana eats soup.","2. Soup is eaten on Mondays.","3. Eli eats salad.","4. Salad is a spring food.","5. Fern eats on Fridays.","6. Stew is an autumn dish."),
            solution = listOf(listOf(0,0,0,0), listOf(1,1,2,1), listOf(2,2,1,2))
        ),
        LogicGridPuzzle(
            title = "Animals, Colors, and Sizes",
            description = "Three zookeepers each care for an animal of a different color and size.",
            categories = listOf("Person", "Animal", "Color", "Size"),
            items = listOf(listOf("Glen","Hope","Iris"), listOf("Parrot","Turtle","Rabbit"), listOf("Green","Brown","White"), listOf("Small","Medium","Large")),
            clues = listOf("1. Glen cares for the parrot.","2. The parrot is green.","3. Hope's animal is brown.","4. The rabbit is white.","5. The parrot is small.","6. Hope's animal is medium sized."),
            solution = listOf(listOf(0,0,0,0), listOf(1,1,1,1), listOf(2,2,2,2))
        ),
        LogicGridPuzzle(
            title = "Students, Clubs, and Days",
            description = "Three students each join a club, meet on a day, and earn a rank.",
            categories = listOf("Person", "Club", "Day", "Rank"),
            items = listOf(listOf("Jay","Kate","Lars"), listOf("Chess","Drama","Coding"), listOf("Mon","Tue","Wed"), listOf("Bronze","Silver","Gold")),
            clues = listOf("1. Jay joins chess.","2. Chess meets on Mondays.","3. Kate joins drama.","4. Drama meets on Tuesdays.","5. Lars has a gold rank.","6. The chess club earns bronze rank."),
            solution = listOf(listOf(0,0,0,0), listOf(1,1,1,1), listOf(2,2,2,2))
        ),
        LogicGridPuzzle(
            title = "Writers, Genres, and Places",
            description = "Three writers each write a genre, set in a place, and write at a time of day.",
            categories = listOf("Person", "Genre", "Place", "Time"),
            items = listOf(listOf("Mira","Ned","Opal"), listOf("Mystery","Fantasy","Comedy"), listOf("City","Forest","Beach"), listOf("Morning","Noon","Night")),
            clues = listOf("1. Mira writes mystery.","2. Mystery is set in a city.","3. Ned writes fantasy.","4. Fantasy is set in a forest.","5. Opal writes at night.","6. The mystery writer works in the morning."),
            solution = listOf(listOf(0,0,0,0), listOf(1,1,1,1), listOf(2,2,2,2))
        ),
        LogicGridPuzzle(
            title = "Chefs, Dishes, and Restaurants",
            description = "Three chefs each cook a dish, work at a restaurant, and serve a portion.",
            categories = listOf("Person", "Dish", "Restaurant", "Portion"),
            items = listOf(listOf("Pam","Rex","Sue"), listOf("Risotto","Ramen","Curry"), listOf("Alto","Mido","Basso"), listOf("Small","Regular","Large")),
            clues = listOf("1. Pam cooks risotto.","2. The risotto is served at Alto.","3. Rex cooks ramen.","4. Ramen is served at Mido.","5. Sue serves large portions.","6. The risotto is a small portion."),
            solution = listOf(listOf(0,0,0,0), listOf(1,1,1,1), listOf(2,2,2,2))
        ),
        LogicGridPuzzle(
            title = "Pilots, Routes, and Planes",
            description = "Three pilots each fly a route, use a plane type, and depart at a time.",
            categories = listOf("Person", "Route", "Plane", "Departure"),
            items = listOf(listOf("Tom","Uma","Vera"), listOf("North","East","South"), listOf("Jet","Prop","Twin"), listOf("AM","Noon","PM")),
            clues = listOf("1. Tom flies north.","2. The north route uses a jet.","3. Uma flies east.","4. The east route uses a prop plane.","5. Vera departs in the afternoon.","6. The north route departs in the morning."),
            solution = listOf(listOf(0,0,0,0), listOf(1,1,1,1), listOf(2,2,2,2))
        ),
        LogicGridPuzzle(
            title = "Painters, Brushes, and Canvases",
            description = "Three painters each use a brush size, a canvas size, and a color palette.",
            categories = listOf("Person", "Brush", "Canvas", "Palette"),
            items = listOf(listOf("Walt","Xena","Yuki"), listOf("Fine","Medium","Wide"), listOf("Small","Large","Huge"), listOf("Warm","Cool","Neutral")),
            clues = listOf("1. Walt uses a fine brush.","2. The fine brush is used on a small canvas.","3. Xena uses a medium brush.","4. The medium brush uses a cool palette.","5. Yuki uses a huge canvas.","6. The small canvas uses warm colors."),
            solution = listOf(listOf(0,0,0,0), listOf(1,1,2,1), listOf(2,2,1,2))
        ),
        LogicGridPuzzle(
            title = "Nurses, Wards, and Shifts",
            description = "Three nurses each work in a ward, on a shift, and care for a condition.",
            categories = listOf("Person", "Ward", "Shift", "Condition"),
            items = listOf(listOf("Zara","Alex","Beau"), listOf("Pediatric","Surgical","Emergency"), listOf("Day","Night","Evening"), listOf("Fever","Fracture","Injury")),
            clues = listOf("1. Zara works in pediatrics.","2. The pediatric nurse works the day shift.","3. Alex works in surgery.","4. The surgical nurse works nights.","5. Beau treats injuries.","6. The pediatric ward handles fever cases."),
            solution = listOf(listOf(0,0,0,0), listOf(1,1,1,1), listOf(2,2,2,2))
        ),
        LogicGridPuzzle(
            title = "Farmers, Crops, and Tools",
            description = "Three farmers each grow a crop, use a tool, and work in a season.",
            categories = listOf("Person", "Crop", "Tool", "Season"),
            items = listOf(listOf("Cleo","Dax","Elara"), listOf("Wheat","Corn","Rice"), listOf("Plow","Sickle","Hoe"), listOf("Spring","Summer","Autumn")),
            clues = listOf("1. Cleo grows wheat.","2. Wheat is plowed in spring.","3. Dax grows corn.","4. Corn is harvested with a sickle.","5. Elara works in autumn.","6. The wheat farmer works in spring."),
            solution = listOf(listOf(0,0,0,0), listOf(1,1,1,1), listOf(2,2,2,2))
        ),
        LogicGridPuzzle(
            title = "Traders, Goods, and Ports",
            description = "Three traders each sell goods, work at a port, and sail in a season.",
            categories = listOf("Person", "Goods", "Port", "Season"),
            items = listOf(listOf("Finn","Gala","Hiro"), listOf("Silk","Spice","Gold"), listOf("East","West","South"), listOf("Spring","Summer","Winter")),
            clues = listOf("1. Finn sells silk.","2. Silk is traded at the East port.","3. Gala sells spice.","4. Spice is sold at the West port.","5. Hiro sails in winter.","6. Silk traders sail in spring."),
            solution = listOf(listOf(0,0,0,0), listOf(1,1,1,1), listOf(2,2,2,2))
        ),
        LogicGridPuzzle(
            title = "Cooks, Pots, and Kitchens",
            description = "Three cooks each use a pot type, cook in a kitchen style, and make a meal.",
            categories = listOf("Person", "Pot", "Kitchen", "Meal"),
            items = listOf(listOf("Ivan","Jade","Kurt"), listOf("Clay","Steel","Cast-iron"), listOf("Open","Closed","Camp"), listOf("Porridge","Broth","Stew")),
            clues = listOf("1. Ivan uses a clay pot.","2. The clay pot makes porridge.","3. Jade uses a steel pot.","4. The steel pot is used in a closed kitchen.","5. Kurt cooks in a camp kitchen.","6. The clay pot is used in an open kitchen."),
            solution = listOf(listOf(0,0,0,0), listOf(1,1,1,1), listOf(2,2,2,2))
        ),
        LogicGridPuzzle(
            title = "Sailors, Ships, and Seas",
            description = "Three sailors each captain a ship type, sail a sea, and carry cargo.",
            categories = listOf("Person", "Ship", "Sea", "Cargo"),
            items = listOf(listOf("Luna","Max","Nina"), listOf("Sloop","Brig","Galleon"), listOf("Red","Black","White"), listOf("Grain","Timber","Coal")),
            clues = listOf("1. Luna captains a sloop.","2. The sloop sails the Red Sea.","3. Max captains a brig.","4. The brig sails the Black Sea.","5. Nina carries coal.","6. The sloop carries grain."),
            solution = listOf(listOf(0,0,0,0), listOf(1,1,1,1), listOf(2,2,2,2))
        ),
        LogicGridPuzzle(
            title = "Scholars, Topics, and Libraries",
            description = "Three scholars each research a topic, use a library, and publish in a journal.",
            categories = listOf("Person", "Topic", "Library", "Journal"),
            items = listOf(listOf("Otto","Petra","Quinn"), listOf("History","Law","Medicine"), listOf("National","City","Royal"), listOf("Chronicle","Record","Report")),
            clues = listOf("1. Otto researches history.","2. The history scholar uses the National Library.","3. Petra researches law.","4. The law scholar uses the City Library.","5. Quinn publishes in the Report journal.","6. The history scholar publishes in Chronicle."),
            solution = listOf(listOf(0,0,0,0), listOf(1,1,1,1), listOf(2,2,2,2))
        ),
        LogicGridPuzzle(
            title = "Riders, Horses, and Tracks",
            description = "Three riders each have a horse, compete on a track, and wear a color.",
            categories = listOf("Person", "Horse", "Track", "Color"),
            items = listOf(listOf("Rosa","Sam","Tara"), listOf("Storm","Flash","Blaze"), listOf("Oval","Straight","Cross"), listOf("Red","Blue","Green")),
            clues = listOf("1. Rosa rides Storm.","2. Storm competes on the oval track.","3. Sam rides Flash.","4. Flash races on the straight track.","5. Tara wears green.","6. Storm's rider wears red."),
            solution = listOf(listOf(0,0,0,0), listOf(1,1,1,1), listOf(2,2,2,2))
        ),
        LogicGridPuzzle(
            title = "Builders, Materials, and Tools",
            description = "Three builders each use a material, a tool, and work in a district.",
            categories = listOf("Person", "Material", "Tool", "District"),
            items = listOf(listOf("Uma","Vera","Will"), listOf("Brick","Wood","Stone"), listOf("Chisel","Saw","Hammer"), listOf("North","South","East")),
            clues = listOf("1. Uma builds with brick.","2. The brick builder uses a chisel.","3. Vera builds with wood.","4. The wood builder uses a saw.","5. Will works in the east district.","6. The brick builder works in the north."),
            solution = listOf(listOf(0,0,0,0), listOf(1,1,1,1), listOf(2,2,2,2))
        ),
        LogicGridPuzzle(
            title = "Explorers, Maps, and Ships",
            description = "Three explorers each use a map type, sail a ship, and explore a region.",
            categories = listOf("Person", "Map", "Ship", "Region"),
            items = listOf(listOf("Xena","Yael","Zack"), listOf("Star","Sea","Land"), listOf("Caravel","Frigate","Schooner"), listOf("Arctic","Tropics","Desert")),
            clues = listOf("1. Xena uses a star map.","2. The star map navigator sails a caravel.","3. Yael uses a sea map.","4. The sea map navigator sails a frigate.","5. Zack explores the desert.","6. The star map navigator explores the Arctic."),
            solution = listOf(listOf(0,0,0,0), listOf(1,1,1,1), listOf(2,2,2,2))
        ),
        LogicGridPuzzle(
            title = "Inventors, Tools, and Decades",
            description = "Three inventors each create a tool, earn a patent, and work in a decade.",
            categories = listOf("Person", "Tool", "Patent", "Decade"),
            items = listOf(listOf("Abby","Bart","Cass"), listOf("Lens","Gear","Spring"), listOf("Gold","Silver","Bronze"), listOf("1880s","1890s","1900s")),
            clues = listOf("1. Abby invents a lens.","2. The lens earns a gold patent.","3. Bart invents a gear.","4. The gear earns a silver patent.","5. Cass works in the 1900s.","6. The lens was invented in the 1880s."),
            solution = listOf(listOf(0,0,0,0), listOf(1,1,1,1), listOf(2,2,2,2))
        ),
        LogicGridPuzzle(
            title = "Guards, Gates, and Shifts",
            description = "Three guards each watch a gate, work a shift, and carry a weapon.",
            categories = listOf("Person", "Gate", "Shift", "Weapon"),
            items = listOf(listOf("Drew","Elsa","Fred"), listOf("North","East","South"), listOf("Morning","Afternoon","Night"), listOf("Sword","Bow","Spear")),
            clues = listOf("1. Drew guards the north gate.","2. The north gate is guarded in the morning.","3. Elsa guards the east gate.","4. The east gate guard works afternoons.","5. Fred carries a spear.","6. The north gate guard carries a sword."),
            solution = listOf(listOf(0,0,0,0), listOf(1,1,1,1), listOf(2,2,2,2))
        ),
        LogicGridPuzzle(
            title = "Hunters, Prey, and Weapons",
            description = "Three hunters each track prey, use a weapon, and hunt in a terrain.",
            categories = listOf("Person", "Prey", "Weapon", "Terrain"),
            items = listOf(listOf("Gina","Hans","Ida"), listOf("Deer","Boar","Hare"), listOf("Rifle","Bow","Trap"), listOf("Forest","Hill","Plains")),
            clues = listOf("1. Gina hunts deer.","2. The deer hunter uses a rifle.","3. Hans hunts boar.","4. The boar hunter uses a bow.","5. Ida hunts on the plains.","6. The deer hunter hunts in the forest."),
            solution = listOf(listOf(0,0,0,0), listOf(1,1,1,1), listOf(2,2,2,2))
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
    ) + listOf(
        // EXPERT puzzles 16-45
        LogicGridPuzzle(
            title = "Careers, Cities, and Transport",
            description = "Four professionals each have a career, live in a city, and use a transport.",
            categories = listOf("Person", "Career", "City", "Transport"),
            items = listOf(listOf("Arlo","Bess","Cole","Dana"), listOf("Lawyer","Pilot","Writer","Scientist"), listOf("Oslo","Lima","Cairo","Delhi"), listOf("Train","Plane","Bus","Bike")),
            clues = listOf("1. Arlo is a lawyer.","2. The lawyer lives in Oslo.","3. Bess is a pilot.","4. The pilot lives in Lima.","5. Cole is a writer.","6. The writer lives in Cairo.","7. Dana is a scientist.","8. The scientist lives in Delhi.","9. The lawyer takes the train.","10. The pilot takes a plane."),
            solution = listOf(listOf(0,0,0,0), listOf(1,1,1,1), listOf(2,2,2,2), listOf(3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Gardens, Flowers, and Soils",
            description = "Four gardeners each grow a flower in a garden with a soil type.",
            categories = listOf("Person", "Flower", "Garden", "Soil"),
            items = listOf(listOf("Eli","Flo","Gil","Hana"), listOf("Rose","Iris","Tulip","Lily"), listOf("North","South","East","West"), listOf("Clay","Sand","Loam","Peat")),
            clues = listOf("1. Eli grows roses.","2. The rose garden is in the north.","3. Flo grows irises.","4. The iris garden is in the south.","5. Gil grows tulips.","6. The tulip garden is in the east.","7. Hana grows lilies.","8. The lily garden is in the west.","9. The north garden has clay soil.","10. The south garden has sandy soil."),
            solution = listOf(listOf(0,0,0,0), listOf(1,1,1,1), listOf(2,2,2,2), listOf(3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Festivals, Foods, and Months",
            description = "Four friends each attend a festival, eat a food, and go in a month.",
            categories = listOf("Person", "Festival", "Food", "Month"),
            items = listOf(listOf("Ivan","Jade","Kurt","Luna"), listOf("Jazz","Film","Food","Art"), listOf("Tacos","Crepes","Curry","Sushi"), listOf("Jan","Apr","Jul","Oct")),
            clues = listOf("1. Ivan attends the jazz festival.","2. The jazz festival is in January.","3. Jade attends the film festival.","4. The film festival is in April.","5. Kurt attends the food festival.","6. The food festival is in July.","7. Luna attends the art festival.","8. The art festival is in October.","9. The jazz festival serves tacos.","10. The film festival serves crepes."),
            solution = listOf(listOf(0,0,0,0), listOf(1,1,1,1), listOf(2,2,2,2), listOf(3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Mountains, Heights, and Camps",
            description = "Four climbers each summit a mountain with a height and use a base camp.",
            categories = listOf("Person", "Mountain", "Height", "Camp"),
            items = listOf(listOf("Max","Nina","Otto","Petra"), listOf("Alpha","Bravo","Charlie","Delta"), listOf("Low","Mid","High","Peak"), listOf("Red","Blue","Green","Gold")),
            clues = listOf("1. Max climbs Alpha.","2. Alpha is the lowest mountain.","3. Nina climbs Bravo.","4. Bravo is at mid height.","5. Otto climbs Charlie.","6. Charlie is the high mountain.","7. Petra climbs Delta.","8. Delta is at peak height.","9. Alpha's base camp is Red.","10. Bravo's base camp is Blue."),
            solution = listOf(listOf(0,0,0,0), listOf(1,1,1,1), listOf(2,2,2,2), listOf(3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Desserts, Countries, and Techniques",
            description = "Four chefs each make a dessert from a country using a technique.",
            categories = listOf("Person", "Dessert", "Country", "Technique"),
            items = listOf(listOf("Quinn","Rosa","Sam","Tara"), listOf("Cake","Tart","Pudding","Souffle"), listOf("UK","France","USA","Italy"), listOf("Bake","Chill","Steam","Whip")),
            clues = listOf("1. Quinn makes cake.","2. Cake is a UK dessert.","3. Rosa makes tart.","4. Tart is a French dessert.","5. Sam makes pudding.","6. Pudding is from the USA.","7. Tara makes souffle.","8. Souffle is Italian.","9. Cake is baked.","10. Tart is chilled."),
            solution = listOf(listOf(0,0,0,0), listOf(1,1,1,1), listOf(2,2,2,2), listOf(3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Oceans, Depths, and Creatures",
            description = "Four divers each explore an ocean, dive to a depth, and find a creature.",
            categories = listOf("Person", "Ocean", "Depth", "Creature"),
            items = listOf(listOf("Uma","Vera","Will","Xena"), listOf("Pacific","Atlantic","Indian","Arctic"), listOf("Shallow","Mid","Deep","Abyss"), listOf("Shark","Whale","Octopus","Jellyfish")),
            clues = listOf("1. Uma dives in the Pacific.","2. The Pacific dive is shallow.","3. Vera dives in the Atlantic.","4. The Atlantic dive is at mid depth.","5. Will dives in the Indian Ocean.","6. The Indian Ocean dive is deep.","7. Xena dives in the Arctic.","8. The Arctic dive reaches the abyss.","9. Uma finds a shark.","10. Vera finds a whale."),
            solution = listOf(listOf(0,0,0,0), listOf(1,1,1,1), listOf(2,2,2,2), listOf(3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Languages, Scripts, and Nations",
            description = "Four linguists each study a language, its script, and the nation that speaks it.",
            categories = listOf("Person", "Language", "Script", "Nation"),
            items = listOf(listOf("Yael","Zack","Abby","Bram"), listOf("Arabic","Chinese","Hindi","Greek"), listOf("ArabicScript","Hanzi","Devanagari","GreekScript"), listOf("Egypt","China","India","Greece")),
            clues = listOf("1. Yael studies Arabic.","2. Arabic uses the Arabic script.","3. Zack studies Chinese.","4. Chinese uses Hanzi script.","5. Abby studies Hindi.","6. Hindi uses Devanagari script.","7. Bram studies Greek.","8. Greek uses the Greek script.","9. Arabic is spoken in Egypt.","10. Chinese is spoken in China."),
            solution = listOf(listOf(0,0,0,0), listOf(1,1,1,1), listOf(2,2,2,2), listOf(3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Trains, Routes, and Classes",
            description = "Four travellers each take a train on a route and sit in a class.",
            categories = listOf("Person", "Train", "Route", "Class"),
            items = listOf(listOf("Cara","Dale","Elsa","Finn"), listOf("Express","Local","Bullet","Freight"), listOf("North","South","East","West"), listOf("First","Second","Third","Cargo")),
            clues = listOf("1. Cara takes the express.","2. The express goes north.","3. Dale takes the local.","4. The local goes south.","5. Elsa takes the bullet train.","6. The bullet train goes east.","7. Finn takes the freight.","8. The freight goes west.","9. The express is first class.","10. The local is second class."),
            solution = listOf(listOf(0,0,0,0), listOf(1,1,1,1), listOf(2,2,2,2), listOf(3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Myths, Heroes, and Powers",
            description = "Four mythology fans each study a myth, name a hero, and identify a power.",
            categories = listOf("Person", "Myth", "Hero", "Power"),
            items = listOf(listOf("Gina","Hugo","Ida","Jack"), listOf("Greek","Norse","Egyptian","Celtic"), listOf("Achilles","Thor","Ra","Cuchulainn"), listOf("Strength","Thunder","Sun","Speed")),
            clues = listOf("1. Gina studies Greek myth.","2. The Greek hero is Achilles.","3. Hugo studies Norse myth.","4. The Norse hero is Thor.","5. Ida studies Egyptian myth.","6. The Egyptian hero is Ra.","7. Jack studies Celtic myth.","8. The Celtic hero is Cuchulainn.","9. Achilles has strength.","10. Thor commands thunder."),
            solution = listOf(listOf(0,0,0,0), listOf(1,1,1,1), listOf(2,2,2,2), listOf(3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Architects, Styles, and Cities",
            description = "Four architects each work in a style, design a building type, and live in a city.",
            categories = listOf("Person", "Style", "Building", "City"),
            items = listOf(listOf("Kyla","Liam","Maya","Noah"), listOf("Gothic","Modern","Baroque","Minimal"), listOf("Church","Tower","Palace","Studio"), listOf("Berlin","Tokyo","Rome","Oslo")),
            clues = listOf("1. Kyla works in Gothic style.","2. The Gothic architect designs a church.","3. Liam works in Modern style.","4. The Modern architect designs a tower.","5. Maya works in Baroque style.","6. The Baroque architect designs a palace.","7. Noah works in Minimal style.","8. The Minimal architect designs a studio.","9. The Gothic architect lives in Berlin.","10. The Modern architect lives in Tokyo."),
            solution = listOf(listOf(0,0,0,0), listOf(1,1,1,1), listOf(2,2,2,2), listOf(3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Dances, Music, and Countries",
            description = "Four dancers each perform a dance style with a music instrument from a country.",
            categories = listOf("Person", "Dance", "Instrument", "Country"),
            items = listOf(listOf("Olga","Paul","Quin","Rita"), listOf("Tango","Waltz","Flamenco","Salsa"), listOf("Bandoneon","Violin","Guitar","Trumpet"), listOf("Argentina","Austria","Spain","Cuba")),
            clues = listOf("1. Olga dances tango.","2. Tango uses the bandoneon.","3. Paul dances waltz.","4. Waltz uses violin.","5. Quin dances flamenco.","6. Flamenco uses guitar.","7. Rita dances salsa.","8. Salsa uses trumpet.","9. Tango is from Argentina.","10. Waltz is from Austria."),
            solution = listOf(listOf(0,0,0,0), listOf(1,1,1,1), listOf(2,2,2,2), listOf(3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Games, Platforms, and Genres",
            description = "Four gamers each play a game on a platform in a genre.",
            categories = listOf("Person", "Game", "Platform", "Genre"),
            items = listOf(listOf("Sean","Tess","Ulric","Vera"), listOf("Quest","Arena","Realm","Strike"), listOf("PC","Console","Mobile","VR"), listOf("RPG","FPS","Puzzle","Racing")),
            clues = listOf("1. Sean plays Quest.","2. Quest is on PC.","3. Tess plays Arena.","4. Arena is on Console.","5. Ulric plays Realm.","6. Realm is on Mobile.","7. Vera plays Strike.","8. Strike is on VR.","9. Quest is an RPG.","10. Arena is an FPS."),
            solution = listOf(listOf(0,0,0,0), listOf(1,1,1,1), listOf(2,2,2,2), listOf(3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Scientists, Fields, and Discoveries",
            description = "Four scientists each work in a field, make a discovery, and win an award.",
            categories = listOf("Person", "Field", "Discovery", "Award"),
            items = listOf(listOf("Walt","Xena","Yara","Zack"), listOf("Physics","Chemistry","Biology","Astronomy"), listOf("Quark","Enzyme","Gene","Pulsar"), listOf("Nobel","Medal","Prize","Grant")),
            clues = listOf("1. Walt works in physics.","2. The physics discovery is the quark.","3. Xena works in chemistry.","4. The chemistry discovery is the enzyme.","5. Yara works in biology.","6. The biology discovery is the gene.","7. Zack works in astronomy.","8. The astronomy discovery is the pulsar.","9. The physics scientist wins the Nobel.","10. The chemistry scientist wins the Medal."),
            solution = listOf(listOf(0,0,0,0), listOf(1,1,1,1), listOf(2,2,2,2), listOf(3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Chefs, Cuisines, and Venues",
            description = "Four chefs each specialize in a cuisine, use a technique, and work at a venue.",
            categories = listOf("Person", "Cuisine", "Technique", "Venue"),
            items = listOf(listOf("Abby","Bart","Cass","Drew"), listOf("Thai","French","Japanese","Mexican"), listOf("Wok","Sous-vide","Grill","Steam"), listOf("Hotel","Bistro","Sushi-bar","Cantina")),
            clues = listOf("1. Abby cooks Thai.","2. Thai cuisine uses a wok.","3. Bart cooks French.","4. French cuisine uses sous-vide.","5. Cass cooks Japanese.","6. Japanese cuisine uses a grill.","7. Drew cooks Mexican.","8. Mexican cuisine uses steam.","9. The Thai chef works at a hotel.","10. The French chef works at a bistro."),
            solution = listOf(listOf(0,0,0,0), listOf(1,1,1,1), listOf(2,2,2,2), listOf(3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Writers, Genres, and Awards",
            description = "Four writers each write in a genre, set in a place, and win an award.",
            categories = listOf("Person", "Genre", "Setting", "Award"),
            items = listOf(listOf("Elsa","Fred","Gina","Hans"), listOf("Thriller","Romance","Fantasy","Horror"), listOf("City","Village","Castle","Forest"), listOf("Gold","Silver","Bronze","Crystal")),
            clues = listOf("1. Elsa writes thrillers.","2. The thriller is set in a city.","3. Fred writes romance.","4. The romance is set in a village.","5. Gina writes fantasy.","6. The fantasy is set in a castle.","7. Hans writes horror.","8. The horror is set in a forest.","9. The thriller wins the Gold award.","10. The romance wins the Silver award."),
            solution = listOf(listOf(0,0,0,0), listOf(1,1,1,1), listOf(2,2,2,2), listOf(3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Athletes, Sports, and Records",
            description = "Four athletes each set a record in a sport at a venue and in a year.",
            categories = listOf("Person", "Sport", "Venue", "Year"),
            items = listOf(listOf("Iris","Jake","Kara","Leo"), listOf("Swimming","Cycling","Running","Rowing"), listOf("Pool","Track","Road","Lake"), listOf("2018","2019","2020","2021")),
            clues = listOf("1. Iris set the swimming record.","2. Swimming takes place at the pool.","3. Jake set the cycling record.","4. Cycling takes place on the track.","5. Kara set the running record.","6. Running takes place on the road.","7. Leo set the rowing record.","8. Rowing takes place on a lake.","9. The swimming record was set in 2018.","10. The cycling record was set in 2019."),
            solution = listOf(listOf(0,0,0,0), listOf(1,1,1,1), listOf(2,2,2,2), listOf(3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Birds, Habitats, and Calls",
            description = "Four ornithologists each study a bird, its habitat, and its call type.",
            categories = listOf("Person", "Bird", "Habitat", "Call"),
            items = listOf(listOf("Mona","Nate","Ona","Pete"), listOf("Robin","Owl","Parrot","Eagle"), listOf("Garden","Forest","Jungle","Mountain"), listOf("Song","Hoot","Squawk","Screech")),
            clues = listOf("1. Mona studies the robin.","2. Robins live in gardens.","3. Nate studies the owl.","4. Owls live in forests.","5. Ona studies the parrot.","6. Parrots live in jungles.","7. Pete studies the eagle.","8. Eagles live on mountains.","9. Robins are known for their song.","10. Owls hoot."),
            solution = listOf(listOf(0,0,0,0), listOf(1,1,1,1), listOf(2,2,2,2), listOf(3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Painters, Styles, and Museums",
            description = "Four painters each work in a style, use a medium, and show at a museum.",
            categories = listOf("Person", "Style", "Medium", "Museum"),
            items = listOf(listOf("Quinn","Rana","Seth","Tara"), listOf("Abstract","Realist","Surreal","Impressionist"), listOf("Oil","Watercolor","Acrylic","Pastel"), listOf("Tate","Louvre","MoMA","Uffizi")),
            clues = listOf("1. Quinn paints abstract.","2. The abstract painter uses oil.","3. Rana paints realism.","4. The realist uses watercolor.","5. Seth paints surrealism.","6. The surrealist uses acrylic.","7. Tara paints impressionism.","8. The impressionist uses pastel.","9. The abstract painter shows at the Tate.","10. The realist shows at the Louvre."),
            solution = listOf(listOf(0,0,0,0), listOf(1,1,1,1), listOf(2,2,2,2), listOf(3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Travelers, Modes, and Durations",
            description = "Four travelers each use a mode of transport, take a route, and travel for a duration.",
            categories = listOf("Person", "Mode", "Route", "Duration"),
            items = listOf(listOf("Uma","Vera","Will","Xara"), listOf("Ship","Car","Helicopter","Balloon"), listOf("Coast","Highway","Sky","River"), listOf("Week","Day","Hour","Month")),
            clues = listOf("1. Uma travels by ship.","2. The ship takes the coast route.","3. Vera travels by car.","4. The car takes the highway.","5. Will travels by helicopter.","6. The helicopter takes the sky route.","7. Xara travels by balloon.","8. The balloon follows the river.","9. The ship journey lasts a week.","10. The car journey takes a day."),
            solution = listOf(listOf(0,0,0,0), listOf(1,1,1,1), listOf(2,2,2,2), listOf(3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Explorers, Continents, and Finds",
            description = "Four explorers each visit a continent, find an artifact, and travel in a decade.",
            categories = listOf("Person", "Continent", "Artifact", "Decade"),
            items = listOf(listOf("Yael","Zane","Abby","Bram"), listOf("Africa","Asia","Americas","Antarctica"), listOf("Mask","Scroll","Map","Crystal"), listOf("1960s","1970s","1980s","1990s")),
            clues = listOf("1. Yael explores Africa.","2. The Africa explorer finds a mask.","3. Zane explores Asia.","4. The Asia explorer finds a scroll.","5. Abby explores the Americas.","6. The Americas explorer finds a map.","7. Bram explores Antarctica.","8. The Antarctica explorer finds a crystal.","9. The Africa exploration was in the 1960s.","10. The Asia exploration was in the 1970s."),
            solution = listOf(listOf(0,0,0,0), listOf(1,1,1,1), listOf(2,2,2,2), listOf(3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Doctors, Specialties, and Hospitals",
            description = "Four doctors each have a specialty, work at a hospital, and treat a condition.",
            categories = listOf("Person", "Specialty", "Hospital", "Condition"),
            items = listOf(listOf("Cara","Dale","Elsa","Finn"), listOf("Cardiology","Neurology","Oncology","Pediatrics"), listOf("City","General","Central","North"), listOf("Heart","Brain","Cancer","Flu")),
            clues = listOf("1. Cara is a cardiologist.","2. The cardiologist works at City Hospital.","3. Dale is a neurologist.","4. The neurologist works at General Hospital.","5. Elsa is an oncologist.","6. The oncologist works at Central Hospital.","7. Finn is a pediatrician.","8. The pediatrician works at North Hospital.","9. The cardiologist treats heart conditions.","10. The neurologist treats brain conditions."),
            solution = listOf(listOf(0,0,0,0), listOf(1,1,1,1), listOf(2,2,2,2), listOf(3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Inventors, Inventions, and Eras",
            description = "Four inventors each created an invention in an era and in a country.",
            categories = listOf("Person", "Invention", "Era", "Country"),
            items = listOf(listOf("Gina","Hugo","Ida","Jack"), listOf("Engine","Radio","Vaccine","Telescope"), listOf("1800s","1900s","1700s","1600s"), listOf("UK","Italy","France","Netherlands")),
            clues = listOf("1. Gina invented the engine.","2. The engine was invented in the 1800s.","3. Hugo invented the radio.","4. The radio was invented in the 1900s.","5. Ida invented the vaccine.","6. The vaccine was invented in the 1700s.","7. Jack invented the telescope.","8. The telescope was invented in the 1600s.","9. The engine was invented in the UK.","10. The radio was invented in Italy."),
            solution = listOf(listOf(0,0,0,0), listOf(1,1,1,1), listOf(2,2,2,2), listOf(3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Photographers, Subjects, and Cameras",
            description = "Four photographers each shoot a subject, use a camera, and work in a studio location.",
            categories = listOf("Person", "Subject", "Camera", "Studio"),
            items = listOf(listOf("Kyla","Liam","Maya","Noah"), listOf("Portrait","Landscape","Wildlife","Street"), listOf("DSLR","Film","Mirrorless","Drone"), listOf("Downtown","Uptown","Midtown","Suburb")),
            clues = listOf("1. Kyla shoots portraits.","2. The portrait photographer uses a DSLR.","3. Liam shoots landscapes.","4. The landscape photographer uses film.","5. Maya shoots wildlife.","6. The wildlife photographer uses mirrorless.","7. Noah shoots street photography.","8. The street photographer uses a drone.","9. The portrait studio is downtown.","10. The landscape studio is uptown."),
            solution = listOf(listOf(0,0,0,0), listOf(1,1,1,1), listOf(2,2,2,2), listOf(3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Collectors, Items, and Centuries",
            description = "Four collectors each collect an item from a century and display it in a room.",
            categories = listOf("Person", "Item", "Century", "Room"),
            items = listOf(listOf("Olga","Paul","Quin","Rita"), listOf("Coin","Stamp","Map","Vase"), listOf("16th","17th","18th","19th"), listOf("Hall","Library","Study","Gallery")),
            clues = listOf("1. Olga collects coins.","2. The coins are from the 16th century.","3. Paul collects stamps.","4. The stamps are from the 17th century.","5. Quin collects maps.","6. The maps are from the 18th century.","7. Rita collects vases.","8. The vases are from the 19th century.","9. The coins are displayed in the hall.","10. The stamps are in the library."),
            solution = listOf(listOf(0,0,0,0), listOf(1,1,1,1), listOf(2,2,2,2), listOf(3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Professors, Subjects, and Ranks",
            description = "Four professors each teach a subject, hold a rank, and publish in a journal.",
            categories = listOf("Person", "Subject", "Rank", "Journal"),
            items = listOf(listOf("Sean","Tess","Ulric","Vera"), listOf("Math","History","Economics","Philosophy"), listOf("Full","Associate","Assistant","Emeritus"), listOf("Alpha","Beta","Gamma","Delta")),
            clues = listOf("1. Sean teaches math.","2. The math professor is a Full professor.","3. Tess teaches history.","4. The history professor is an Associate professor.","5. Ulric teaches economics.","6. The economics professor is an Assistant professor.","7. Vera teaches philosophy.","8. The philosophy professor is Emeritus.","9. The math professor publishes in Alpha.","10. The history professor publishes in Beta."),
            solution = listOf(listOf(0,0,0,0), listOf(1,1,1,1), listOf(2,2,2,2), listOf(3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Musicians, Instruments, and Tours",
            description = "Four musicians each play an instrument, go on a tour, and perform in a venue.",
            categories = listOf("Person", "Instrument", "Tour", "Venue"),
            items = listOf(listOf("Walt","Xena","Yara","Zack"), listOf("Cello","Flute","Trumpet","Harp"), listOf("Euro","Asia","Americas","Pacific"), listOf("Hall","Arena","Theatre","Stadium")),
            clues = listOf("1. Walt plays cello.","2. The cellist does the Euro tour.","3. Xena plays flute.","4. The flutist does the Asia tour.","5. Yara plays trumpet.","6. The trumpeter does the Americas tour.","7. Zack plays harp.","8. The harpist does the Pacific tour.","9. The cellist plays in a hall.","10. The flutist plays in an arena."),
            solution = listOf(listOf(0,0,0,0), listOf(1,1,1,1), listOf(2,2,2,2), listOf(3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Runners, Distances, and Shoes",
            description = "Four runners each race a distance, wear a shoe brand, and train in a park.",
            categories = listOf("Person", "Distance", "Shoe", "Park"),
            items = listOf(listOf("Abby","Bart","Cass","Drew"), listOf("5K","10K","Half","Full"), listOf("Stride","Pace","Rush","Sprint"), listOf("Oak","Pine","Maple","Birch")),
            clues = listOf("1. Abby runs the 5K.","2. The 5K runner wears Stride shoes.","3. Bart runs the 10K.","4. The 10K runner wears Pace shoes.","5. Cass runs the half marathon.","6. The half-marathon runner wears Rush shoes.","7. Drew runs the full marathon.","8. The full-marathon runner wears Sprint shoes.","9. The 5K runner trains in Oak Park.","10. The 10K runner trains in Pine Park."),
            solution = listOf(listOf(0,0,0,0), listOf(1,1,1,1), listOf(2,2,2,2), listOf(3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Detectives, Cases, and Districts",
            description = "Four detectives each solve a case, find a clue, and work in a district.",
            categories = listOf("Person", "Case", "Clue", "District"),
            items = listOf(listOf("Elsa","Fred","Gina","Hans"), listOf("Theft","Murder","Fraud","Missing"), listOf("Fingerprint","Witness","Document","Photo"), listOf("East","West","North","South")),
            clues = listOf("1. Elsa solves the theft case.","2. The theft clue is a fingerprint.","3. Fred solves the murder case.","4. The murder clue is a witness.","5. Gina solves the fraud case.","6. The fraud clue is a document.","7. Hans solves the missing persons case.","8. The missing persons clue is a photo.","9. The theft detective works in the East district.","10. The murder detective works in the West district."),
            solution = listOf(listOf(0,0,0,0), listOf(1,1,1,1), listOf(2,2,2,2), listOf(3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Poets, Forms, and Themes",
            description = "Four poets each write in a form, explore a theme, and publish in a season.",
            categories = listOf("Person", "Form", "Theme", "Season"),
            items = listOf(listOf("Iris","Jake","Kara","Leo"), listOf("Sonnet","Haiku","Ode","Ballad"), listOf("Love","Nature","Loss","Joy"), listOf("Spring","Summer","Autumn","Winter")),
            clues = listOf("1. Iris writes sonnets.","2. The sonnet theme is love.","3. Jake writes haiku.","4. The haiku theme is nature.","5. Kara writes odes.","6. The ode theme is loss.","7. Leo writes ballads.","8. The ballad theme is joy.","9. The sonnet is published in spring.","10. The haiku is published in summer."),
            solution = listOf(listOf(0,0,0,0), listOf(1,1,1,1), listOf(2,2,2,2), listOf(3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Astronomers, Telescopes, and Stars",
            description = "Four astronomers each use a telescope, work at an observatory, and study a star type.",
            categories = listOf("Person", "Telescope", "Observatory", "StarType"),
            items = listOf(listOf("Mona","Nate","Ona","Pete"), listOf("Hubble","Keck","VLT","Chandra"), listOf("Hawaii","Chile","Arizona","Space"), listOf("Dwarf","Giant","Supergiant","Neutron")),
            clues = listOf("1. Mona uses Hubble.","2. Hubble operates in Space.","3. Nate uses Keck.","4. Keck is in Hawaii.","5. Ona uses VLT.","6. VLT is in Chile.","7. Pete uses Chandra.","8. Chandra operates in Arizona.","9. Hubble studies dwarf stars.","10. Keck studies giant stars.","11. VLT studies supergiants.","12. Chandra studies neutron stars."),
            solution = listOf(listOf(0,0,3,0), listOf(1,1,0,1), listOf(2,2,1,2), listOf(3,3,2,3))
        )
    )

    // MASTER: 5 categories x 4 items, very complex multi-step deduction
    private val MASTER = listOf(
        LogicGridPuzzle(
            title = "Jobs, Cities, Pets, and Hobbies",
            description = "Four friends each have a job, live in a city, own a pet, and have a hobby.",
            categories = listOf("Person", "Job", "City", "Pet", "Hobby"),
            items = listOf(listOf("Arlo","Bess","Cole","Dana"), listOf("Doctor","Lawyer","Chef","Teacher"), listOf("Oslo","Lima","Cairo","Delhi"), listOf("Cat","Dog","Bird","Fish"), listOf("Chess","Running","Painting","Reading")),
            clues = listOf("1. Arlo is a doctor.","2. The doctor lives in Oslo.","3. Bess is a lawyer.","4. The lawyer lives in Lima.","5. Cole is a chef.","6. The chef lives in Cairo.","7. Dana is a teacher.","8. The teacher lives in Delhi.","9. The doctor owns a cat.","10. The lawyer owns a dog.","11. The chef owns a bird.","12. The doctor plays chess."),
            solution = listOf(listOf(0,0,0,0,0), listOf(1,1,1,1,1), listOf(2,2,2,2,2), listOf(3,3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Sports, Countries, Foods, and Days",
            description = "Four athletes each play a sport, are from a country, eat a food, and train on a day.",
            categories = listOf("Person", "Sport", "Country", "Food", "Day"),
            items = listOf(listOf("Eli","Flo","Gil","Hana"), listOf("Tennis","Soccer","Golf","Swim"), listOf("UK","France","Spain","Italy"), listOf("Pasta","Crepe","Tapas","Pizza"), listOf("Mon","Tue","Wed","Thu")),
            clues = listOf("1. Eli plays tennis.","2. The tennis player is from the UK.","3. Flo plays soccer.","4. The soccer player is from France.","5. Gil plays golf.","6. The golf player is from Spain.","7. Hana swims.","8. The swimmer is from Italy.","9. The UK player eats pasta.","10. The French player eats crepes.","11. The Spanish player eats tapas.","12. The tennis player trains on Monday."),
            solution = listOf(listOf(0,0,0,0,0), listOf(1,1,1,1,1), listOf(2,2,2,2,2), listOf(3,3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Instruments, Genres, Ages, and Bands",
            description = "Four musicians each play an instrument, prefer a genre, are an age, and are in a band.",
            categories = listOf("Person", "Instrument", "Genre", "Age", "Band"),
            items = listOf(listOf("Ivan","Jade","Kurt","Luna"), listOf("Piano","Guitar","Drums","Violin"), listOf("Jazz","Rock","Pop","Classical"), listOf("20","25","30","35"), listOf("Alpha","Beta","Gamma","Delta")),
            clues = listOf("1. Ivan plays piano.","2. The pianist plays jazz.","3. Jade plays guitar.","4. The guitarist plays rock.","5. Kurt plays drums.","6. The drummer plays pop.","7. Luna plays violin.","8. The violinist plays classical.","9. The pianist is 20 years old.","10. The guitarist is 25 years old.","11. The drummer is 30 years old.","12. The pianist is in Alpha band."),
            solution = listOf(listOf(0,0,0,0,0), listOf(1,1,1,1,1), listOf(2,2,2,2,2), listOf(3,3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Languages, Scripts, Nations, and Capitals",
            description = "Four linguists each study a language, script, nation, and its capital.",
            categories = listOf("Person", "Language", "Script", "Nation", "Capital"),
            items = listOf(listOf("Max","Nina","Otto","Petra"), listOf("Arabic","Chinese","Hindi","Greek"), listOf("Arabic","Hanzi","Devanagari","Greek"), listOf("Egypt","China","India","Greece"), listOf("Cairo","Beijing","Delhi","Athens")),
            clues = listOf("1. Max studies Arabic.","2. Arabic uses the Arabic script.","3. Nina studies Chinese.","4. Chinese uses Hanzi.","5. Otto studies Hindi.","6. Hindi uses Devanagari.","7. Petra studies Greek.","8. Greek uses the Greek script.","9. Arabic is spoken in Egypt.","10. Chinese is spoken in China.","11. Hindi is spoken in India.","12. The Arabic-speaking nation's capital is Cairo."),
            solution = listOf(listOf(0,0,0,0,0), listOf(1,1,1,1,1), listOf(2,2,2,2,2), listOf(3,3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Animals, Habitats, Diets, and Colors",
            description = "Four zoologists each study an animal with a habitat, diet, and color.",
            categories = listOf("Person", "Animal", "Habitat", "Diet", "Color"),
            items = listOf(listOf("Quinn","Rana","Seth","Tara"), listOf("Eagle","Shark","Tiger","Frog"), listOf("Sky","Ocean","Forest","Swamp"), listOf("Meat","Fish","Deer","Insects"), listOf("Brown","Blue","Orange","Green")),
            clues = listOf("1. Quinn studies the eagle.","2. The eagle lives in the sky.","3. Rana studies the shark.","4. The shark lives in the ocean.","5. Seth studies the tiger.","6. The tiger lives in the forest.","7. Tara studies the frog.","8. The frog lives in the swamp.","9. The eagle eats meat.","10. The shark eats fish.","11. The tiger hunts deer.","12. The eagle is brown."),
            solution = listOf(listOf(0,0,0,0,0), listOf(1,1,1,1,1), listOf(2,2,2,2,2), listOf(3,3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Seasons, Fruits, Sports, and Clothes",
            description = "Four friends each love a season, fruit, sport, and clothing style.",
            categories = listOf("Person", "Season", "Fruit", "Sport", "Clothing"),
            items = listOf(listOf("Uma","Vera","Will","Xara"), listOf("Spring","Summer","Autumn","Winter"), listOf("Apple","Banana","Cherry","Date"), listOf("Tennis","Soccer","Golf","Swim"), listOf("Casual","Sporty","Formal","Cozy")),
            clues = listOf("1. Uma loves spring.","2. The spring person eats apple.","3. Vera loves summer.","4. The summer person eats banana.","5. Will loves autumn.","6. The autumn person eats cherry.","7. Xara loves winter.","8. The winter person eats date.","9. The spring person plays tennis.","10. The summer person plays soccer.","11. The autumn person plays golf.","12. The spring person dresses casually."),
            solution = listOf(listOf(0,0,0,0,0), listOf(1,1,1,1,1), listOf(2,2,2,2,2), listOf(3,3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Books, Genres, Authors, and Lengths",
            description = "Four readers each read a book, genre, author nationality, and book length.",
            categories = listOf("Person", "Book", "Genre", "Author", "Length"),
            items = listOf(listOf("Yael","Zane","Abby","Bram"), listOf("Atlas","Brief","Canon","Draft"), listOf("History","Science","Fiction","Poetry"), listOf("British","French","American","German"), listOf("Short","Medium","Long","Epic")),
            clues = listOf("1. Yael reads Atlas.","2. Atlas is a history book.","3. Zane reads Brief.","4. Brief is a science book.","5. Abby reads Canon.","6. Canon is fiction.","7. Bram reads Draft.","8. Draft is poetry.","9. Atlas is by a British author.","10. Brief is by a French author.","11. Canon is by an American author.","12. Atlas is a short book."),
            solution = listOf(listOf(0,0,0,0,0), listOf(1,1,1,1,1), listOf(2,2,2,2,2), listOf(3,3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Planets, Sizes, Moons, and Distances",
            description = "Four astronomers each study a planet with a size, moon count, and distance.",
            categories = listOf("Person", "Planet", "Size", "Moons", "Distance"),
            items = listOf(listOf("Cara","Dale","Elsa","Finn"), listOf("Mars","Saturn","Neptune","Venus"), listOf("Small","Huge","Large","Tiny"), listOf("Two","Many","Few","None"), listOf("Near","Far","Farther","Nearest")),
            clues = listOf("1. Cara studies Mars.","2. Mars is small.","3. Dale studies Saturn.","4. Saturn is huge.","5. Elsa studies Neptune.","6. Neptune is large.","7. Finn studies Venus.","8. Venus is tiny.","9. Mars has two moons.","10. Saturn has many moons.","11. Neptune has few moons.","12. Mars is near."),
            solution = listOf(listOf(0,0,0,0,0), listOf(1,1,1,1,1), listOf(2,2,2,2,2), listOf(3,3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Drinks, Cafes, Snacks, and Times",
            description = "Four friends each have a drink, visit a cafe, eat a snack, and go at a time.",
            categories = listOf("Person", "Drink", "Cafe", "Snack", "Time"),
            items = listOf(listOf("Gina","Hugo","Ida","Jack"), listOf("Espresso","Latte","Mocha","Tea"), listOf("Bean","Brew","Cup","Drip"), listOf("Cake","Cookie","Muffin","Scone"), listOf("Morning","Noon","Evening","Night")),
            clues = listOf("1. Gina drinks espresso.","2. Gina visits Bean cafe.","3. Hugo drinks latte.","4. Hugo visits Brew cafe.","5. Ida drinks mocha.","6. Ida visits Cup cafe.","7. Jack drinks tea.","8. Jack visits Drip cafe.","9. The espresso drinker eats cake.","10. The latte drinker eats a cookie.","11. The mocha drinker eats a muffin.","12. The espresso drinker goes in the morning."),
            solution = listOf(listOf(0,0,0,0,0), listOf(1,1,1,1,1), listOf(2,2,2,2,2), listOf(3,3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Schools, Clubs, Awards, and Cities",
            description = "Four students each attend a school, join a club, win an award, and live in a city.",
            categories = listOf("Person", "School", "Club", "Award", "City"),
            items = listOf(listOf("Kyla","Liam","Maya","Noah"), listOf("Alpha","Beta","Gamma","Delta"), listOf("Chess","Art","Music","Drama"), listOf("Star","Moon","Sun","Cloud"), listOf("Oslo","Lima","Cairo","Delhi")),
            clues = listOf("1. Kyla goes to Alpha.","2. Alpha students join chess club.","3. Liam goes to Beta.","4. Beta students join art club.","5. Maya goes to Gamma.","6. Gamma students join music club.","7. Noah goes to Delta.","8. Delta students join drama club.","9. Alpha students win the Star award.","10. Beta students win the Moon award.","11. Gamma students win the Sun award.","12. Kyla lives in Oslo."),
            solution = listOf(listOf(0,0,0,0,0), listOf(1,1,1,1,1), listOf(2,2,2,2,2), listOf(3,3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Rooms, Colors, Furniture, and Styles",
            description = "Four designers each decorate a room with a color, furniture, and style.",
            categories = listOf("Person", "Room", "Color", "Furniture", "Style"),
            items = listOf(listOf("Olga","Paul","Quin","Rita"), listOf("Kitchen","Bedroom","Living","Bath"), listOf("White","Grey","Blue","Red"), listOf("Table","Bed","Sofa","Shelf"), listOf("Modern","Classic","Minimal","Rustic")),
            clues = listOf("1. Olga decorates the kitchen.","2. The kitchen uses white.","3. Paul decorates the bedroom.","4. The bedroom is grey.","5. Quin decorates the living room.","6. The living room is blue.","7. Rita decorates the bath.","8. The bath is red.","9. The kitchen has a table.","10. The bedroom has a bed.","11. The living room has a sofa.","12. The kitchen style is modern."),
            solution = listOf(listOf(0,0,0,0,0), listOf(1,1,1,1,1), listOf(2,2,2,2,2), listOf(3,3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Chefs, Cuisines, Venues, and Techniques",
            description = "Four chefs each specialize in a cuisine, work at a venue, and use a technique.",
            categories = listOf("Person", "Cuisine", "Venue", "Technique", "Specialty"),
            items = listOf(listOf("Sean","Tess","Ulric","Vera"), listOf("Thai","French","Japanese","Mexican"), listOf("Hotel","Bistro","Sushi-bar","Cantina"), listOf("Wok","Sous-vide","Grill","Steam"), listOf("Curry","Pastry","Sashimi","Tacos")),
            clues = listOf("1. Sean cooks Thai.","2. Sean works at the hotel.","3. Tess cooks French.","4. Tess works at the bistro.","5. Ulric cooks Japanese.","6. Ulric works at the sushi bar.","7. Vera cooks Mexican.","8. Vera works at the cantina.","9. The Thai chef uses a wok.","10. The French chef uses sous-vide.","11. The Japanese chef grills.","12. The Thai chef's specialty is curry."),
            solution = listOf(listOf(0,0,0,0,0), listOf(1,1,1,1,1), listOf(2,2,2,2,2), listOf(3,3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Festivals, Months, Foods, and Venues",
            description = "Four friends each attend a festival, in a month, eat a food, at a venue.",
            categories = listOf("Person", "Festival", "Month", "Food", "Venue"),
            items = listOf(listOf("Walt","Xena","Yara","Zack"), listOf("Jazz","Film","Food","Art"), listOf("Jan","Apr","Jul","Oct"), listOf("Tacos","Crepes","Curry","Sushi"), listOf("Club","Cinema","Market","Gallery")),
            clues = listOf("1. Walt goes to the jazz festival.","2. The jazz festival is in January.","3. Xena goes to the film festival.","4. The film festival is in April.","5. Yara goes to the food festival.","6. The food festival is in July.","7. Zack goes to the art festival.","8. The art festival is in October.","9. The jazz festival serves tacos.","10. The film festival serves crepes.","11. The food festival serves curry.","12. The jazz festival is at a club."),
            solution = listOf(listOf(0,0,0,0,0), listOf(1,1,1,1,1), listOf(2,2,2,2,2), listOf(3,3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Dances, Music, Countries, and Costumes",
            description = "Four dancers each perform a dance, use a music type, from a country, in a costume.",
            categories = listOf("Person", "Dance", "Music", "Country", "Costume"),
            items = listOf(listOf("Abby","Bart","Cass","Drew"), listOf("Tango","Waltz","Flamenco","Salsa"), listOf("Bandoneon","Violin","Guitar","Trumpet"), listOf("Argentina","Austria","Spain","Cuba"), listOf("Black","White","Red","Yellow")),
            clues = listOf("1. Abby dances tango.","2. Tango uses the bandoneon.","3. Bart dances waltz.","4. Waltz uses violin.","5. Cass dances flamenco.","6. Flamenco uses guitar.","7. Drew dances salsa.","8. Salsa uses trumpet.","9. Tango is from Argentina.","10. Waltz is from Austria.","11. Flamenco is from Spain.","12. The tango dancer wears black."),
            solution = listOf(listOf(0,0,0,0,0), listOf(1,1,1,1,1), listOf(2,2,2,2,2), listOf(3,3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Painters, Styles, Media, and Museums",
            description = "Four painters each work in a style, use a medium, and show at a museum.",
            categories = listOf("Person", "Style", "Medium", "Museum", "Theme"),
            items = listOf(listOf("Elsa","Fred","Gina","Hans"), listOf("Abstract","Realist","Surreal","Impressionist"), listOf("Oil","Watercolor","Acrylic","Pastel"), listOf("Tate","Louvre","MoMA","Uffizi"), listOf("Nature","People","Dreams","Light")),
            clues = listOf("1. Elsa paints abstract.","2. The abstract artist uses oil.","3. Fred paints realism.","4. The realist uses watercolor.","5. Gina paints surrealism.","6. The surrealist uses acrylic.","7. Hans paints impressionism.","8. The impressionist uses pastel.","9. The abstract artist shows at the Tate.","10. The realist shows at the Louvre.","11. The surrealist shows at MoMA.","12. The abstract artist's theme is nature."),
            solution = listOf(listOf(0,0,0,0,0), listOf(1,1,1,1,1), listOf(2,2,2,2,2), listOf(3,3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Athletes, Records, Venues, and Years",
            description = "Four athletes each set a record, at a venue, in a sport, and in a year.",
            categories = listOf("Person", "Sport", "Venue", "Year", "Record"),
            items = listOf(listOf("Iris","Jake","Kara","Leo"), listOf("Swimming","Cycling","Running","Rowing"), listOf("Pool","Track","Road","Lake"), listOf("2018","2019","2020","2021"), listOf("World","National","Regional","Club")),
            clues = listOf("1. Iris sets the swimming record.","2. Swimming takes place at the pool.","3. Jake sets the cycling record.","4. Cycling takes place on the track.","5. Kara sets the running record.","6. Running takes place on the road.","7. Leo sets the rowing record.","8. Rowing takes place on a lake.","9. The swimming record was set in 2018.","10. The cycling record was set in 2019.","11. The running record was set in 2020.","12. The swimming record is a world record."),
            solution = listOf(listOf(0,0,0,0,0), listOf(1,1,1,1,1), listOf(2,2,2,2,2), listOf(3,3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Travellers, Modes, Routes, and Durations",
            description = "Four travellers each use a transport, take a route, and travel for a duration.",
            categories = listOf("Person", "Mode", "Route", "Duration", "Luggage"),
            items = listOf(listOf("Mona","Nate","Ona","Pete"), listOf("Ship","Car","Helicopter","Balloon"), listOf("Coast","Highway","Sky","River"), listOf("Week","Day","Hour","Month"), listOf("Heavy","Light","None","Medium")),
            clues = listOf("1. Mona travels by ship.","2. The ship takes the coast route.","3. Nate travels by car.","4. The car takes the highway.","5. Ona travels by helicopter.","6. The helicopter takes the sky route.","7. Pete travels by balloon.","8. The balloon follows the river.","9. The ship journey lasts a week.","10. The car journey takes a day.","11. The helicopter journey takes an hour.","12. The ship traveller carries heavy luggage."),
            solution = listOf(listOf(0,0,0,0,0), listOf(1,1,1,1,1), listOf(2,2,2,2,2), listOf(3,3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Scientists, Fields, Discoveries, and Awards",
            description = "Four scientists each work in a field, make a discovery, and win an award.",
            categories = listOf("Person", "Field", "Discovery", "Award", "Institute"),
            items = listOf(listOf("Quinn","Rana","Seth","Tara"), listOf("Physics","Chemistry","Biology","Astronomy"), listOf("Quark","Enzyme","Gene","Pulsar"), listOf("Nobel","Medal","Prize","Grant"), listOf("MIT","Oxford","Harvard","Cambridge")),
            clues = listOf("1. Quinn works in physics.","2. The physics discovery is the quark.","3. Rana works in chemistry.","4. The chemistry discovery is the enzyme.","5. Seth works in biology.","6. The biology discovery is the gene.","7. Tara works in astronomy.","8. The astronomy discovery is the pulsar.","9. The physics scientist wins the Nobel.","10. The chemistry scientist wins the Medal.","11. The biology scientist wins the Prize.","12. The physics scientist is at MIT."),
            solution = listOf(listOf(0,0,0,0,0), listOf(1,1,1,1,1), listOf(2,2,2,2,2), listOf(3,3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Games, Platforms, Genres, and Studios",
            description = "Four gamers each play a game on a platform in a genre from a studio.",
            categories = listOf("Person", "Game", "Platform", "Genre", "Studio"),
            items = listOf(listOf("Uma","Vera","Will","Xara"), listOf("Quest","Arena","Realm","Strike"), listOf("PC","Console","Mobile","VR"), listOf("RPG","FPS","Puzzle","Racing"), listOf("Apex","Nova","Crest","Dusk")),
            clues = listOf("1. Uma plays Quest.","2. Quest is on PC.","3. Vera plays Arena.","4. Arena is on Console.","5. Will plays Realm.","6. Realm is on Mobile.","7. Xara plays Strike.","8. Strike is on VR.","9. Quest is an RPG.","10. Arena is an FPS.","11. Realm is a puzzle game.","12. Quest is made by Apex studio."),
            solution = listOf(listOf(0,0,0,0,0), listOf(1,1,1,1,1), listOf(2,2,2,2,2), listOf(3,3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Birds, Habitats, Calls, and Colors",
            description = "Four ornithologists each study a bird with a habitat, call, and plumage color.",
            categories = listOf("Person", "Bird", "Habitat", "Call", "Color"),
            items = listOf(listOf("Yael","Zane","Abby","Bram"), listOf("Robin","Owl","Parrot","Eagle"), listOf("Garden","Forest","Jungle","Mountain"), listOf("Song","Hoot","Squawk","Screech"), listOf("Red","Brown","Green","Gold")),
            clues = listOf("1. Yael studies the robin.","2. Robins live in gardens.","3. Zane studies the owl.","4. Owls live in forests.","5. Abby studies the parrot.","6. Parrots live in jungles.","7. Bram studies the eagle.","8. Eagles live on mountains.","9. Robins sing.","10. Owls hoot.","11. Parrots squawk.","12. Robins have red plumage."),
            solution = listOf(listOf(0,0,0,0,0), listOf(1,1,1,1,1), listOf(2,2,2,2,2), listOf(3,3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Detectives, Cases, Clues, and Districts",
            description = "Four detectives each solve a case, find a clue, and work in a district.",
            categories = listOf("Person", "Case", "Clue", "District", "Partner"),
            items = listOf(listOf("Cara","Dale","Elsa","Finn"), listOf("Theft","Murder","Fraud","Missing"), listOf("Fingerprint","Witness","Document","Photo"), listOf("East","West","North","South"), listOf("Morgan","Taylor","Jordan","Casey")),
            clues = listOf("1. Cara solves the theft.","2. The theft clue is a fingerprint.","3. Dale solves the murder.","4. The murder clue is a witness.","5. Elsa solves the fraud.","6. The fraud clue is a document.","7. Finn solves the missing case.","8. The missing case clue is a photo.","9. The theft detective works East.","10. The murder detective works West.","11. The fraud detective works North.","12. Cara's partner is Morgan."),
            solution = listOf(listOf(0,0,0,0,0), listOf(1,1,1,1,1), listOf(2,2,2,2,2), listOf(3,3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Myths, Heroes, Powers, and Weapons",
            description = "Four mythology experts each study a myth, know a hero, power, and weapon.",
            categories = listOf("Person", "Myth", "Hero", "Power", "Weapon"),
            items = listOf(listOf("Gina","Hugo","Ida","Jack"), listOf("Greek","Norse","Egyptian","Celtic"), listOf("Achilles","Thor","Ra","Cuchulainn"), listOf("Strength","Thunder","Sun","Speed"), listOf("Spear","Hammer","Staff","Sword")),
            clues = listOf("1. Gina studies Greek myth.","2. The Greek hero is Achilles.","3. Hugo studies Norse myth.","4. The Norse hero is Thor.","5. Ida studies Egyptian myth.","6. The Egyptian hero is Ra.","7. Jack studies Celtic myth.","8. The Celtic hero is Cuchulainn.","9. Achilles has strength.","10. Thor commands thunder.","11. Ra controls the sun.","12. Achilles carries a spear."),
            solution = listOf(listOf(0,0,0,0,0), listOf(1,1,1,1,1), listOf(2,2,2,2,2), listOf(3,3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Architects, Styles, Buildings, and Cities",
            description = "Four architects each work in a style, design a building, and live in a city.",
            categories = listOf("Person", "Style", "Building", "City", "Era"),
            items = listOf(listOf("Kyla","Liam","Maya","Noah"), listOf("Gothic","Modern","Baroque","Minimal"), listOf("Church","Tower","Palace","Studio"), listOf("Berlin","Tokyo","Rome","Oslo"), listOf("Medieval","Contemporary","Renaissance","Postmodern")),
            clues = listOf("1. Kyla works in Gothic style.","2. Gothic architects design churches.","3. Liam works in Modern style.","4. Modern architects design towers.","5. Maya works in Baroque style.","6. Baroque architects design palaces.","7. Noah works in Minimal style.","8. Minimal architects design studios.","9. The Gothic architect lives in Berlin.","10. The Modern architect lives in Tokyo.","11. The Baroque architect lives in Rome.","12. Gothic style is Medieval."),
            solution = listOf(listOf(0,0,0,0,0), listOf(1,1,1,1,1), listOf(2,2,2,2,2), listOf(3,3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Runners, Distances, Shoes, and Parks",
            description = "Four runners each race a distance, wear a shoe, and train in a park.",
            categories = listOf("Person", "Distance", "Shoe", "Park", "Coach"),
            items = listOf(listOf("Olga","Paul","Quin","Rita"), listOf("5K","10K","Half","Full"), listOf("Stride","Pace","Rush","Sprint"), listOf("Oak","Pine","Maple","Birch"), listOf("Adams","Brown","Clark","Davis")),
            clues = listOf("1. Olga runs the 5K.","2. The 5K runner wears Stride.","3. Paul runs the 10K.","4. The 10K runner wears Pace.","5. Quin runs the half marathon.","6. The half-marathon runner wears Rush.","7. Rita runs the full marathon.","8. The full-marathon runner wears Sprint.","9. The 5K runner trains in Oak Park.","10. The 10K runner trains in Pine Park.","11. The half-marathon runner trains in Maple Park.","12. Olga's coach is Adams."),
            solution = listOf(listOf(0,0,0,0,0), listOf(1,1,1,1,1), listOf(2,2,2,2,2), listOf(3,3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Explorers, Continents, Artifacts, and Decades",
            description = "Four explorers each visit a continent, find an artifact, and travel in a decade.",
            categories = listOf("Person", "Continent", "Artifact", "Decade", "Ship"),
            items = listOf(listOf("Sean","Tess","Ulric","Vera"), listOf("Africa","Asia","Americas","Antarctica"), listOf("Mask","Scroll","Map","Crystal"), listOf("1960s","1970s","1980s","1990s"), listOf("Dawn","Horizon","Zenith","Apex")),
            clues = listOf("1. Sean explores Africa.","2. The Africa explorer finds a mask.","3. Tess explores Asia.","4. The Asia explorer finds a scroll.","5. Ulric explores the Americas.","6. The Americas explorer finds a map.","7. Vera explores Antarctica.","8. The Antarctica explorer finds a crystal.","9. The Africa exploration was in the 1960s.","10. The Asia exploration was in the 1970s.","11. The Americas exploration was in the 1980s.","12. Sean's ship is the Dawn."),
            solution = listOf(listOf(0,0,0,0,0), listOf(1,1,1,1,1), listOf(2,2,2,2,2), listOf(3,3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Doctors, Specialties, Hospitals, and Conditions",
            description = "Four doctors each have a specialty, work at a hospital, and treat a condition.",
            categories = listOf("Person", "Specialty", "Hospital", "Condition", "Tool"),
            items = listOf(listOf("Walt","Xena","Yara","Zack"), listOf("Cardiology","Neurology","Oncology","Pediatrics"), listOf("City","General","Central","North"), listOf("Heart","Brain","Cancer","Flu"), listOf("Stethoscope","Scanner","Microscope","Otoscope")),
            clues = listOf("1. Walt is a cardiologist.","2. The cardiologist works at City Hospital.","3. Xena is a neurologist.","4. The neurologist works at General Hospital.","5. Yara is an oncologist.","6. The oncologist works at Central Hospital.","7. Zack is a pediatrician.","8. The pediatrician works at North Hospital.","9. The cardiologist treats heart conditions.","10. The neurologist treats brain conditions.","11. The oncologist treats cancer.","12. Walt uses a stethoscope."),
            solution = listOf(listOf(0,0,0,0,0), listOf(1,1,1,1,1), listOf(2,2,2,2,2), listOf(3,3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Poets, Forms, Themes, and Seasons",
            description = "Four poets each write in a form, explore a theme, and publish in a season.",
            categories = listOf("Person", "Form", "Theme", "Season", "Publisher"),
            items = listOf(listOf("Abby","Bart","Cass","Drew"), listOf("Sonnet","Haiku","Ode","Ballad"), listOf("Love","Nature","Loss","Joy"), listOf("Spring","Summer","Autumn","Winter"), listOf("Quill","Ink","Press","Page")),
            clues = listOf("1. Abby writes sonnets.","2. The sonnet theme is love.","3. Bart writes haiku.","4. The haiku theme is nature.","5. Cass writes odes.","6. The ode theme is loss.","7. Drew writes ballads.","8. The ballad theme is joy.","9. The sonnet is published in spring.","10. The haiku is published in summer.","11. The ode is published in autumn.","12. Abby publishes with Quill."),
            solution = listOf(listOf(0,0,0,0,0), listOf(1,1,1,1,1), listOf(2,2,2,2,2), listOf(3,3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Inventors, Inventions, Eras, and Nations",
            description = "Four inventors each create an invention in an era and a nation.",
            categories = listOf("Person", "Invention", "Era", "Nation", "Patent"),
            items = listOf(listOf("Elsa","Fred","Gina","Hans"), listOf("Engine","Radio","Vaccine","Telescope"), listOf("1800s","1900s","1700s","1600s"), listOf("UK","Italy","France","Netherlands"), listOf("Gold","Silver","Bronze","Copper")),
            clues = listOf("1. Elsa invented the engine.","2. The engine was invented in the 1800s.","3. Fred invented the radio.","4. The radio was invented in the 1900s.","5. Gina invented the vaccine.","6. The vaccine was invented in the 1700s.","7. Hans invented the telescope.","8. The telescope was invented in the 1600s.","9. The engine was invented in the UK.","10. The radio was invented in Italy.","11. The vaccine was invented in France.","12. Elsa holds a gold patent."),
            solution = listOf(listOf(0,0,0,0,0), listOf(1,1,1,1,1), listOf(2,2,2,2,2), listOf(3,3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Photographers, Subjects, Cameras, and Studios",
            description = "Four photographers each shoot a subject, use a camera, and work in a studio.",
            categories = listOf("Person", "Subject", "Camera", "Studio", "Style"),
            items = listOf(listOf("Iris","Jake","Kara","Leo"), listOf("Portrait","Landscape","Wildlife","Street"), listOf("DSLR","Film","Mirrorless","Drone"), listOf("Downtown","Uptown","Midtown","Suburb"), listOf("Color","BlackWhite","HDR","Film-grain")),
            clues = listOf("1. Iris shoots portraits.","2. The portrait photographer uses a DSLR.","3. Jake shoots landscapes.","4. The landscape photographer uses film.","5. Kara shoots wildlife.","6. The wildlife photographer uses mirrorless.","7. Leo shoots street photography.","8. The street photographer uses a drone.","9. The portrait studio is downtown.","10. The landscape studio is uptown.","11. The wildlife studio is midtown.","12. Iris shoots in color."),
            solution = listOf(listOf(0,0,0,0,0), listOf(1,1,1,1,1), listOf(2,2,2,2,2), listOf(3,3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Writers, Genres, Settings, and Awards",
            description = "Four writers each write a genre, set in a place, and win an award.",
            categories = listOf("Person", "Genre", "Setting", "Award", "Inspiration"),
            items = listOf(listOf("Mona","Nate","Ona","Pete"), listOf("Thriller","Romance","Fantasy","Horror"), listOf("City","Village","Castle","Forest"), listOf("Gold","Silver","Bronze","Crystal"), listOf("Travel","Love","Myth","Fear")),
            clues = listOf("1. Mona writes thrillers.","2. The thriller is set in a city.","3. Nate writes romance.","4. The romance is set in a village.","5. Ona writes fantasy.","6. The fantasy is set in a castle.","7. Pete writes horror.","8. The horror is set in a forest.","9. The thriller wins the Gold award.","10. The romance wins the Silver award.","11. The fantasy wins the Bronze award.","12. Mona is inspired by travel."),
            solution = listOf(listOf(0,0,0,0,0), listOf(1,1,1,1,1), listOf(2,2,2,2,2), listOf(3,3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Musicians, Instruments, Tours, and Venues",
            description = "Four musicians each play an instrument, go on a tour, and perform at a venue.",
            categories = listOf("Person", "Instrument", "Tour", "Venue", "Manager"),
            items = listOf(listOf("Quinn","Rana","Seth","Tara"), listOf("Cello","Flute","Trumpet","Harp"), listOf("Euro","Asia","Americas","Pacific"), listOf("Hall","Arena","Theatre","Stadium"), listOf("Adams","Brown","Clark","Davis")),
            clues = listOf("1. Quinn plays cello.","2. The cellist does the Euro tour.","3. Rana plays flute.","4. The flutist does the Asia tour.","5. Seth plays trumpet.","6. The trumpeter does the Americas tour.","7. Tara plays harp.","8. The harpist does the Pacific tour.","9. The cellist plays in a hall.","10. The flutist plays in an arena.","11. The trumpeter plays in a theatre.","12. Quinn's manager is Adams."),
            solution = listOf(listOf(0,0,0,0,0), listOf(1,1,1,1,1), listOf(2,2,2,2,2), listOf(3,3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Professors, Subjects, Ranks, and Journals",
            description = "Four professors each teach a subject, hold a rank, and publish in a journal.",
            categories = listOf("Person", "Subject", "Rank", "Journal", "University"),
            items = listOf(listOf("Uma","Vera","Will","Xara"), listOf("Math","History","Economics","Philosophy"), listOf("Full","Associate","Assistant","Emeritus"), listOf("Alpha","Beta","Gamma","Delta"), listOf("North","South","East","West")),
            clues = listOf("1. Uma teaches math.","2. The math professor is Full rank.","3. Vera teaches history.","4. The history professor is Associate rank.","5. Will teaches economics.","6. The economics professor is Assistant rank.","7. Xara teaches philosophy.","8. The philosophy professor is Emeritus.","9. The math professor publishes in Alpha.","10. The history professor publishes in Beta.","11. The economics professor publishes in Gamma.","12. Uma is at North University."),
            solution = listOf(listOf(0,0,0,0,0), listOf(1,1,1,1,1), listOf(2,2,2,2,2), listOf(3,3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Collectors, Items, Centuries, and Rooms",
            description = "Four collectors each collect an item from a century and display it in a room.",
            categories = listOf("Person", "Item", "Century", "Room", "Origin"),
            items = listOf(listOf("Yael","Zane","Abby","Bram"), listOf("Coin","Stamp","Map","Vase"), listOf("16th","17th","18th","19th"), listOf("Hall","Library","Study","Gallery"), listOf("Rome","Paris","London","Berlin")),
            clues = listOf("1. Yael collects coins.","2. The coins are from the 16th century.","3. Zane collects stamps.","4. The stamps are from the 17th century.","5. Abby collects maps.","6. The maps are from the 18th century.","7. Bram collects vases.","8. The vases are from the 19th century.","9. The coins are displayed in the hall.","10. The stamps are in the library.","11. The maps are in the study.","12. Yael's coins originate from Rome."),
            solution = listOf(listOf(0,0,0,0,0), listOf(1,1,1,1,1), listOf(2,2,2,2,2), listOf(3,3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Sailors, Ships, Seas, and Cargo",
            description = "Four sailors each captain a ship, sail a sea, and carry cargo.",
            categories = listOf("Person", "Ship", "Sea", "Cargo", "Flag"),
            items = listOf(listOf("Cara","Dale","Elsa","Finn"), listOf("Sloop","Brig","Galleon","Schooner"), listOf("Red","Black","White","Blue"), listOf("Grain","Timber","Coal","Silk"), listOf("Cross","Star","Stripe","Wave")),
            clues = listOf("1. Cara captains a sloop.","2. The sloop sails the Red Sea.","3. Dale captains a brig.","4. The brig sails the Black Sea.","5. Elsa captains a galleon.","6. The galleon sails the White Sea.","7. Finn captains a schooner.","8. The schooner sails the Blue Sea.","9. The sloop carries grain.","10. The brig carries timber.","11. The galleon carries coal.","12. Cara's flag has a cross."),
            solution = listOf(listOf(0,0,0,0,0), listOf(1,1,1,1,1), listOf(2,2,2,2,2), listOf(3,3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Guards, Gates, Weapons, and Shifts",
            description = "Four guards each watch a gate, carry a weapon, and work a shift.",
            categories = listOf("Person", "Gate", "Weapon", "Shift", "Rank"),
            items = listOf(listOf("Gina","Hugo","Ida","Jack"), listOf("North","East","South","West"), listOf("Sword","Bow","Spear","Shield"), listOf("Morning","Afternoon","Evening","Night"), listOf("Captain","Sergeant","Corporal","Private")),
            clues = listOf("1. Gina guards the north gate.","2. The north gate guard carries a sword.","3. Hugo guards the east gate.","4. The east gate guard carries a bow.","5. Ida guards the south gate.","6. The south gate guard carries a spear.","7. Jack guards the west gate.","8. The west gate guard carries a shield.","9. The north gate is guarded in the morning.","10. The east gate guard works afternoons.","11. The south gate guard works evenings.","12. Gina holds the rank of Captain."),
            solution = listOf(listOf(0,0,0,0,0), listOf(1,1,1,1,1), listOf(2,2,2,2,2), listOf(3,3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Farmers, Crops, Tools, and Seasons",
            description = "Four farmers each grow a crop, use a tool, and work in a season.",
            categories = listOf("Person", "Crop", "Tool", "Season", "Region"),
            items = listOf(listOf("Kyla","Liam","Maya","Noah"), listOf("Wheat","Corn","Rice","Barley"), listOf("Plow","Sickle","Hoe","Rake"), listOf("Spring","Summer","Autumn","Winter"), listOf("North","South","East","West")),
            clues = listOf("1. Kyla grows wheat.","2. Wheat is plowed in spring.","3. Liam grows corn.","4. Corn is harvested with a sickle.","5. Maya grows rice.","6. Rice is hoed in summer.","7. Noah grows barley.","8. Barley is raked in autumn.","9. The wheat farmer works in the north.","10. The corn farmer works in the south.","11. The rice farmer works in the east.","12. Kyla works in spring."),
            solution = listOf(listOf(0,0,0,0,0), listOf(1,1,1,1,1), listOf(2,2,2,2,2), listOf(3,3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Scholars, Topics, Libraries, and Journals",
            description = "Four scholars each research a topic, use a library, and publish in a journal.",
            categories = listOf("Person", "Topic", "Library", "Journal", "Degree"),
            items = listOf(listOf("Olga","Paul","Quin","Rita"), listOf("History","Law","Medicine","Engineering"), listOf("National","City","Royal","University"), listOf("Chronicle","Record","Report","Digest"), listOf("PhD","Masters","Postdoc","Professor")),
            clues = listOf("1. Olga researches history.","2. The historian uses the National Library.","3. Paul researches law.","4. The law scholar uses the City Library.","5. Quin researches medicine.","6. The medicine scholar uses the Royal Library.","7. Rita researches engineering.","8. The engineer uses the University Library.","9. The historian publishes in Chronicle.","10. The law scholar publishes in Record.","11. The medicine scholar publishes in Report.","12. Olga holds a PhD."),
            solution = listOf(listOf(0,0,0,0,0), listOf(1,1,1,1,1), listOf(2,2,2,2,2), listOf(3,3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Climbers, Mountains, Camps, and Equipment",
            description = "Four climbers each summit a mountain, use a camp, and carry equipment.",
            categories = listOf("Person", "Mountain", "Camp", "Equipment", "Season"),
            items = listOf(listOf("Sean","Tess","Ulric","Vera"), listOf("Alpha","Bravo","Charlie","Delta"), listOf("Red","Blue","Green","Gold"), listOf("Rope","Axe","Tent","Beacon"), listOf("Spring","Summer","Autumn","Winter")),
            clues = listOf("1. Sean climbs Alpha.","2. Alpha's base camp is Red.","3. Tess climbs Bravo.","4. Bravo's base camp is Blue.","5. Ulric climbs Charlie.","6. Charlie's base camp is Green.","7. Vera climbs Delta.","8. Delta's base camp is Gold.","9. The Alpha climber carries rope.","10. The Bravo climber carries an axe.","11. The Charlie climber carries a tent.","12. Sean climbs in spring."),
            solution = listOf(listOf(0,0,0,0,0), listOf(1,1,1,1,1), listOf(2,2,2,2,2), listOf(3,3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Riders, Horses, Tracks, and Colors",
            description = "Four riders each have a horse, compete on a track, and wear a color.",
            categories = listOf("Person", "Horse", "Track", "Color", "Trophy"),
            items = listOf(listOf("Walt","Xena","Yara","Zack"), listOf("Storm","Flash","Blaze","Arrow"), listOf("Oval","Straight","Cross","Hill"), listOf("Red","Blue","Green","Yellow"), listOf("Gold","Silver","Bronze","Crystal")),
            clues = listOf("1. Walt rides Storm.","2. Storm competes on the oval track.","3. Xena rides Flash.","4. Flash races on the straight track.","5. Yara rides Blaze.","6. Blaze races on the cross track.","7. Zack rides Arrow.","8. Arrow races on the hill track.","9. Storm's rider wears red.","10. Flash's rider wears blue.","11. Blaze's rider wears green.","12. Walt wins the gold trophy."),
            solution = listOf(listOf(0,0,0,0,0), listOf(1,1,1,1,1), listOf(2,2,2,2,2), listOf(3,3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Builders, Materials, Districts, and Tools",
            description = "Four builders each use a material, work in a district, and use a tool.",
            categories = listOf("Person", "Material", "District", "Tool", "Project"),
            items = listOf(listOf("Abby","Bart","Cass","Drew"), listOf("Brick","Wood","Stone","Steel"), listOf("North","South","East","West"), listOf("Chisel","Saw","Hammer","Drill"), listOf("House","Bridge","Tower","Road")),
            clues = listOf("1. Abby builds with brick.","2. The brick builder works in the north.","3. Bart builds with wood.","4. The wood builder works in the south.","5. Cass builds with stone.","6. The stone builder works in the east.","7. Drew builds with steel.","8. The steel builder works in the west.","9. The brick builder uses a chisel.","10. The wood builder uses a saw.","11. The stone builder uses a hammer.","12. Abby builds a house."),
            solution = listOf(listOf(0,0,0,0,0), listOf(1,1,1,1,1), listOf(2,2,2,2,2), listOf(3,3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Traders, Goods, Ports, and Seasons",
            description = "Four traders each sell goods, work at a port, and sail in a season.",
            categories = listOf("Person", "Goods", "Port", "Season", "Ship"),
            items = listOf(listOf("Elsa","Fred","Gina","Hans"), listOf("Silk","Spice","Gold","Ivory"), listOf("East","West","South","North"), listOf("Spring","Summer","Autumn","Winter"), listOf("Voyager","Pioneer","Meridian","Compass")),
            clues = listOf("1. Elsa sells silk.","2. Silk is traded at the East port.","3. Fred sells spice.","4. Spice is sold at the West port.","5. Gina sells gold.","6. Gold is traded at the South port.","7. Hans sells ivory.","8. Ivory is traded at the North port.","9. Silk traders sail in spring.","10. Spice traders sail in summer.","11. Gold traders sail in autumn.","12. Elsa's ship is the Voyager."),
            solution = listOf(listOf(0,0,0,0,0), listOf(1,1,1,1,1), listOf(2,2,2,2,2), listOf(3,3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Pilots, Routes, Planes, and Times",
            description = "Four pilots each fly a route, use a plane, and depart at a time.",
            categories = listOf("Person", "Route", "Plane", "Departure", "Airline"),
            items = listOf(listOf("Iris","Jake","Kara","Leo"), listOf("North","East","South","West"), listOf("Jet","Prop","Twin","Turbo"), listOf("AM","Noon","PM","Night"), listOf("Sky","Sun","Air","Fly")),
            clues = listOf("1. Iris flies north.","2. The north route uses a jet.","3. Jake flies east.","4. The east route uses a prop.","5. Kara flies south.","6. The south route uses a twin.","7. Leo flies west.","8. The west route uses a turbo.","9. The north route departs at AM.","10. The east route departs at noon.","11. The south route departs at PM.","12. Iris flies for Sky airline."),
            solution = listOf(listOf(0,0,0,0,0), listOf(1,1,1,1,1), listOf(2,2,2,2,2), listOf(3,3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Nurses, Wards, Shifts, and Conditions",
            description = "Four nurses each work in a ward, on a shift, and treat a condition.",
            categories = listOf("Person", "Ward", "Shift", "Condition", "Tool"),
            items = listOf(listOf("Mona","Nate","Ona","Pete"), listOf("Pediatric","Surgical","Emergency","Geriatric"), listOf("Day","Night","Evening","Morning"), listOf("Fever","Fracture","Injury","Dementia"), listOf("Thermometer","Bandage","Splint","Cane")),
            clues = listOf("1. Mona works in pediatrics.","2. The pediatric nurse works day shift.","3. Nate works in surgery.","4. The surgical nurse works nights.","5. Ona works in emergency.","6. The emergency nurse works evenings.","7. Pete works in geriatrics.","8. The geriatric nurse works mornings.","9. The pediatric ward handles fever.","10. The surgical ward handles fractures.","11. The emergency ward handles injuries.","12. Mona uses a thermometer."),
            solution = listOf(listOf(0,0,0,0,0), listOf(1,1,1,1,1), listOf(2,2,2,2,2), listOf(3,3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Chefs, Dishes, Restaurants, and Portions",
            description = "Four chefs each cook a dish, work at a restaurant, and serve a portion size.",
            categories = listOf("Person", "Dish", "Restaurant", "Portion", "Region"),
            items = listOf(listOf("Quinn","Rana","Seth","Tara"), listOf("Risotto","Ramen","Curry","Tagine"), listOf("Alto","Mido","Basso","Primo"), listOf("Small","Regular","Large","Tasting"), listOf("Italy","Japan","India","Morocco")),
            clues = listOf("1. Quinn cooks risotto.","2. The risotto is served at Alto.","3. Rana cooks ramen.","4. Ramen is served at Mido.","5. Seth cooks curry.","6. Curry is served at Basso.","7. Tara cooks tagine.","8. Tagine is served at Primo.","9. The risotto is a small portion.","10. The ramen is a regular portion.","11. The curry is a large portion.","12. Risotto is from Italy."),
            solution = listOf(listOf(0,0,0,0,0), listOf(1,1,1,1,1), listOf(2,2,2,2,2), listOf(3,3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Cooks, Pots, Kitchens, and Meals",
            description = "Four cooks each use a pot, cook in a kitchen style, and make a meal.",
            categories = listOf("Person", "Pot", "Kitchen", "Meal", "Technique"),
            items = listOf(listOf("Uma","Vera","Will","Xara"), listOf("Clay","Steel","Cast-iron","Copper"), listOf("Open","Closed","Camp","Professional"), listOf("Porridge","Broth","Stew","Roast"), listOf("Simmer","Boil","Fry","Bake")),
            clues = listOf("1. Uma uses a clay pot.","2. The clay pot makes porridge.","3. Vera uses a steel pot.","4. The steel pot is used in a closed kitchen.","5. Will uses cast-iron.","6. The cast-iron is used in a camp kitchen.","7. Xara uses copper.","8. The copper pot is used professionally.","9. The clay pot is in an open kitchen.","10. The steel pot makes broth.","11. The cast-iron makes stew.","12. Uma's technique is simmering."),
            solution = listOf(listOf(0,0,0,0,0), listOf(1,1,1,1,1), listOf(2,2,2,2,2), listOf(3,3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Hunters, Prey, Weapons, and Terrains",
            description = "Four hunters each track prey, use a weapon, and hunt in a terrain.",
            categories = listOf("Person", "Prey", "Weapon", "Terrain", "Season"),
            items = listOf(listOf("Yael","Zane","Abby","Bram"), listOf("Deer","Boar","Hare","Fox"), listOf("Rifle","Bow","Trap","Spear"), listOf("Forest","Hill","Plains","Marsh"), listOf("Autumn","Winter","Spring","Summer")),
            clues = listOf("1. Yael hunts deer.","2. The deer hunter uses a rifle.","3. Zane hunts boar.","4. The boar hunter uses a bow.","5. Abby hunts hare.","6. The hare hunter uses a trap.","7. Bram hunts fox.","8. The fox hunter uses a spear.","9. The deer hunter hunts in a forest.","10. The boar hunter hunts on hills.","11. The hare hunter hunts on plains.","12. Yael hunts in autumn."),
            solution = listOf(listOf(0,0,0,0,0), listOf(1,1,1,1,1), listOf(2,2,2,2,2), listOf(3,3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Painters, Canvases, Palettes, and Studios",
            description = "Four painters each use a canvas size, a color palette, and work in a studio.",
            categories = listOf("Person", "Canvas", "Palette", "Studio", "Style"),
            items = listOf(listOf("Cara","Dale","Elsa","Finn"), listOf("Small","Medium","Large","Huge"), listOf("Warm","Cool","Neutral","Vivid"), listOf("Home","Loft","Garden","Gallery"), listOf("Realism","Abstract","Impressionism","Expressionism")),
            clues = listOf("1. Cara uses a small canvas.","2. The small canvas uses warm colors.","3. Dale uses a medium canvas.","4. The medium canvas uses cool colors.","5. Elsa uses a large canvas.","6. The large canvas uses neutral colors.","7. Finn uses a huge canvas.","8. The huge canvas uses vivid colors.","9. The small canvas artist works at home.","10. The medium canvas artist works in a loft.","11. The large canvas artist works in a garden.","12. Cara paints in a realist style."),
            solution = listOf(listOf(0,0,0,0,0), listOf(1,1,1,1,1), listOf(2,2,2,2,2), listOf(3,3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Students, Schools, Clubs, and Cities",
            description = "Four students each attend a school, join a club, and live in a city.",
            categories = listOf("Person", "School", "Club", "City", "Hobby"),
            items = listOf(listOf("Gina","Hugo","Ida","Jack"), listOf("West","East","North","South"), listOf("Chess","Drama","Coding","Art"), listOf("Oslo","Lima","Cairo","Delhi"), listOf("Reading","Gaming","Cooking","Hiking")),
            clues = listOf("1. Gina attends West School.","2. West School students join chess club.","3. Hugo attends East School.","4. East School students join drama club.","5. Ida attends North School.","6. North School students join coding club.","7. Jack attends South School.","8. South School students join art club.","9. The chess club student lives in Oslo.","10. The drama club student lives in Lima.","11. The coding club student lives in Cairo.","12. Gina's hobby is reading."),
            solution = listOf(listOf(0,0,0,0,0), listOf(1,1,1,1,1), listOf(2,2,2,2,2), listOf(3,3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Trains, Routes, Classes, and Conductors",
            description = "Four train travellers each take a train on a route, sit in a class, with a conductor.",
            categories = listOf("Person", "Train", "Route", "Class", "Conductor"),
            items = listOf(listOf("Kyla","Liam","Maya","Noah"), listOf("Express","Local","Bullet","Freight"), listOf("North","South","East","West"), listOf("First","Second","Third","Cargo"), listOf("Williams","Johnson","Lee","Brown")),
            clues = listOf("1. Kyla takes the express.","2. The express goes north.","3. Liam takes the local.","4. The local goes south.","5. Maya takes the bullet train.","6. The bullet train goes east.","7. Noah takes the freight.","8. The freight goes west.","9. The express is first class.","10. The local is second class.","11. The bullet train is third class.","12. Kyla's conductor is Williams."),
            solution = listOf(listOf(0,0,0,0,0), listOf(1,1,1,1,1), listOf(2,2,2,2,2), listOf(3,3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Divers, Oceans, Depths, and Creatures",
            description = "Four divers each explore an ocean, reach a depth, and find a creature.",
            categories = listOf("Person", "Ocean", "Depth", "Creature", "Equipment"),
            items = listOf(listOf("Olga","Paul","Quin","Rita"), listOf("Pacific","Atlantic","Indian","Arctic"), listOf("Shallow","Mid","Deep","Abyss"), listOf("Shark","Whale","Octopus","Jellyfish"), listOf("Wetsuit","Drysuit","Rebreather","Submersible")),
            clues = listOf("1. Olga dives in the Pacific.","2. The Pacific dive is shallow.","3. Paul dives in the Atlantic.","4. The Atlantic dive is at mid depth.","5. Quin dives in the Indian Ocean.","6. The Indian Ocean dive is deep.","7. Rita dives in the Arctic.","8. The Arctic dive reaches the abyss.","9. Olga finds a shark.","10. Paul finds a whale.","11. Quin finds an octopus.","12. Olga wears a wetsuit."),
            solution = listOf(listOf(0,0,0,0,0), listOf(1,1,1,1,1), listOf(2,2,2,2,2), listOf(3,3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Cyclists, Bikes, Speeds, and Tracks",
            description = "Four cyclists each have a bike type, a speed class, and a track preference.",
            categories = listOf("Person", "Bike", "Speed", "Track", "Helmet"),
            items = listOf(listOf("Sean","Tess","Ulric","Vera"), listOf("Road","Trail","City","BMX"), listOf("Fast","Slow","Mid","Sprint"), listOf("Hill","Flat","Urban","Park"), listOf("Red","Blue","Green","Yellow")),
            clues = listOf("1. Sean rides a road bike.","2. Road bikes are fast.","3. Tess rides a trail bike.","4. Trail bikes go slow.","5. Ulric rides a city bike.","6. City bikes go at mid speed.","7. Vera rides BMX.","8. BMX riders sprint.","9. Road bikes ride hills.","10. Trail bikes ride flat terrain.","11. City bikes ride urban streets.","12. Sean wears a red helmet."),
            solution = listOf(listOf(0,0,0,0,0), listOf(1,1,1,1,1), listOf(2,2,2,2,2), listOf(3,3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Hobbyists, Activities, Locations, and Days",
            description = "Four hobbyists each have an activity, do it at a location, and on a day.",
            categories = listOf("Person", "Activity", "Location", "Day", "Partner"),
            items = listOf(listOf("Walt","Xena","Yara","Zack"), listOf("Chess","Yoga","Pottery","Archery"), listOf("Club","Studio","Workshop","Range"), listOf("Mon","Tue","Wed","Thu"), listOf("Sam","Lee","Kim","Pat")),
            clues = listOf("1. Walt plays chess.","2. Chess is played at the club.","3. Xena does yoga.","4. Yoga is done at a studio.","5. Yara does pottery.","6. Pottery is done at a workshop.","7. Zack does archery.","8. Archery is done at a range.","9. Chess is on Monday.","10. Yoga is on Tuesday.","11. Pottery is on Wednesday.","12. Walt's partner is Sam."),
            solution = listOf(listOf(0,0,0,0,0), listOf(1,1,1,1,1), listOf(2,2,2,2,2), listOf(3,3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Artisans, Crafts, Markets, and Regions",
            description = "Four artisans each practice a craft, sell at a market, and come from a region.",
            categories = listOf("Person", "Craft", "Market", "Region", "Material"),
            items = listOf(listOf("Abby","Bart","Cass","Drew"), listOf("Weaving","Pottery","Carving","Glasswork"), listOf("North","South","East","West"), listOf("Highland","Coastal","Forest","Desert"), listOf("Wool","Clay","Wood","Sand")),
            clues = listOf("1. Abby weaves.","2. Abby sells at the North market.","3. Bart makes pottery.","4. Bart sells at the South market.","5. Cass carves.","6. Cass sells at the East market.","7. Drew works with glass.","8. Drew sells at the West market.","9. The weaver is from the Highlands.","10. The potter is from the coast.","11. The carver is from the forest.","12. Abby uses wool."),
            solution = listOf(listOf(0,0,0,0,0), listOf(1,1,1,1,1), listOf(2,2,2,2,2), listOf(3,3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Vets, Animals, Clinics, and Treatments",
            description = "Four vets each treat an animal at a clinic using a treatment.",
            categories = listOf("Person", "Animal", "Clinic", "Treatment", "Specialty"),
            items = listOf(listOf("Elsa","Fred","Gina","Hans"), listOf("Dog","Cat","Horse","Bird"), listOf("City","Grove","Peak","Bay"), listOf("Surgery","Vaccine","Physio","Medication"), listOf("Canine","Feline","Equine","Avian")),
            clues = listOf("1. Elsa treats dogs.","2. Elsa works at City clinic.","3. Fred treats cats.","4. Fred works at Grove clinic.","5. Gina treats horses.","6. Gina works at Peak clinic.","7. Hans treats birds.","8. Hans works at Bay clinic.","9. The dog vet performs surgery.","10. The cat vet gives vaccines.","11. The horse vet does physio.","12. Elsa specializes in canines."),
            solution = listOf(listOf(0,0,0,0,0), listOf(1,1,1,1,1), listOf(2,2,2,2,2), listOf(3,3,3,3,3))
        ),
        LogicGridPuzzle(
            title = "Choristers, Voices, Conductors, and Cities",
            description = "Four choristers each have a voice type, work with a conductor, and sing in a city.",
            categories = listOf("Person", "Voice", "Conductor", "City", "Venue"),
            items = listOf(listOf("Iris","Jake","Kara","Leo"), listOf("Soprano","Alto","Tenor","Bass"), listOf("Wagner","Mozart","Bach","Handel"), listOf("Vienna","Paris","Milan","London"), listOf("Opera","Hall","Church","Theatre")),
            clues = listOf("1. Iris is a soprano.","2. The soprano works with Wagner.","3. Jake is an alto.","4. The alto works with Mozart.","5. Kara is a tenor.","6. The tenor works with Bach.","7. Leo is a bass.","8. The bass works with Handel.","9. The soprano sings in Vienna.","10. The alto sings in Paris.","11. The tenor sings in Milan.","12. Iris sings at the opera."),
            solution = listOf(listOf(0,0,0,0,0), listOf(1,1,1,1,1), listOf(2,2,2,2,2), listOf(3,3,3,3,3))
        )
    )

    fun get(difficulty: Int, index: Int): LogicGridPuzzle {
        val pool = when (difficulty) {
            0 -> EASY; 1 -> MEDIUM; 2 -> HARD; 3 -> EXPERT; else -> MASTER
        }
        return pool[index.coerceIn(0, pool.size - 1)]
    }
}
