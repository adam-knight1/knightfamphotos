
#!/bin/bash
# Authenticate Docker to ECR
aws ecr get-login-password --region us-east-2 | docker login --username AWS --password-stdin 913524908137.dkr.ecr.us-east-2.amazonaws.com

# Pull and run the container
docker pull 913524908137.dkr.ecr.us-east-2.amazonaws.com/backend-app:latest
docker run -d --name knightfam -p 8080:8080 913524908137.dkr.ecr.us-east-2.amazonaws.com/backend-app:latest

