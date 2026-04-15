#!/bin/bash

set -e

echo "Deploying Central Log System to AWS EKS..."
echo ""

GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

if ! command -v kubectl &> /dev/null; then
    echo -e "${RED}kubectl is not installed${NC}"
    exit 1
fi

if ! kubectl cluster-info &> /dev/null; then
    echo -e "${RED}Cannot connect to cluster. Run: aws eks update-kubeconfig --name <cluster> --region <region>${NC}"
    exit 1
fi

echo -e "${GREEN}Connected to Kubernetes cluster${NC}"
echo ""

echo -e "${YELLOW}[1/7] Creating namespace...${NC}"
kubectl apply -f 00-namespace.yaml
echo ""

echo -e "${YELLOW}[2/7] Creating secrets...${NC}"
echo -e "${RED}  IMPORTANT: Update 01-secrets.yaml with real base64 values first!${NC}"
kubectl apply -f 01-secrets.yaml
echo ""

echo -e "${YELLOW}[3/7] Deploying databases...${NC}"
kubectl apply -f 03-databases/
echo "Waiting for databases to be ready..."
kubectl wait --for=condition=ready pod -l app=auth-db -n central-log-system --timeout=120s
kubectl wait --for=condition=ready pod -l app=faculty-db -n central-log-system --timeout=120s
kubectl wait --for=condition=ready pod -l app=student-db -n central-log-system --timeout=120s
echo ""

echo -e "${YELLOW}[4/7] Deploying RabbitMQ...${NC}"
kubectl apply -f 04-rabbitmq.yaml
echo "Waiting for RabbitMQ to be ready..."
kubectl wait --for=condition=ready pod -l app=rabbitmq -n central-log-system --timeout=120s
echo ""

echo -e "${YELLOW}[5/7] Creating RBAC and logging resources...${NC}"
kubectl apply -f 05-rbac.yaml
[ -f "08-logging.yaml" ] && kubectl apply -f 08-logging.yaml
echo ""

echo -e "${YELLOW}[6/7] Deploying microservices...${NC}"
kubectl apply -f 06-services/
echo "Waiting for services to be ready..."
sleep 15
kubectl wait --for=condition=ready pod -l app=auth-service -n central-log-system --timeout=300s
kubectl wait --for=condition=ready pod -l app=faculty-service -n central-log-system --timeout=300s
kubectl wait --for=condition=ready pod -l app=student-service -n central-log-system --timeout=300s
kubectl wait --for=condition=ready pod -l app=api-gateway -n central-log-system --timeout=300s
echo ""

if [ -f "07-ingress.yaml" ]; then
    echo -e "${YELLOW}[7/7] Deploying ALB Ingress...${NC}"
    kubectl apply -f 07-ingress.yaml
    echo ""
fi

echo -e "${GREEN}Deployment Complete!${NC}"
echo ""
echo "Pods:"
kubectl get pods -n central-log-system
echo ""
echo "Services:"
kubectl get svc -n central-log-system
echo ""
echo "Ingress:"
kubectl get ingress -n central-log-system
echo ""
echo "ALB URL:"
echo "  kubectl get ingress central-log-system-ingress -n central-log-system -o jsonpath='{.status.loadBalancer.ingress[0].hostname}'"
echo ""
echo "Port forward (testing):"
echo "  kubectl port-forward svc/api-gateway 8080:8080 -n central-log-system"
echo ""
echo "View logs:"
echo "  kubectl logs -f -l app=api-gateway -n central-log-system"
