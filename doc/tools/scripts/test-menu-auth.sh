#!/bin/bash

# Risk-Guard API 整合測試腳本 (生產級優化版本)
# 用法: bash test-menu-auth.sh

API_BASE="http://localhost:8080/api"
TEST_EMAIL="test@example.com"
TEST_PASSWORD="Test123!"

# 顏色
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}========================================${NC}"
echo -e "${YELLOW}Risk-Guard API 整合測試 (Refactored)${NC}"
echo -e "${YELLOW}========================================${NC}\n"

# 檢查 jq 是否安裝
if ! command -v jq &> /dev/null; then
    echo -e "${RED}✗ 錯誤: 必須安裝 'jq' 才能執行此測試腳本${NC}"
    exit 1
fi

# 測試 1: 檢查服務器連接
echo -e "${YELLOW}[測試 1] 檢查服務器連接...${NC}"
if ! curl -s -m 2 "$API_BASE/auth/login" > /dev/null 2>&1; then
    echo -e "${RED}✗ 服務器未運行或無法連接${NC}"
    echo "  請確保應用在 http://localhost:8080 運行"
    exit 1
fi
echo -e "${GREEN}✓ 服務器連接成功${NC}\n"

# 測試 2: 測試登入 (POST /api/auth/login)
echo -e "${YELLOW}[測試 2] 測試登入 (POST /api/auth/login)...${NC}"
LOGIN_RESPONSE=$(curl -s -X POST "$API_BASE/auth/login" \
  -H "Content-Type: application/json" \
  -d "{\"email\": \"$TEST_EMAIL\", \"password\": \"$TEST_PASSWORD\"}")

# 驗證回傳 Code 是否為 200 (SUCCESS)
CODE=$(echo "$LOGIN_RESPONSE" | jq -r '.code')
if [ "$CODE" = "200" ]; then
    echo -e "${GREEN}✓ 登入成功 (Code 200)${NC}"

    # 提取 accessToken (從 .data.accessToken)
    TOKEN=$(echo "$LOGIN_RESPONSE" | jq -r '.data.accessToken')
    
    if [ "$TOKEN" = "null" ] || [ -z "$TOKEN" ]; then
        echo -e "${RED}✗ 無法從回應中提取 accessToken${NC}"
        echo "  響應內容: $LOGIN_RESPONSE"
        exit 1
    fi
    echo "  Captured Token: ${TOKEN:0:30}..."
else
    echo -e "${RED}✗ 登入失敗 (Code: $CODE)${NC}"
    echo "  響應訊息: $(echo "$LOGIN_RESPONSE" | jq -r '.message')"
    exit 1
fi
echo ""

# 測試 3: 無 Token 訪問受保護端點 (應該返回 401)
echo -e "${YELLOW}[測試 3] 無 Token 訪問受保護端點 /menu (預期 401)...${NC}"
MENU_NO_TOKEN=$(curl -s -i -X GET "$API_BASE/menu")
HTTP_STATUS=$(echo "$MENU_NO_TOKEN" | grep "HTTP/" | awk '{print $2}')

if [ "$HTTP_STATUS" = "401" ]; then
    echo -e "${GREEN}✓ 正確返回 HTTP 401 Unauthorized${NC}"
else
    echo -e "${RED}✗ 預期 HTTP 401，但收到 $HTTP_STATUS${NC}"
fi
echo ""

# 測試 4: 帶 Bearer Token 訪問 /menu (應該返回 200)
echo -e "${YELLOW}[測試 4] 帶 Bearer Token 訪問 /menu (預期 200)...${NC}"
MENU_WITH_TOKEN=$(curl -s -X GET "$API_BASE/menu" \
  -H "Authorization: Bearer $TOKEN")

CODE=$(echo "$MENU_WITH_TOKEN" | jq -r '.code')
if [ "$CODE" = "200" ]; then
    echo -e "${GREEN}✓ 正確返回成功代碼 200${NC}"
    # 檢查是否含有選單資料
    DATA_COUNT=$(echo "$MENU_WITH_TOKEN" | jq '.data | length')
    echo -e "${GREEN}✓ 成功獲取 $DATA_COUNT 筆選單數據${NC}"
else
    echo -e "${RED}✗ 請求失敗 (Code: $CODE)${NC}"
    echo "  響應: $MENU_WITH_TOKEN"
fi
echo ""

# 測試 5: 獲取使用者信息 (GET /api/auth/me)
echo -e "${YELLOW}[測試 5] 獲取當前使用者資訊 /auth/me (預期 200)...${NC}"
USER_INFO=$(curl -s -X GET "$API_BASE/auth/me" \
  -H "Authorization: Bearer $TOKEN")

CODE=$(echo "$USER_INFO" | jq -r '.code')
if [ "$CODE" = "200" ]; then
    NAME=$(echo "$USER_INFO" | jq -r '.data.name')
    echo -e "${GREEN}✓ 正確獲取使用者: $NAME${NC}"
else
    echo -e "${RED}✗ 獲取使用者失敗 (Code: $CODE)${NC}"
    echo "  響應: $USER_INFO"
fi
echo ""

# 測試 6: 參數校驗測試 (登入密碼為空)
echo -e "${YELLOW}[測試 6] 參數校驗測試: 密碼為空 (預期 400)...${NC}"
VALIDATION_RESPONSE=$(curl -s -i -X POST "$API_BASE/auth/login" \
  -H "Content-Type: application/json" \
  -d "{\"email\": \"$TEST_EMAIL\", \"password\": \"\"}")

HTTP_STATUS=$(echo "$VALIDATION_RESPONSE" | grep "HTTP/" | awk '{print $2}')
BODY=$(echo "$VALIDATION_RESPONSE" | sed -n '/{/,/}/p')

if [ "$HTTP_STATUS" = "400" ]; then
    MSG=$(echo "$BODY" | jq -r '.message')
    echo -e "${GREEN}✓ 正確攔截無效參數 (HTTP 400)${NC}"
    echo "  錯誤訊息: $MSG"
else
    echo -e "${RED}✗ 預期 HTTP 400，但收到 $HTTP_STATUS${NC}"
fi
echo ""

# 測試 7: 測試登出
echo -e "${YELLOW}[測試 7] 測試登出 (POST /api/auth/logout)...${NC}"
LOGOUT_RESPONSE=$(curl -s -i -X POST "$API_BASE/auth/logout" \
  -H "Authorization: Bearer $TOKEN")

HTTP_STATUS=$(echo "$LOGOUT_RESPONSE" | grep "HTTP/" | awk '{print $2}')
if [ "$HTTP_STATUS" = "200" ]; then
    echo -e "${GREEN}✓ 登出成功 (HTTP 200)${NC}"
    # 檢查是否有 Set-Cookie 清除指令
    if echo "$LOGOUT_RESPONSE" | grep -q "access_token=;"; then
        echo -e "${GREEN}✓ 已接收到清除 Cookie 指令${NC}"
    fi
else
    echo -e "${RED}✗ 登出失敗 (HTTP: $HTTP_STATUS)${NC}"
fi
echo ""

# 測試摘要
echo -e "${YELLOW}========================================${NC}"
echo -e "${GREEN}✓ 所有整合測試完成！${NC}"
echo -e "${YELLOW}========================================${NC}"
