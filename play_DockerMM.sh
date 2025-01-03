#!/bin/bash
# play_DockerMM.sh

# docker-compose up -d && docker attach game
docker-compose up -d && docker exec -it game sh -c "java -cp '.:src/lib/*:src' MyMastermind --l 2"