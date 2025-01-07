# Welcome to My Mastermind in Java
## Overview
This project implementats the classic Mastermind number guessing game. The user plays against the program. Read more about it here: 
* https://en.wikipedia.org/wiki/Mastermind_(board_game)

Key features include:
- Developed in Java, adhering to Object-Oriented Programming (OOP).
- Stores game data, including player names, rounds, results, timestamps, secret codes, and guess histories, in an SQLite database.
- Docker-enabled for cross-platform compatibility and ease of use.

## Game Rules
The goal of Mastermind is to guess the secret code composed of four pieces, each ranging from 0 to 7. After each guess, feedback is provided:

- Correctly placed pieces: Numbers in the correct position.
- Misplaced pieces: Numbers that are correct but in the wrong position.
  
The user has 10 attempts by default (configurable) to guess the secret code.

## User Stories
#### Game Setup
1. As a player
- Enter name before starting the game
- Start the game with pre-defined difficulty level (gives feedback, 10 tries)
2. As a developer
- Game to support extendibility

#### Game Play
1. As a player
- Game randomly generates a secret code
- Feedback after each guess indicating number of correctly placed and misplaced pieces
- Game to validate input (4 digits, 0-7)
- Game ends after either code guess or exhausted attempts

#### Database and DB Management
1. As a player
- Game saves results, including name, gueses, and whether game was solved
2. As a developer
- Store the game data in a database
- Use a DAO pattern to abstract db operations (so that db inmplementation can be changed)

#### Deployment
1. As a player
- Game to be playable on any system via a Docker container so we don't have to worry about installing dependencies
2. As a developer
- Project to include all necessary libraries and JAR files so users don't need to download them manually
- Simple setup script to build and run the game

#### Error Handling
1. As a developer
- Meaningful error messages when the game encounters an issue so I understand what went wrong and how to fix it



## Installation and Operation
The game can be played through a command line interface (locally), or through a Docker container (requires Docker Desktop). The addition of Docker should allow a user to run my application on any system that supports Docker.


#### Requirements
1. Java: 
- To check if you have it installed, in the terminal:</br>


```
java -version
```
- If not installed, you can install here: https://www.java.com/en/

2. Docker Desktop: 
- This application manages containerization. To check if Docker is installed, in the terminal:
```
docker --version
```
- If not installed, you can download it here: https://www.docker.com/get-started/

#### Installation:
1. Clone the repository:
```
git clone https://github.com/aip956/Reach_Jan25
```

2. Navigate to the project directory:
```
cd MM_Reach_Jan25
```
</br>

### Running the Game
#### Local Execution

1. Enable the script (1st time): </br>
```
chmod +x ./play_LocalMM.sh
```

2. Run: </br>
```
./play_LocalMM.sh
```
#### Docker Execution
1. Start Docker Desktop
2. Make the script executable (1st time only)
```
chmod +x ./play_DockerMM.sh
```
3. Build and run the container
```
./play_DockerMM.sh
```


### View data locally:
1. Navigate to database directoryGame data is stored in an SQLite database. To view:
```
cd src/data
```

2. Open the SQL shell
```
sqlite3 MM_Reach.db
```

3. View the game data (Optional: Turn on headers)
```
.header on
SELECT * FROM game_data;
```
4. Exit the sql shell
```
.exit
```

### View Data in Docker:
1. Start game again, but don't play. Open another terminal and enter the bash shell:
```
docker exec -it game /bin/bash
```    
2. Navigate to the data dir and open the SQLite shell. Turn header view on, and view the data:
```
cd src/data
sqlite3 MM_Reach.db
.header on
SELECT * FROM game_data;

```
Exit the SQLite shell:
```
.exit
```

Exit the container's shell:
```
.exit
```

</br>
</br>

## Design
### Key Design Features
1. Object-Oriented Design
- The game is structured using classes that encapsulate the core logic and interactions
- Separation of concerns is maintained with packages like Models, View, DAO, DBConnectionManager.

2. Factory Pattern
- A factory design pattern is implemented in the DAOFactory class to create the appropriate GameDataDAO (e.g. SQLite). This design supports extendibility, enabling easy addition of other databases (e.g. MySQL).

3. Database Integration:
- Game data is stored persistently in an SQLite database using a GameDataDAO interface and its SQLiteGameDataDAO implementation.

4. Docker Integration:
- A Dockerfile defines the environment and dependencies
- A docker-compose.yaml simplifies starting, stopping, and rebuilding the application.



## Screen Captures

</br>

#### Run and play the game locally using the command line:</br> 
![Running locally on command line](./ScreenCaps/GamePlay.png)

</br>

#### Wrong input entered!</br> 
![Wrong input entered](./ScreenCaps/WrongInput.png) 
</br>

#### Viewing data in DB Browser:</br> 
![View data in DB Browser](./ScreenCaps/DB_Browser_Data.png)
</br>

#### Viewing data in the terminal</br> 
![View data in the terminal](./ScreenCaps/LocalSQLData.png)
</br>
</br>

### Running in Docker:
#### Build and Run Container:</br>

![Build and Run Container](./ScreenCaps/BuildAndRunContainer.png)
</br>

#### Playing in Docker:
- After building the container, game play is the same as running locally 
</br>

#### View Docker Data:</br> 
![Playing in Docker](./ScreenCaps/ViewDockerData.png)
</br>

#### Docker Desktop, Container:</br> 
![Playing in Docker](./ScreenCaps/DockerContainer.png)
</br>





#### Unified Modeling Language Diagram
![UML](./UML/Mastermind.png)


## Future improvements

1. Database Enhancements:
- Explore additional database backends like MySQL or PostgreSQL for scalability
- Implement methods for retrieving games by various criteria (e.g. player or solved status)
  
2. Automated Testing
- Add JUnit tests for core components, including models, game logic, and DAO implementations.

3. Debug Mode:
- Add a command-line flag for debugging the secret code at the beginning of the game.


Notes:
#### MyMastermind
Entry point to the game. Initializes the database path, db type, instantiates the gameDataDAO (to connect to the db), initializes the scanner, gameSetup (entry dialogue), players (can extend to multiplayer), SecretKeeper and SecretCode, starts the game, closes the scanner and database connection.

#### Controller
GameSetup: Dialogue to set up the player name. If there were multiple players or player levels, it would be handled here. It also contains the logic to turn on the debugger (if enabled in the MyMastermind class). It returns the list of players (and levels, if applicable).

LeaderboardManager: If there is a method in the SQLiteGameDataDAO to extract leaderboard data, would determine if the leaderboard was enabled (CLI flag) and displays the leaderboard data.

#### Models Package
Game: This class manages the game flow, including starting the game, managing the rounds, and finalizing the game data. It also includes the getters and setters. 

* Single Responsibility: Coordinates game logic
* Dependency Inversion Principle: Relies on abstractions by interacting with the Guesser, SecretKeeper, GameData, and GameDataDAO interfaces rather than concrete implementations


Player: The Player is an abstract class for players. Guesser will extend from Player.  
* Open/Closed Principle: It's easily extendable to add more player types without modifying existing code.
* Abstraction: It abstracts player details, providing a base for specific player types.

Guesser: The Guesser represents the player making guesses against the secret code. It extends from Player; it inputs the guess.
* Open/Closed Principle: New guessing strategies can be added without modifying existing code by extending this class.
* Liskov Substitution Principle: As a subclass of Player, it can be used anywhere a Player is expected without affecting the behavior negatively.

SecretKeeper: The SecretKeeper generates the secret. It fetches the secret from an API. If the API is not available, it will generate a local secret.

* Single Responsibility Principle: Focused on maintaining and validating the secret code.
* Dependency Inversion Principle: It uses Player as a base class, promoting use of abstractions over concrete classes.



PlayerLevel: If we extend the game to allow the player to select a different level, it would be managed here. The level indicates the maxAttempts and whether there is feedback ofter the guess.

#### Utils
ValidationUtils: Creates class to validate the guess



#### View Package
GameUI: The GameUI handles all user interactions, including displaying messages and capturing user input.
* Single Responsibility Principle: It's dedicated solely to user interface operations.

#### DAO Package
DAOFactory: This class allows easier switch to a different database. 

GameDataDAO: This class is an interface for data access operations related to GameData, such as saving and retrieving game data. (Other game retrieval methods would be defined here, like getGamesByPlayer)
* Interface Segregation Principle: Clients will not be forced to depend on methods they do not use.

SQLiteGameDataDAO: This class implements GameDataDAO, providing specific data operations using SQLite. (Other games retrieval methods would be implemented here.)
* Dependency Inversion Principle: It depends on the GameDataDAO abstraction, allowing for flexibility in data storage methods
* Single Responsibility Principle: It manages the database operations specific to GameData


#### DBConnectionManagerPackage
DatabaseConnectionManager: This class manages database connections, ensuring a single active connection or creating a new one as needed
* Single Responsiblity Principle: It centralizes the management of database connections, separating it from other database operations
* Singleton Pattern: It ensures that there is a single instance of the connection, reused throughout the application





#### SOLID Design Principles:
While my game design aims to be SOLID, I also needed to balance simplicity and scope. For example, 

Single Responsibility Principle
* A class should have only one job/responsibility
* However, some of my classes take on additional responsibilities to avoid excessive fragmentation and over-complication.
* Game handles the game loop, processing guesses, interfacing with GameUI; it mixes game logic with user interaction. 

Open-Closed Principle
* Entities (classes, functions, etc.) should be open for extension but closed for modification
* However, some of my classes also are not closed to modification; this is also for simplicity.
* Adding new players might change how the Game class operates.

Liskov Sustitution Principle
* Objects of a superclass should be replaceable with objects of subclasses
* However, subclasses of Player (e.g. AIPlayer) might not be used interchangeably without the Game class knowing the differences.

Interface Segregation Principle
* Clients should not be forced to depend on interfaces they don't use
* However, some interfaces implementations do not use all methods

Dependency Inversion Principle
* High-level modules should not depend on low-level modules; both should depend on abstractions
* However, a high-level module (Game) might depend on low-level ones (e.g. Guesser, SQLiteGameDataDAO) rather than abstractions

#### OOP Principles:
Abstraction
* Hide implementation details

Inheritance
* Allows one object to acquire the properties and methods of another

Polymorphism
* Allows an inherited object to have different method implementations

Encapsulation
* Each object should control its own state

#### Other:
Java:
* I've been learning Java and felt it a good language for this project. It's a mature language, with vast ecosystem (development tools, libraries, community, etc.). I also chose an OOP design as the modularity allows greater complexity management and code reusability.

Docker:
* I've added Dockerfile to allow a user to run my application on any system that supports Docker.
  * Dockerfile: Defines the environment, dependencies, and necessary commands
  * Although the game runs in a single container, I have a docker-compose.yaml which simplifies and centralizes the configurations, making it easier to scale the application (even within one container). It also allows starting, stopping and rebuilding with simple commands (e.g. docker-compse up, docker-compse down).

Database
* I chose SQLite for the database. I assumed the data volume would be low, and the data is fairly structured.

Logging
* Included in the game code is Logback. While I was coding it was helpful for debugging. I've kept it in the code so that if the code is extended, logging can continue to help debug.

I've also added a .gitignore file to prevent certain files from being committed to the git repository. This will help keep the repository clean and focused.