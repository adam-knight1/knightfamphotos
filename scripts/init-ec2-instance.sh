#!/bin/bash

set -e

echo "🔧 Updating packages..."
sudo dnf update -y

echo "🐳 Installing Docker..."
sudo dnf install -y docker

echo "🚀 Starting and enabling Docker..."
sudo systemctl start docker
sudo systemctl enable docker

echo "👤 Adding ec2-user to Docker group..."
sudo usermod -aG docker ec2-user

echo "✅ Docker installation complete"
docker --version

echo "💎 Installing Ruby and wget (for CodeDeploy agent)..."
sudo dnf install -y ruby wget

echo "📦 Downloading CodeDeploy agent installer..."
REGION="us-east-2"  # Change region if different
cd /home/ec2-user
wget https://aws-codedeploy-${REGION}.s3.${REGION}.amazonaws.com/latest/install
chmod +x ./install

echo "🛠️ Installing CodeDeploy agent..."
sudo ./install auto

echo "🚦 Starting and enabling CodeDeploy agent..."
sudo systemctl start codedeploy-agent
sudo systemctl enable codedeploy-agent

echo "✅ CodeDeploy agent installation complete"
sudo systemctl status codedeploy-agent --no-pager

echo "🎉 EC2 instance initialized with Docker and CodeDeploy!"
