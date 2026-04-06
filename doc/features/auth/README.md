# 認證與授權功能模組 (Authentication & Authorization Module)

`guard-auth` 模組是 Risk Guard 系統的安全核心，基於 **Spring Boot 4.0.0**、**Spring Security** 與 **JWT (JSON Web Token)** 構建，實現了無狀態 (Stateless) 的身份驗證與細粒度授權。

---

## 核心功能特性

### 1. 多維度認證模式
- **憑證識別**: 支援透過 `email` 或 `username` 進行登入。系統會自動偵測輸入內容並匹配對應的資料庫欄位。
- **密碼安全**: 使用 `BCryptPasswordEncoder` 進行單向雜湊存儲，確保資料庫洩漏時密碼不被還原。

### 2. 無狀態會話管理 (Stateless Session)
- **禁用 JSESSIONID**: 系統完全依賴 JWT，不維護伺服器端 Session，支援水平擴展。
- **雙 Token 刷新機制**:
  - **Access Token**: 短效期 (預設 15 分鐘)，用於 API 調用的身分證明。
  - **Refresh Token**: 長效期 (預設 7 天)，用於在 Access Token 過期後靜默獲取新權杖，平衡安全性與使用者體驗。

### 3. 多重 Token 傳遞與認證媒介
- **Header 傳遞**: 支援標準 `Authorization: Bearer <token>` 標頭。
- **Cookie 傳遞**: 系統會自動將 `access_token` 與 `refresh_token` 寫入 HttpOnly Cookies。`JwtAuthenticationFilter` 會優先檢查 Header，若無則從 Cookie 提取，實現對 Web 與 Mobile 端的全面支援。

---

## 技術架構與組件

### 1. 安全過濾鏈 (Security Filter Chain)
- **`SecurityConfig`**: 中心化配置類，定義了：
  - 公開 API 白名單 (`/api/auth/login`, `/api/auth/register`, `/api/auth/refresh`)。
  - CORS 跨域策略 (允許特定 Origin、憑證、與標頭)。
  - 整合自定義的 `JwtAuthenticationFilter`。
- **`JwtAuthenticationFilter`**: 在 `UsernamePasswordAuthenticationFilter` 之前攔截請求：
  1. 透過 `getJwtFromRequest` 優先從 `Authorization` Header 或 `access_token` Cookie 中提取 Token。
  2. 透過 `JwtService` 驗證簽章、解析使用者身分 (Email/Username)。
  3. 檢索使用者詳情並建立 `SecurityContext`。

### 2. 核心服務層
- **`SecurityServiceImpl`**: 封裝了 Spring Security 的核心邏輯：
  - 調用 `AuthenticationManager` 進行身分校驗。
  - 整合 `UserQueryService` 與 `UserRepository` 進行持久化操作 (如註冊)。
  - 協調 `RefreshTokenCookieFactory` 生成符合規範的 Cookies。
- **`UserDetailsServiceImpl`**: 橋接資料庫與安全框架，根據 Email 或 Username 載入使用者權限資訊。

### 3. Token 安全工廠 (`RefreshTokenCookieFactory`)
- **HttpOnly**: 防止 JavaScript 讀取 Token (防禦 XSS)。
- **SameSite=Strict**: 嚴格防止跨站請求偽造 (防禦 CSRF)。
- **Path=/api**: 限制 Cookie 僅在 API 路徑下發送，減少不必要的傳輸。
- **MaxAge=0**: 登出時透過設置過期 Cookie 清除客戶端狀態。

---

## 關鍵流程說明

### 登入與 Token 核發 (Login Flow)
1. 使用者提交憑證 (LoginRequest) 至 `/api/auth/login`。
2. `SecurityService` 驗證成功後，生成 Access & Refresh JWT。
3. 系統將 Tokens 封裝於 `LoginResponse` DTO 中，並同時設置 `access_token` 與 `refresh_token` 的 Set-Cookie 標頭。

### 權杖刷新 (Refresh Flow)
1. 當 Access Token 過期 (401)，客戶端發送請求至 `/api/auth/refresh`。
2. 系統可從 Request Body (`RefreshTokenRequest`) 或 Cookie 中自動獲取 `refresh_token`。
3. `SecurityService` 驗證 Refresh Token 有效性，核發新的 Access Token。
4. 更新 `access_token` Cookie 並回傳新的 DTO。

---

## API 端點與認證要求

| 方法 | 路徑 | 說明 | 認證要求 | 傳遞媒介 |
| :--- | :--- | :--- | :--- | :--- |
| **POST** | `/api/auth/login` | 身分驗證並初始化會話 | 公開 | Body (DTO) |
| **POST** | `/api/auth/register` | 新使用者註冊並自動登入 | 公開 | Body (DTO) |
| **POST** | `/api/auth/refresh` | 刷新 Access Token | 公開* | Body/Cookie |
| **GET**  | `/api/auth/me` | 查詢當前登入者身分 (UserInfo) | JWT | Header/Cookie |
| **POST** | `/api/auth/logout` | 廢棄會話並清除 Cookies | 公開* | Cookie |

*\*註：`refresh` 與 `logout` 端點在 Filter 層級被放行，但業務邏輯會檢查 Token 存在性。*

---

## 安全配置參考 (JwtService)

| 參數 | 預設值 | 說明 |
| :--- | :--- | :--- |
| **Secret Key** | HMAC-SHA256 | 用於簽署 JWT 的金鑰 (由配置檔載入) |
| **Access Expire** | 15 Minutes | 訪問權杖有效期 |
| **Refresh Expire** | 7 Days | 刷新權杖有效期 |
| **Algorithm** | HS256 | 標準 HMAC SHA256 簽名算法 |

---
**文件版本**: 1.3
**最後修訂**: 2026-04-06
**維護者**: Risk Guard Security Team
