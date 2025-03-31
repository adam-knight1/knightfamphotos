#!/bin/bash
docker pull 913524908137.dkr.ecr.us-east-2.amazonaws.com/backend-app:latest
docker run -d --name knightfam -p 8080:8080 913524908137.dkr.ecr.us-east-2.amazonaws.com/backend-app:latest
