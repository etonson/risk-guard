# 開發者指南 (Developer Guide)

本指南旨在協助開發者快速上手 Risk Guard 專案，理解其開發流程與標準規範。

---

## 🛠️ 1. 技術棧與環境要求

*   **JDK**: 21 (LTS)
*   **Framework**: Spring Boot 4.0.0
*   **Database**: PostgreSQL 16+
*   **Build Tool**: Maven 3.8+
*   **Security**: Spring Security + JWT (JJWT 0.12.6)

---

## 🏗️ 2. 專案模組架構

專案依據 **六邊形架構** 劃分模組，依賴關係遵循 **由外向內** 的原則。

1.  **`guard-domain`**: 核心層。不依賴框架。包含實體、業務規約與 Repository 介面。
2.  **`guard-application`**: 應用層。負責 Use Case 編排與 DTO 轉換。
3.  **`guard-infrastructure`**: 基礎設施。包含資料庫訪問與外部 API 整合。
4.  **`guard-api`**: 表現層。負責 RESTful 端點與 Request/Response 映射。
5.  **`guard-auth`**: 安全模組。封裝 Security 與 JWT 邏輯。
6.  **`guard-bootstrap`**: 啟動模組。聚合所有組件並啟動專案。

---

## 🚀 3. 快速啟動

### 1. 資料庫準備
確保 PostgreSQL 服務已啟動。預設配置於 `guard-bootstrap/src/main/resources/application-db.yml`。

### 2. 編譯與執行
在專案根目錄執行：

```bash
# 清理並安裝依賴
mvn clean install

# 啟動應用程式
mvn spring-boot:run -pl guard-bootstrap
```

API 基礎路徑: `http://localhost:8080/api`
Swagger UI: `http://localhost:8080/swagger-ui/index.html`

---

## 📏 4. 編碼規範

1.  **Lombok**: 強烈建議使用 `@Data`, `@Builder`, `@RequiredArgsConstructor`。
2.  **依賴注入**: 務必使用 **建構子注入 (Constructor Injection)**。
3.  **API 規範**:
    - 所有 API 路徑必須以 `/api` 開頭。
    - 使用 `ApiResponse<T>` 作為統一返回格式。
4.  **異常處理**: 業務異常應轉化為帶有適當 `ResultCode` 的響應。
5.  **日誌**: 使用 `Slf4j` (Lombok `@Slf4j`)。

---

## 🧪 5. 測試與驗證

*   **單元測試**: 置於各模組的 `src/test/java` 下。
*   **API 測試**: 使用 `doc/tools/scripts/test-menu-auth.sh` 進行端點驗證。
*   **手動驗證**: 建議使用 Postman 或 IntelliJ HTTP Client。

---
**版本**: 1.1  
**最後更新**: 2026-03-29
