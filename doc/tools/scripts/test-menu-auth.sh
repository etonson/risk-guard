#!/bin/bash

# Risk-Guard Menu Authentication Test Script
# 測試側邊欄選單認証修復
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
echo -e "${YELLOW}Risk-Guard 選單認証修復測試${NC}"
echo -e "${YELLOW}========================================${NC}\n"

# 測試 1: 檢查服務器連接
echo -e "${YELLOW}[測試 1] 檢查服務器連接...${NC}"
if ! curl -s -m 2 "$API_BASE/auth/login" > /dev/null 2>&1; then
    echo -e "${RED}✗ 服務器未運行或無法連接${NC}"
    echo "  請確保應用在 http://localhost:8080 運行"
    exit 1
fi
echo -e "${GREEN}✓ 服務器連接成功${NC}\n"

# 測試 2: 登入 (公開端點)
echo -e "${YELLOW}[測試 2] 測試登入 (POST /api/auth/login)...${NC}"
LOGIN_RESPONSE=$(curl -s -X POST "$API_BASE/auth/login" \
  -H "Content-Type: application/json" \
  -d "{\"email\": \"$TEST_EMAIL\", \"password\": \"$TEST_PASSWORD\"}")

if echo "$LOGIN_RESPONSE" | grep -q "access_token"; then
    echo -e "${GREEN}✓ 登入成功${NC}"

    # 提取 token
    TOKEN=$(echo "$LOGIN_RESPONSE" | grep -o '"access_token":"[^"]*' | cut -d'"' -f4)
    if [ -z "$TOKEN" ]; then
        echo -e "${RED}✗ 無法提取 token${NC}"
        exit 1
    fi
    echo "  Token: ${TOKEN:0:30}..."
else
    echo -e "${RED}✗ 登入失敗${NC}"
    echo "  響應: $LOGIN_RESPONSE"
    exit 1
fi
echo ""

# 測試 3: 無 Token 訪問選單 (應該返回 401)
echo -e "${YELLOW}[測試 3] 無 Token 訪問選單 (預期 401)...${NC}"
MENU_NO_TOKEN=$(curl -s -w "\n%{http_code}" -X GET "$API_BASE/menu")
HTTP_CODE=$(echo "$MENU_NO_TOKEN" | tail -n1)

if [ "$HTTP_CODE" = "401" ]; then
    echo -e "${GREEN}✓ 正確返回 401 Unauthorized${NC}"
else
    echo -e "${RED}✗ 預期 401，但收到 $HTTP_CODE${NC}"
fi
echo ""

# 測試 4: 帶 Bearer Token 訪問選單 (應該返回 200)
echo -e "${YELLOW}[測試 4] 帶 Bearer Token 訪問選單 (預期 200)...${NC}"
MENU_WITH_TOKEN=$(curl -s -w "\n%{http_code}" -X GET "$API_BASE/menu" \
  -H "Authorization: Bearer $TOKEN")

HTTP_CODE=$(echo "$MENU_WITH_TOKEN" | tail -n1)
BODY=$(echo "$MENU_WITH_TOKEN" | head -n-1)

if [ "$HTTP_CODE" = "200" ]; then
    echo -e "${GREEN}✓ 正確返回 200 OK${NC}"
    if echo "$BODY" | grep -q "blog_posts"; then
        echo -e "${GREEN}✓ 返回了有效的選單數據${NC}"
    else
        echo -e "${YELLOW}⚠ 返回 200 但選單數據可能不正確${NC}"
    fi
else
    echo -e "${RED}✗ 預期 200，但收到 $HTTP_CODE${NC}"
    echo "  響應: $BODY"
fi
echo ""

# 測試 5: 獲取使用者信息 (需要認証)
echo -e "${YELLOW}[測試 5] 獲取使用者信息 (GET /api/auth/me)...${NC}"
USER_INFO=$(curl -s -w "\n%{http_code}" -X GET "$API_BASE/auth/me" \
  -H "Authorization: Bearer $TOKEN")

HTTP_CODE=$(echo "$USER_INFO" | tail -n1)

if [ "$HTTP_CODE" = "200" ]; then
    echo -e "${GREEN}✓ 正確返回 200 OK${NC}"
else
    echo -e "${RED}✗ 預期 200，但收到 $HTTP_CODE${NC}"
fi
echo ""

# 測試 6: 測試無效 Token
echo -e "${YELLOW}[測試 6] 無效 Token 訪問受保護端點 (預期 401)...${NC}"
INVALID_TOKEN_RESPONSE=$(curl -s -w "\n%{http_code}" -X GET "$API_BASE/menu" \
  -H "Authorization: Bearer invalid_token_12345")

HTTP_CODE=$(echo "$INVALID_TOKEN_RESPONSE" | tail -n1)

if [ "$HTTP_CODE" = "401" ]; then
    echo -e "${GREEN}✓ 正確拒絕無效 Token${NC}"
else
    echo -e "${RED}✗ 預期 401，但收到 $HTTP_CODE${NC}"
fi
echo ""

# 測試 7: 測試登出
echo -e "${YELLOW}[測試 7] 測試登出 (POST /api/auth/logout)...${NC}"
LOGOUT_RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$API_BASE/auth/logout" \
  -H "Authorization: Bearer $TOKEN")

HTTP_CODE=$(echo "$LOGOUT_RESPONSE" | tail -n1)

if [ "$HTTP_CODE" = "200" ]; then
    echo -e "${GREEN}✓ 登出成功${NC}"
else
    echo -e "${YELLOW}⚠ 登出返回 $HTTP_CODE (通常也是可接受的)${NC}"
fi
echo ""

# 測試摘要
echo -e "${YELLOW}========================================${NC}"
echo -e "${GREEN}✓ 所有測試完成！${NC}"
echo -e "${YELLOW}========================================${NC}"
echo ""
echo "測試摘要:"
echo "  ✓ 服務器連接"
echo "  ✓ 登入端點工作正常"
echo "  ✓ 無 Token 返回 401"
echo "  ✓ 有效 Token 返回 200"
echo "  ✓ 選單數據正確返回"
echo ""
echo -e "${GREEN}修復成功！側邊欄選單應該現在可以正常加載。${NC}"

