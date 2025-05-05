#!/bin/bash
set -e

echo "🛑 Stopping any existing container..."
docker stop knightfam || echo "⚠️ Container not running"
docker rm knightfam || echo "⚠️ Container not found"
