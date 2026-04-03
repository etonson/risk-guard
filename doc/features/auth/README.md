# 認證與授權功能模組 (Authentication & Authorization Module)

`guard-auth` 模組是 Risk Guard 系統的安全核心，基於 **Spring Security** 與 **JWT (JSON Web Token)** 構建，實現了無狀態 (Stateless) 的身份驗證與細粒度授權。

---

## 核心功能特性

### 1. 多維度認證模式
- **憑證識別**: 支援透過 `email` 或 `username` 進行登入。
- **密碼安全**: 使用 `BCryptPasswordEncoder` (強度 12) 進行單向雜湊存儲，確保資料庫洩漏時密碼不被還原。

### 2. 無狀態會話管理 (Stateless Session)
- **禁用 JSESSIONID**: 系統完全依賴 JWT，不維護伺服器端 Session，支援水平擴展。
- **雙 Token 刷新機制**:
  - **Access Token**: 短效期 (預設 15 分鐘)，用於 API 調用的身分證明。
  - **Refresh Token**: 長效期 (預設 7 天)，用於在 Access Token 過期後靜默獲取新權杖，平衡安全性與使用者體驗。

### 3. 多重 Token 傳遞方式
- **Header 傳遞**: 支援標準 `Authorization: Bearer <token>` 標頭。
- **Cookie 傳遞**: 支援從 HTTP-only Cookies 自動讀取 `access_token` 與 `refresh_token`，方便瀏覽器客戶端集成。

---

## 技術架構與組件

### 1. 安全過濾鏈 (Security Filter Chain)
- **`SecurityConfig`**: 中心化配置類，定義了：
  - 公開 API 白名單 (`/auth/login`, `/auth/register`, `/auth/refresh`)。
  - CORS 跨域策略 (允許特定 Origin、憑證、與標頭)。
  - 自定義異常處理 (`AuthenticationEntryPoint`, `AccessDeniedHandler`)。
- **`JwtAuthenticationFilter`**: 在 `UsernamePasswordAuthenticationFilter` 之前攔截請求：
  1. 優先從 Header 或 Cookie 中提取 Token。
  2. 透過 `JwtService` 驗證簽章與有效期。
  3. 檢索使用者詳情並建立 `SecurityContext`。

### 2. 核心服務層
- **`SecurityServiceImpl`**: 封裝了 Spring Security 的核心邏輯：
  - 調用 `AuthenticationManager` 進行身分校驗。
  - 整合 `UserQueryService` 讀取領域模型資料。
  - 協調 `RefreshTokenCookieFactory` 生成符合規範的 Cookies。
- **`UserDetailsServiceImpl`**: 橋接資料庫與安全框架：
  - **開發模式模式支援**: 若資料庫中找不到使用者，會回傳預設的 Mock User (密碼: `Test123!`)，加速開發測試。

### 3. Token 安全工廠 (`RefreshTokenCookieFactory`)
- **HttpOnly**: 防止 JavaScript 讀取 Token (防禦 XSS)。
- **SameSite=Lax**: 防止跨站請求偽造 (防禦 CSRF)。
- **Secure**: 生產環境下強制度過 HTTPS。
- **MaxAge=0**: 登出時透過設置過期 Cookie 清除客戶端狀態。

---

## 關鍵流程說明

### 登入與 Token 核發 (Login Flow)
1. 使用者提交憑證至 `/api/auth/login`。
2. `SecurityService` 驗證成功後，生成 Access & Refresh JWT。
3. 系統將 Tokens 同時放入 API 回傳內容 (DTO) 與 HTTP 響應的 Set-Cookie 標頭。

### 權杖刷新 (Refresh Flow)
1. 客戶端 Access Token 過期。
2. 客戶端發送請求至 `/api/auth/refresh` (自動攜帶 Refresh Token Cookie)。
3. `SecurityService` 驗證 Refresh Token 有效性。
4. 回傳新的 Access Token 並更新 `access_token` Cookie。

---

## API 端點與認證要求

| 方法 | 路徑 | 說明 | 認證要求 | 傳遞媒介 |
| :--- | :--- | :--- | :--- | :--- |
| **POST** | `/api/auth/login` | 身分驗證並初始化會話 | 公開 | Body |
| **POST** | `/api/auth/register` | 新使用者註冊並自動登入 | 公開 | Body |
| **POST** | `/api/auth/refresh` | 靜默刷新 Access Token | Cookie | `refresh_token` |
| **GET**  | `/api/auth/me` | 查詢當前登入者身分 (UserInfo) | JWT | Header/Cookie |
| **POST** | `/api/auth/logout` | 廢棄會話並清除 Cookies | JWT | Cookie |

---

## 安全配置參考 (JwtService)

| 參數 | 預設值 | 說明 |
| :--- | :--- | :--- |
| **Secret Key** | 256-bit Hex | 用於簽署 JWT 的金鑰 (生產環境應從配置獲取) |
| **Access Expire** | 15 Minutes | 訪問權杖有效期 |
| **Refresh Expire** | 7 Days | 刷新權杖有效期 |
| **Algorithm** | HS256 | HMAC SHA256 簽名算法 |

---
**文件版本**: 1.2
**最後修訂**: 2026-04-03
**維護者**: Risk Guard Security Team
