# 認證功能模組 (Auth Module)

認證模組負責系統的使用者身份驗證與授權，支援 JWT 與無狀態會話管理。

---

## 功能特性

- **多模式登入**: 支援 Email 或 Username 憑證。
- **無狀態認證**: 使用 JWT (JSON Web Token) 進行身份識別。
- **雙重存取機制**:
  - `access_token`: 短效期 (預設 15 分鐘)，用於資源訪問。
  - `refresh_token`: 長效期 (預設 7 天)，用於刷新 access token。
- **安全存儲**: 透過 `HttpOnly` 與 `Secure` Cookies 存儲 Token，防止 XSS 攻擊。

---

##  技術實作

### 1. 關鍵組件
- **`AuthController`**: 定義外部 API 端點。
- **`AuthApplicationService`**: 業務邏輯編排與 DTO 轉換。
- **`SecurityService`**: 整合 Spring Security 的具體安全邏輯實作。
- **`JwtService`**: 負責 JWT 的簽發、解析與驗證。
- **`JwtAuthenticationFilter`**: 核心過濾器，攔截請求並建立 Security Context。

### 2. 資料模型
- **`User` (Domain)**: 使用者領域模型。
- **`CommonUser` (Entity)**: 資料庫存儲實體。
- **`LoginRequest` / `LoginResponse`**: 用於資料傳遞的 DTOs。

---

## API 端點概要

| 方法 | 路徑 | 說明 | 認證要求 |
| :--- | :--- | :--- | :--- |
| **POST** | `/api/auth/login` | 使用者登入並核發 Tokens | 公開 |
| **POST** | `/api/auth/register` | 新使用者註冊 | 公開 |
| **POST** | `/api/auth/refresh` | 使用 Refresh Token 獲取新 Access Token | Cookie (refresh_token) |
| **GET** | `/api/auth/me` | 獲取當前登入者詳細資訊 | 需要認證 |
| **POST** | `/api/auth/logout` | 清除客戶端認證 Cookies | 需要認證 |

---

## 認證流程詳解

請參閱 [SYSTEM_OVERVIEW.md](../../architecture/SYSTEM_OVERVIEW.md) 了解完整的 JWT 簽發與攔截流程。

---
**版本**: 1.1  
**最後更新**: 2026-03-29
