# Risk Guard 系統架構與認證流程手冊

本文件旨在提供開發者對於專案架構及 JWT 認證流程的快速理解。

---

## 🏗️ 1. 系統架構 (六邊形架構)

本專案採用 **六邊形架構 (Hexagonal Architecture)**，將業務核心與技術細節分離。

| 模組名稱 | 層級 | 職責描述 |
| :--- | :--- | :--- |
| **`guard-domain`** | 核心層 | **業務核心**。包含 Entity (`User`, `MenuItem`) 與 Repository 介面 (Ports)。 |
| **`guard-application`** | 應用層 | **業務編排**。實現 Use Cases (如 `AuthApplicationService`)，協調 Domain 物件。 |
| **`guard-api`** | 介面層 | **外部入口**。REST Controllers 處理 HTTP 請求與 DTO 轉換。 |
| **`guard-infrastructure`** | 基礎設施層 | **技術實現**。JPA Repository 實作、資料庫映射。 |
| **`guard-auth`** | 安全層 | **橫切關注點**。Spring Security 配置、JWT 核心邏輯。 |
| **`guard-bootstrap`** | 啟動層 | **整合與啟動**。Spring Boot 進入點、設定檔 (`application.yml`)。 |

---

## 🔐 2. JWT 認證流程

系統採用無狀態的 JWT (JSON Web Token) 認證機制，支援 **Authorization Header** 與 **HttpOnly Cookie** 雙模式。

### 🔄 A. 登入與註冊 (Authentication)
1. **Request**: 使用者發送憑證 (Email/Username + Password) 至 `/api/auth/login`。
2. **Process**: 
   - `AuthController` 呼叫 `AuthApplicationService`。
   - 驗證成功後，`JwtService` 產生 **Access Token** (15 min) 與 **Refresh Token** (7 days)。
3. **Response**: 
   - 回傳 Token 資訊於 JSON Body。
   - 同時寫入 `access_token` 與 `refresh_token` 兩個 **HttpOnly Cookies**。

### 🛡️ B. 請求攔截與驗證 (Authorization)
1. **攔截**: `JwtAuthenticationFilter` 攔截所有非公開請求。
2. **提取**: 依序從 `Authorization: Bearer <token>` 或 Cookie 中提取 JWT。
3. **驗證**: `JwtService` 檢查 Token 簽章、有效期及使用者身分。
4. **注入**: 驗證成功後，將使用者資訊注入 `SecurityContextHolder`，供後續權限檢查使用。

### ♻️ C. 權杖刷新 (Token Refresh)
- 當 Access Token 過期時，前端調用 `/api/auth/refresh`。
- 後端驗證 `refresh_token` Cookie，若合法則核發新的 Access Token。

---

## 🛠️ 3. 關鍵組件速查

- **安全配置**: `guard-auth/.../security/SecurityConfig.java`
- **JWT 過濾器**: `guard-auth/.../security/JwtAuthenticationFilter.java`
- **權限服務**: `guard-application/.../menu/PermissionService.java`
- **選單過濾**: `guard-application/.../menu/MenuPermissionFilter.java`

---
**版本**: 1.0  
**更新日期**: 2026-03-18
