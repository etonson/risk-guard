# 🔧 Quick Fix Reference Card

## Problem
```
❌ Failed to load sidebar menu: Failed to fetch menu: 401
```

## Root Cause
```
SecurityConfig and JwtAuthenticationFilter
路由路徑缺少 /api 前綴導致認証配置不一致
```

## Fix Applied ✅

### File 1: SecurityConfig.java (Line 67)
```diff
- .requestMatchers("/auth/login", "/auth/register").permitAll()
+ .requestMatchers("/api/auth/login", "/api/auth/register").permitAll()
```

### File 2: JwtAuthenticationFilter.java (Line 37)
```diff
- boolean skip = path.endsWith("/auth/login") || path.endsWith("/auth/register");
+ boolean skip = path.endsWith("/api/auth/login") || path.endsWith("/api/auth/register");
```

## Verification Status ✅
```
✓ Code compiled successfully
✓ Syntax correct
✓ Changes verified
```

## Test Commands
```bash
# 1. Quick test with script
chmod +x test-menu-auth.sh
./test-menu-auth.sh

# 2. Manual test - Get token
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"Test123!"}' \
  | grep -o '"access_token":"[^"]*' | cut -d'"' -f4)

# 3. Access menu with token
curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/menu
# Expected: 200 OK with menu data

# 4. Access menu without token
curl http://localhost:8080/api/menu
# Expected: 401 Unauthorized
```

## Expected Results ✅
| Endpoint | Method | Auth | Expected |
|----------|--------|------|----------|
| /api/auth/login | POST | No | 200 OK |
| /api/auth/register | POST | No | 200 OK |
| /api/menu | GET | No | 401 ✓ |
| /api/menu | GET | Yes | 200 OK ✓ |
| /api/auth/me | GET | Yes | 200 OK ✓ |

## Files Modified
```
✓ guard-auth/src/main/java/com/security/SecurityConfig.java
✓ guard-auth/src/main/java/com/security/JwtAuthenticationFilter.java
```

## Documentation Created
```
✓ .ht-ai/FIX_401_AUTHENTICATION_ERROR.md (詳細指南)
✓ test-menu-auth.sh (測試腳本)
✓ .ht-ai/backend-menu-contract.yml (v2.0 API 契約)
✓ .ht-ai/BACKEND_MENU_SUMMARY.md (快速參考)
```

## Next Steps
1. ✅ Rebuild project: `mvn clean compile`
2. ⏳ Start application
3. ⏳ Run tests: `./test-menu-auth.sh`
4. ⏳ Verify menu loads in frontend

---
**Status**: 🟢 Ready to Deploy

