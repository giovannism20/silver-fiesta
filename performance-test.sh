#!/bin/bash

echo "=========================================="
echo "  Performance Tests - Products API"
echo "=========================================="
echo ""

GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

API_URL="http://localhost:8080/api/v1/products"

CPU_CORES=$(nproc 2>/dev/null || sysctl -n hw.ncpu 2>/dev/null || echo 4)
THREADS=$CPU_CORES
CONNECTIONS=$((CPU_CORES * 10))
STRESS_CONNECTIONS=$((CPU_CORES * 20))

echo -e "${BLUE}💻 CPU detected: $CPU_CORES cores${NC}"
echo -e "${BLUE}⚙️  Configuration: $THREADS threads, $CONNECTIONS base connections${NC}"
echo ""

if ! command -v wrk &> /dev/null; then
    echo -e "${YELLOW}⚠️  wrk not found!${NC}"
    echo ""
    echo "To install:"
    echo "  Ubuntu/Debian: sudo apt-get install wrk"
    echo "  macOS: brew install wrk"
    echo "  Arch Linux: sudo pacman -S wrk"
    echo ""
    exit 1
fi

echo -e "${BLUE}🔍 Checking if API is online...${NC}"
if ! curl -s -f "$API_URL" > /dev/null 2>&1; then
    echo -e "${YELLOW}⚠️  API is not responding at $API_URL${NC}"
    echo ""
    echo "Make sure the application is running:"
    echo "  docker compose up"
    echo ""
    exit 1
fi

echo -e "${GREEN}✓ API is online!${NC}"
echo ""

run_test() {
    local name=$1
    local url=$2
    local threads=$3
    local connections=$4
    local duration=$5
    local description=$6

    echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
    echo -e "${YELLOW}📊 $name${NC}"
    echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
    echo "Description: $description"
    echo "Threads: $threads | Connections: $connections | Duration: ${duration}s"
    echo "Endpoint: $url"
    echo ""

    wrk -t$threads -c$connections -d${duration}s "$url"
    echo ""
}

run_test \
    "Test 1: Product Listing" \
    "$API_URL" \
    $THREADS \
    $CONNECTIONS \
    30 \
    "Listing endpoint with default pagination"

run_test \
    "Test 2: Get by ID (Cache Active)" \
    "$API_URL/1" \
    $THREADS \
    $CONNECTIONS \
    30 \
    "Endpoint that benefits from Caffeine cache (optimized)"

run_test \
    "Test 3: Custom Pagination" \
    "$API_URL?page=0&size=5&sortBy=name&direction=ASC" \
    $((THREADS / 2 > 0 ? THREADS / 2 : 1)) \
    $((CONNECTIONS / 2 > 0 ? CONNECTIONS / 2 : 10)) \
    20 \
    "Test with pagination and sorting parameters"

run_test \
    "Test 4: Stress Test" \
    "$API_URL/1" \
    $THREADS \
    $STRESS_CONNECTIONS \
    30 \
    "Stress test with $STRESS_CONNECTIONS simultaneous connections"

echo -e "${GREEN}==========================================${NC}"
echo -e "${GREEN}Tests completed successfully!${NC}"
echo -e "${GREEN}==========================================${NC}"
echo ""
echo "System configuration:"
echo "  - CPU: $CPU_CORES cores"
echo "  - Threads: $THREADS"
echo "  - Base connections: $CONNECTIONS"
echo "  - Stress connections: $STRESS_CONNECTIONS"
echo ""
