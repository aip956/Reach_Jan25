# play_LocalMM.sh
#!/bin/bash

# Compile java files

javac -cp "src:src/lib/*" src/DAO/*.java src/DBConnectionManager/*.java src/Models/*.java src/View/*.java src/MyMastermind.java


# Check for leaderboard flag
if [[ $1 == "--leaderboard" || $1 == "-l" ]]; then
    # Pass leaderboard args to Java program
    java -cp "src:src/lib/*" MyMastermind "$@"
else
    # Run w/o leaderboard
    java -cp "src:src/lib/*" MyMastermind
fi