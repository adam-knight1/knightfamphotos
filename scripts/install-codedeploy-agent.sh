#!/bin/bash
set -e

echo "🔧 Updating packages..."
sudo dnf update -y

echo "🐳 Installing Docker..."
sudo dnf install -y docker

echo "🚀 Starting Docker..."
sudo systemctl start docker
sudo systemctl enable docker
sudo usermod -aG docker ec2-user

echo "💎 Installing Ruby & wget for CodeDeploy..."
sudo dnf install -y ruby wget
cd /home/ec2-user
wget https://aws-codedeploy-us-east-2.s3.us-east-2.amazonaws.com/latest/install
chmod +x ./install
sudo ./install auto

echo "✅ Bootstrap complete. Now running deploy script…"
# Call your deploy script (make sure it’s on the instance or pulled from S3/Git)
bash /home/ec2-user/deploy-knightfam.sh
