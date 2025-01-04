#!/bin/bash
# play_DockerMM.sh

# Stop and remove old container
docker-compose down

# Rebuild and restart the container
docker-compose up --build  -d

# Start the game
docker exec -it game sh -c "java -cp '.:src/lib/*:src' MyMastermind --l 2"
# docker-compose up -d && docker exec -it game sh -c "java -cp '.:src/lib/*:src' MyMastermind --l 2"