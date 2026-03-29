# Risk Guard 系統架構與認證流程手冊

本文件旨在提供開發者對於專案架構及 JWT 認證流程的深度理解。

---

## 🏗️ 1. 系統架構 (六邊形架構)

本專案採用 **六邊形架構 (Hexagonal Architecture)**，將業務核心與技術細節分離，確保系統的可測試性與可維護性。

### 模組職責說明

| 模組名稱 | 層級 | 職責描述 | 關鍵組件範例 |
| :--- | :--- | :--- | :--- |
| **`guard-domain`** | 核心層 | **業務核心**。包含 Entity、Value Objects 與 Repository 介面 (Ports)。 | `User`, `MenuItem`, `UserRepository` |
| **`guard-application`** | 應用層 | **業務編排**。實現 Use Cases，協調 Domain 物件執行特定業務邏輯。 | `AuthApplicationService`, `MenuApplicationService` |
| **`guard-api`** | 介面層 | **外部入口**。REST Controllers 處理 HTTP 請求、參數驗證與 DTO 轉換。 | `AuthController`, `MenuController` |
| **`guard-infrastructure`** | 基礎設施層 | **技術實現**。JPA Repository 實作、資料庫映射、外部服務集成。 | `JpaUserRepositoryAdapter` |
| **`guard-auth`** | 安全層 | **橫切關注點**。Spring Security 配置、JWT 核心邏輯、Token 生成。 | `JwtService`, `SecurityConfig` |
| **`guard-bootstrap`** | 啟動層 | **整合與啟動**。Spring Boot 進入點、全局設定檔。 | `GuardApplication`, `application.yml` |

---

## 🔐 2. JWT 認證流程 (無狀態)

系統採用雙重認證模式，同時支援 **Authorization Header (Bearer)** 與 **HttpOnly Cookie**，以兼顧移動端與瀏覽器的安全性。

### 🔄 A. 認證流程 (Authentication)
1. **登入請求**: 客戶端發送憑證至 `/api/auth/login`。
2. **驗證與核發**:
   - `AuthApplicationService` 調用 `SecurityService` 驗證使用者。
   - 驗證成功後，產生 **Access Token** (短期) 與 **Refresh Token** (長期)。
3. **響應處理**:
   - Token 資訊封裝在 JSON Body 中回傳。
   - 同時將 Tokens 寫入 `access_token` 與 `refresh_token` 兩個 **HttpOnly Cookies**。

### 🛡️ B. 授權攔截 (Authorization)
1. **攔截**: `JwtAuthenticationFilter` 攔截所有非公開端點的請求。
2. **提取**: 依序嘗試從 `Authorization` Header 或 Cookie 中提取 JWT。
3. **驗證**: 檢查 Token 簽章、過期時間與撤銷狀態。
4. **上下文注入**: 成功後將 `UserDetails` 注入 `SecurityContextHolder`。

### ♻️ C. 權杖刷新 (Token Refresh)
- 當 Access Token 過期時，客戶端調用 `/api/auth/refresh`。
- 後端驗證 Cookie 中的 `refresh_token`，合法則核發新的 Access Token。

---

## 📂 3. 關鍵目錄索引

- **核心業務**: `guard-domain/src/main/java/com/domain/`
- **安全配置**: `guard-auth/src/main/java/com/security/`
- **資料持久化**: `guard-infrastructure/src/main/java/com/infrastructure/`
- **API 定義**: `guard-api/src/main/java/com/controllers/`

---
**版本**: 1.1  
**最後更新**: 2026-03-29
