#!/bin/bash

echo "Building RMI Tracing Services..."

# Build Docker images in parallel (JARs are built inside the containers)
echo "Building Docker images in parallel..."
docker build -f rmi-client/Dockerfile -t rcskin/rmi-client:latest . &
docker build -f rmi-server/Dockerfile -t rcskin/rmi-server:latest . &

# Wait for both builds to complete
wait

echo "Pushing Docker images..."
docker push rcskin/rmi-client:latest &
docker push rcskin/rmi-server:latest &

# Wait for both pushes to complete
wait

# Update the Kubernetes Deployments
kubectl apply -f k8s/

# Force restart deployments to pick up new images
echo "Restarting deployments to pick up new images..."
kubectl rollout restart deployment/rmi-server -n rmi-tracing
kubectl rollout restart deployment/rmi-client -n rmi-tracing

echo "Build complete!"
echo ""
echo "To deploy to Kubernetes:"
echo "kubectl apply -f k8s/configmap.yaml"
echo "kubectl apply -f k8s/"
echo ""
echo "To test RMI Client:"
echo "NodePort access (local cluster):"
echo "curl http://localhost:30080/api/hello"
echo ""
echo "To test RMI call:"
echo "curl -X POST 'http://localhost:30080/api/add-user?userId=123&userData=test'"
echo ""