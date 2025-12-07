#!/bin/bash

set -e

echo "🧹 Cleaning up Kubernetes resources..."
echo ""

# Colors
RED='\033[0;31m'
YELLOW='\033[1;33m'
GREEN='\033[0;32m'
NC='\033[0m'

read -p "Are you sure you want to delete all resources? (yes/no): " confirm

if [ "$confirm" != "yes" ]; then
    echo "Cleanup cancelled."
    exit 0
fi

echo ""
echo -e "${YELLOW}Deleting resources in reverse order...${NC}"

# Delete Ingress
if [ -f "07-ingress.yaml" ]; then
    echo "Deleting Ingress..."
    kubectl delete -f 07-ingress.yaml --ignore-not-found=true
fi

# Delete Services
echo "Deleting microservices..."
kubectl delete -f 06-services/ --ignore-not-found=true

# Delete RBAC
echo "Deleting RBAC..."
kubectl delete -f 05-rbac.yaml --ignore-not-found=true

# Delete RabbitMQ
echo "Deleting RabbitMQ..."
kubectl delete -f 04-rabbitmq.yaml --ignore-not-found=true

# Delete Databases
echo "Deleting databases..."
kubectl delete -f 03-databases/ --ignore-not-found=true

# Delete ConfigMaps
echo "Deleting ConfigMaps..."
kubectl delete -f 02-configmaps/ --ignore-not-found=true

# Delete Secrets
echo "Deleting secrets..."
kubectl delete -f 01-secrets.yaml --ignore-not-found=true

# Delete Namespace (this will delete everything)
echo "Deleting namespace..."
kubectl delete -f 00-namespace.yaml --ignore-not-found=true

echo ""
echo -e "${GREEN}✅ Cleanup complete!${NC}"
echo ""
echo "Verify deletion:"
echo "kubectl get all -n microservices"
