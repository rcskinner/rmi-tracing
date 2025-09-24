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
# Access RMI Client via NodePort (or use port-forward)
# NodePort: http://localhost:30090
# Or port-forward: kubectl port-forward service/rmi-client 8080:8080

# Test hello endpoint
curl http://localhost:30090/api/hello

# Test RMI call
curl -X POST 'http://localhost:30090/api/add-user?userId=123&userData=test'
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

- **RMI Client**: 
  - Container Port: 8080
  - NodePort: 30090 (HTTP)
  - Debug Port: 30091 (for remote debugging)
- **RMI Server**: 
  - Container Port: 1099 (RMI)
  - NodePort: 30092 (RMI)
  - Debug Port: 30093 (for remote debugging)
