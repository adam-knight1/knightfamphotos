#!/bin/bash

set -e

echo "🚀 Installing AWS CodeDeploy Agent on Amazon Linux 2023..."

# Update package index
sudo dnf update -y

# Install Ruby and wget (required for CodeDeploy agent)
sudo dnf install -y ruby wget

# Download and install the CodeDeploy agent installer script
REGION="us-east-2"  # 🔧 Change this if you're using a different region
INSTALLER_URL="https://aws-codedeploy-${REGION}.s3.${REGION}.amazonaws.com/latest/install"

cd /home/ec2-user
wget ${INSTALLER_URL}
chmod +x ./install

# Run the installer
sudo ./install auto

# Start and enable the agent
sudo systemctl start codedeploy-agent
sudo systemctl enable codedeploy-agent

# Check status
echo "✅ CodeDeploy agent status:"
sudo systemctl status codedeploy-agent --no-pager

echo "🎉 CodeDeploy agent installation complete."
