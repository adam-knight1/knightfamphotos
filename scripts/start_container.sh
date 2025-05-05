
#!/bin/bash
set -e  # Exit immediately if any command fails

echo "🔐 Logging into ECR..."
aws ecr get-login-password --region us-east-2 | docker login --username AWS --password-stdin 913524908137.dkr.ecr.us-east-2.amazonaws.com

echo "🐳 Pulling latest image..."
docker pull 913524908137.dkr.ecr.us-east-2.amazonaws.com/backend-app:latest

echo "🚀 Starting container..."
docker run -d --name knightfam -p 8080:8080 913524908137.dkr.ecr.us-east-2.amazonaws.com/backend-app:latest || {
  echo "❌ Failed to start Docker container"
  exit 1
}

echo "✅ Container started successfully."
