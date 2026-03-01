# React 後端專案

## 概覽

這是一個基於 Spring Boot 和 Maven 的多模組後端專案，旨在為 React 前端應用程式提供支援。專案採用了分層的六邊形架構，將核心業務邏輯與外部基礎設施分離。

## 專案結構

專案由以下幾個模組組成：

- `guard-domain`: 包含核心領域模型和業務規則。
- `guard-application`: 包含應用程式服務和使用案例，協調領域模型以執行業務邏輯。
- `guard-infrastructure`: 負責基礎設施相關的實現，例如資料庫存取 (JPA/Hibernate)。
- `guard-auth`: 處理安全相關的設定，例如 Spring Security 和 JWT 認證。
- `guard-api`: 提供 API 端點，處理 HTTP 請求和回應。
- `guard-bootstrap`: Spring Boot 應用程式的啟動模組，整合所有其他模組。

## 關鍵技術

- **Java 21**
- **Spring Boot 4.0.0**
- **Maven**: 專案管理
- **Spring Web**: 用於建立 RESTful API
- **Spring Data JPA & Hibernate 7.1.11**: 資料庫存取
- **Spring Security**: 處理認證和授權
- **PostgreSQL 16+**: 資料庫
- **JWT (JJWT 0.12.6)**: Token 認證
- **Lombok 1.18.42**: 減少樣板程式碼

## 設定

1.  **資料庫**:
    -   確保您有一個正在執行的 PostgreSQL 16+ 執行個體。
    -   推薦使用 Docker Compose 啟動 PostgreSQL (參考 `/home/sixson/podmans/PostgreSQLForMe/docker-compose.yml`)。
    -   預設連線資訊：
        - **主機**: localhost
        - **埠**: 5432
        - **使用者**: sa
        - **密碼**: A@t123456
        - **資料庫**: mydatabase
    -   設定位置: `guard-bootstrap/src/main/resources/application-db.yml`

2.  **環境**:
    -   安裝 Java 21 或更高版本。
    -   安裝 Maven 3.8 或更高版本。

## 如何建置和執行

1.  **建置專案**:
    在專案根目錄執行以下 Maven 命令：
    ```bash
    mvn clean install
    ```

2.  **執行應用程式**:
    您可以透過 IDE 直接執行 `guard-bootstrap` 模組中的 `DemoApplication` 類別，或者使用以下命令：
    ```bash
    mvn spring-boot:run -pl guard-bootstrap
    ```

    應用程式將在 `http://localhost:8080/api` 上啟動。

## API 端點

API 的基礎 URL 是 `/api`。具體的端點在 `guard-api` 模組中定義。
