# play_LocalMM.sh
#!/bin/bash

# Compile java files
javac -cp "src:src/lib/*" src/DAO/*.java src/DBConnectionManager/*.java src/Models/*.java src/View/*.java src/MyMastermind.java

# Run the program
java -cp "src:src/lib/*" MyMastermind "$@"
