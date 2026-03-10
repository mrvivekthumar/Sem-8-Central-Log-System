#!/bin/bash

set -e

echo "Cleaning up ColabBridge Kubernetes resources..."
echo ""

GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

read -p "Delete all ColabBridge resources? (yes/no): " confirm

if [ "$confirm" != "yes" ]; then
    echo "Cleanup cancelled."
    exit 0
fi

echo ""
echo -e "${YELLOW}Deleting resources...${NC}"

[ -f "07-ingress.yaml" ] && kubectl delete -f 07-ingress.yaml --ignore-not-found=true
kubectl delete -f 06-services/ --ignore-not-found=true
[ -f "08-logging.yaml" ] && kubectl delete -f 08-logging.yaml --ignore-not-found=true
kubectl delete -f 05-rbac.yaml --ignore-not-found=true
kubectl delete -f 04-rabbitmq.yaml --ignore-not-found=true
kubectl delete -f 03-databases/ --ignore-not-found=true
kubectl delete -f 01-secrets.yaml --ignore-not-found=true
kubectl delete -f 00-namespace.yaml --ignore-not-found=true

echo ""
echo -e "${GREEN}Cleanup complete!${NC}"
echo ""
echo "Verify: kubectl get all -n colabbridge"
