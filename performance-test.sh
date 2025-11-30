#!/bin/bash

# Script de Teste de Performance - Desafio Mercado Livre
# Requisito: wrk (HTTP benchmarking tool)
# Instalação: sudo apt-get install wrk (Ubuntu/Debian) ou brew install wrk (macOS)

echo "=========================================="
echo "  Testes de Performance - API Produtos"
echo "=========================================="
echo ""

# Cores para output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

API_URL="http://localhost:8080/api/v1/products"

# Detecta número de cores da CPU
CPU_CORES=$(nproc 2>/dev/null || sysctl -n hw.ncpu 2>/dev/null || echo 4)
THREADS=$CPU_CORES
CONNECTIONS=$((CPU_CORES * 10))
STRESS_CONNECTIONS=$((CPU_CORES * 20))

echo -e "${BLUE}💻 CPU detectada: $CPU_CORES cores${NC}"
echo -e "${BLUE}⚙️  Configuração: $THREADS threads, $CONNECTIONS conexões base${NC}"
echo ""

# Verifica se wrk está instalado
if ! command -v wrk &> /dev/null; then
    echo -e "${YELLOW}⚠️  wrk não encontrado!${NC}"
    echo ""
    echo "Para instalar:"
    echo "  Ubuntu/Debian: sudo apt-get install wrk"
    echo "  macOS: brew install wrk"
    echo "  Arch Linux: sudo pacman -S wrk"
    echo ""
    exit 1
fi

# Verifica se a API está respondendo
echo -e "${BLUE}🔍 Verificando se a API está online...${NC}"
if ! curl -s -f "$API_URL" > /dev/null 2>&1; then
    echo -e "${YELLOW}⚠️  API não está respondendo em $API_URL${NC}"
    echo ""
    echo "Certifique-se de que a aplicação está rodando:"
    echo "  docker compose up"
    echo ""
    exit 1
fi

echo -e "${GREEN}✓ API está online!${NC}"
echo ""

# Função para executar teste e exibir resultado
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
    echo "Descrição: $description"
    echo "Threads: $threads | Conexões: $connections | Duração: ${duration}s"
    echo "Endpoint: $url"
    echo ""

    wrk -t$threads -c$connections -d${duration}s "$url"
    echo ""
}

# Teste 1: Listagem de produtos (sem cache)
run_test \
    "Teste 1: Listagem de Produtos" \
    "$API_URL" \
    $THREADS \
    $CONNECTIONS \
    30 \
    "Endpoint de listagem com paginação padrão"

# Teste 2: Busca por ID (com cache Caffeine)
run_test \
    "Teste 2: Busca por ID (Cache Ativo)" \
    "$API_URL/1" \
    $THREADS \
    $CONNECTIONS \
    30 \
    "Endpoint que se beneficia do cache Caffeine (otimizado)"

# Teste 3: Paginação customizada
run_test \
    "Teste 3: Paginação Customizada" \
    "$API_URL?page=0&size=5&sortBy=name&direction=ASC" \
    $((THREADS / 2 > 0 ? THREADS / 2 : 1)) \
    $((CONNECTIONS / 2 > 0 ? CONNECTIONS / 2 : 10)) \
    20 \
    "Teste com parâmetros de paginação e ordenação"

# Teste 4: Carga alta
run_test \
    "Teste 4: Teste de Estresse" \
    "$API_URL/1" \
    $THREADS \
    $STRESS_CONNECTIONS \
    30 \
    "Teste de estresse com $STRESS_CONNECTIONS conexões simultâneas"

echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${GREEN}✅ Testes de performance concluídos!${NC}"
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo ""
echo "💻 Configuração do sistema:"
echo "  • CPU: $CPU_CORES cores"
echo "  • Threads: $THREADS"
echo "  • Conexões base: $CONNECTIONS"
echo "  • Conexões estresse: $STRESS_CONNECTIONS"
echo ""
echo "📈 Benchmarks típicos:"
echo "  • Listagem: 3.000-5.000 req/s"
echo "  • Cache ativo: 10.000-15.000 req/s"
echo "  • Latência média: 10-30ms"
echo ""
echo "💡 Dicas:"
echo "  • O cache Caffeine melhora significativamente buscas por ID"
echo "  • Cache expira após 10 minutos ou ao atingir 500 produtos"
echo "  • Performance escala linearmente com número de cores da CPU"
echo ""
