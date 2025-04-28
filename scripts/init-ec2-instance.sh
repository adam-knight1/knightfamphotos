#!/bin/bash
set -e

echo "🔑 Logging into ECR…"
aws ecr get-login-password \
  --region us-east-2 \
  | docker login \
      --username AWS \
      --password-stdin 913524908137.dkr.ecr.us-east-2.amazonaws.com

echo "🐳 Pulling latest image…"
docker pull 913524908137.dkr.ecr.us-east-2.amazonaws.com/backend-app:latest

echo "♻️ Removing old container…"
docker rm -f knightfam || true

echo "🚀 Launching container on 80→8080…"
docker run -d \
  --name knightfam \
  --restart unless-stopped \
  -p 80:8080 \
  913524908137.dkr.ecr.us-east-2.amazonaws.com/backend-app:latest

echo "✅ Deploy complete."
