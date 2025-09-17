# RMI Tracing Sandbox

A simple Java RMI tracing sandbox with two services for testing distributed tracing functionality.

## Architecture

- **RMI Client**: Spring Boot application with two endpoints
  - `/api/hello` - Simple hello world endpoint
  - `/api/add-user` - Makes RMI call to RMI Server
- **RMI Server**: Spring Boot application with RMI service
  - RMI service that receives calls from RMI Client and logs events

## Quick Start

1. Build and deploy:
```bash
./build.sh
kubectl apply -f k8s/
```

2. Test the services:
```bash
# Port forward RMI Client
kubectl port-forward service/rmi-client 8080:8080

# Test hello endpoint
curl http://localhost:8080/api/hello

# Test RMI call
curl -X POST 'http://localhost:8080/api/add-user?userId=123&userData=test'
```

## Project Structure

```
├── rmi-client/                # RMI Client application
├── rmi-server/                # RMI Server application  
├── rmi-interface/             # Shared RMI interface
├── k8s/                       # Kubernetes manifests
└── build.sh                   # Build script
```

## Services

- **RMI Client**: Port 8080
- **RMI Server**: Port 8081 (HTTP), Port 1099 (RMI)
