#!/bin/bash

echo "Building RMI Tracing Services..."

# Build Docker images (JARs are built inside the containers)
echo "Building RMI Client Docker image..."
docker build -f rmi-client/Dockerfile -t rcskin/rmi-client:latest .
docker push rcskin/rmi-client:latest

# Build and push the Docker RMI Server Image
echo "Building RMI Server Docker image..."
docker build -f rmi-server/Dockerfile -t rcskin/rmi-server:latest .
docker push rcskin/rmi-server:latest


echo "Build complete!"
echo ""
echo "To deploy to Kubernetes:"
echo "kubectl apply -f k8s/configmap.yaml"
echo "kubectl apply -f k8s/"
echo ""
echo "To test RMI Client:"
echo "kubectl port-forward service/rmi-client 8080:8080"
echo "curl http://localhost:8080/api/hello"
echo ""
echo "To test RMI call:"
echo "curl -X POST 'http://localhost:8080/api/add-user?userId=123&userData=test'"
