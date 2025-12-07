#!/bin/bash

set -e

echo "🚀 Deploying Microservices to Kubernetes..."
echo ""

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Check if kubectl is installed
if ! command -v kubectl &> /dev/null; then
    echo -e "${RED}❌ kubectl is not installed${NC}"
    exit 1
fi

# Check cluster connection
if ! kubectl cluster-info &> /dev/null; then
    echo -e "${RED}❌ Cannot connect to Kubernetes cluster${NC}"
    exit 1
fi

echo -e "${GREEN}✓ Connected to Kubernetes cluster${NC}"
echo ""

# Step 1: Create Namespace
echo -e "${YELLOW}📦 Creating namespace...${NC}"
kubectl apply -f 00-namespace.yaml
echo ""

# Step 2: Create Secrets
echo -e "${YELLOW}🔐 Creating secrets...${NC}"
kubectl apply -f 01-secrets.yaml
echo ""

# Step 3: Create ConfigMaps
echo -e "${YELLOW}⚙️  Creating ConfigMaps...${NC}"
kubectl apply -f 02-configmaps/
echo ""

# Step 4: Deploy Databases
echo -e "${YELLOW}🗄️  Deploying databases...${NC}"
kubectl apply -f 03-databases/

echo "Waiting for databases to be ready..."
kubectl wait --for=condition=ready pod -l app=auth-db -n microservices --timeout=300s
kubectl wait --for=condition=ready pod -l app=faculty-db -n microservices --timeout=300s
kubectl wait --for=condition=ready pod -l app=student-db -n microservices --timeout=300s
echo ""

# Step 5: Deploy RabbitMQ
echo -e "${YELLOW}🐰 Deploying RabbitMQ...${NC}"
kubectl apply -f 04-rabbitmq.yaml

echo "Waiting for RabbitMQ to be ready..."
kubectl wait --for=condition=ready pod -l app=rabbitmq -n microservices --timeout=300s
echo ""

# Step 6: Create RBAC
echo -e "${YELLOW}🔑 Creating RBAC resources...${NC}"
kubectl apply -f 05-rbac.yaml
echo ""

# Step 7: Deploy Microservices
echo -e "${YELLOW}🚢 Deploying microservices...${NC}"
kubectl apply -f 06-services/

echo "Waiting for services to be ready..."
sleep 10
kubectl wait --for=condition=ready pod -l app=auth-service -n microservices --timeout=300s
kubectl wait --for=condition=ready pod -l app=faculty-service -n microservices --timeout=300s
kubectl wait --for=condition=ready pod -l app=student-service -n microservices --timeout=300s
kubectl wait --for=condition=ready pod -l app=api-gateway -n microservices --timeout=300s
echo ""

# Step 8: Deploy Ingress (optional)
if [ -f "07-ingress.yaml" ]; then
    echo -e "${YELLOW}🌐 Deploying Ingress...${NC}"
    kubectl apply -f 07-ingress.yaml
    echo ""
fi

# Display status
echo -e "${GREEN}✅ Deployment Complete!${NC}"
echo ""
echo "📊 Current Status:"
kubectl get pods -n microservices
echo ""
echo "🔗 Services:"
kubectl get svc -n microservices
echo ""
echo "🌐 Get API Gateway URL:"
echo "kubectl get svc api-gateway -n microservices -o jsonpath='{.status.loadBalancer.ingress[0].ip}'"
echo ""
echo "📝 View logs:"
echo "kubectl logs -f deployment/api-gateway -n microservices"
echo ""
echo "🔍 Monitor pods:"
echo "kubectl get pods -n microservices -w"
