#!/bin/bash

# Risk-Guard API 整合測試腳本
# 用法: bash api-test.sh

# 配置項
API_BASE="http://localhost:8080/api"
TEST_EMAIL="test@example.com"
TEST_PASSWORD="Test123!"

# 顏色定義
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # 無顏色

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}    Risk-Guard API 測試工具箱           ${NC}"
echo -e "${BLUE}========================================${NC}\n"

# 函數：檢查響應代碼
check_status() {
    local expected=$1
    local actual=$2
    local msg=$3
    if [ "$actual" == "$expected" ]; then
        echo -e "${GREEN}  [✓] $msg (HTTP $actual)${NC}"
    else
        echo -e "${RED}  [✗] $msg (預期 $expected, 實際 $actual)${NC}"
    fi
}

# 1. 測試連通性
echo -e "${YELLOW}[1] 檢查伺服器連通性...${NC}"
if ! curl -s -m 2 "$API_BASE/auth/login" > /dev/null 2>&1; then
    echo -e "${RED}錯誤: 無法連接到 $API_BASE。請確保應用程式已啟動。${NC}"
    exit 1
fi
echo -e "${GREEN}連通性正常${NC}\n"

# 2. 測試登入
echo -e "${YELLOW}[2] 測試登入 (POST /auth/login)...${NC}"
# 使用 -c 保存 cookie (獲取 Set-Cookie)
LOGIN_FULL=$(curl -s -i -X POST "$API_BASE/auth/login" \
  -H "Content-Type: application/json" \
  -d "{\"email\": \"$TEST_EMAIL\", \"password\": \"$TEST_PASSWORD\"}")

LOGIN_CODE=$(echo "$LOGIN_FULL" | grep "HTTP/" | awk '{print $2}')
check_status "200" "$LOGIN_CODE" "登入端點"

# 提取 Token (從 JSON Body)
LOGIN_BODY=$(echo "$LOGIN_FULL" | sed -n '/{/,$p')
TOKEN=$(echo "$LOGIN_BODY" | grep -o '"access_token":"[^"]*' | cut -d'"' -f4)

if [ -z "$TOKEN" ]; then
    echo -e "${RED}失敗: 無法從登入響應中獲取 Access Token${NC}"
    echo "響應內容: $LOGIN_BODY"
    exit 1
fi
echo -e "${GREEN}獲取 Token 成功: ${TOKEN:0:20}...${NC}\n"

# 3. 測試選單 API (需要認証)
echo -e "${YELLOW}[3] 測試選單 API (GET /menu)...${NC}"
MENU_RES=$(curl -s -w "\n%{http_code}" -X GET "$API_BASE/menu" \
  -H "Authorization: Bearer $TOKEN")
MENU_CODE=$(echo "$MENU_RES" | tail -n1)
check_status "200" "$MENU_CODE" "選單訪問"

if [[ "$MENU_RES" == *"code\":200"* ]]; then
    echo -e "  ${GREEN}選單數據返回正常${NC}"
fi
echo ""

# 4. 測試個人資料 (GET /auth/me)
echo -e "${YELLOW}[4] 測試個人資料 (GET /auth/me)...${NC}"
ME_RES=$(curl -s -w "\n%{http_code}" -X GET "$API_BASE/auth/me" \
  -H "Authorization: Bearer $TOKEN")
ME_CODE=$(echo "$ME_RES" | tail -n1)
check_status "200" "$ME_CODE" "個人資料訪問"
echo ""

# 5. 測試登出
echo -e "${YELLOW}[5] 測試登出 (POST /auth/logout)...${NC}"
LOGOUT_RES=$(curl -s -w "\n%{http_code}" -X POST "$API_BASE/auth/logout" \
  -H "Authorization: Bearer $TOKEN")
LOGOUT_CODE=$(echo "$LOGOUT_RES" | tail -n1)
check_status "200" "$LOGOUT_CODE" "登出功能"

echo -e "\n${BLUE}========================================${NC}"
echo -e "${BLUE}    測試完成！${NC}"
echo -e "${BLUE}========================================${NC}"
