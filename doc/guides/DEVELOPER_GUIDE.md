# 開發者指南 (Developer Guide)

本指南旨在協助開發者快速上手並理解 `risk-guard` 專案的架構與開發流程。本專案採用 **六邊形架構 (Hexagonal Architecture)**，亦稱為 **埠與適配器架構 (Ports and Adapters Architecture)**，以確保業務邏輯的核心地位並與外部依賴解耦。

## 1. 技術堆疊 (Tech Stack)

*   **語言**: Java 21
*   **框架**: Spring Boot 4.0.0
*   **建置工具**: Maven 3.8+
*   **資料庫**: PostgreSQL 16+
*   **資料庫驅動**: PostgreSQL JDBC Driver 42.7.7
*   **ORM**: Hibernate 7.1.11 / Spring Data JPA
*   **安全性**: Spring Security, JWT (JJWT 0.12.6)
*   **HTTP 伺服器**: Embedded Tomcat (Spring Boot)
*   **工具**: Lombok 1.18.42

## 2. 專案模組架構 (Project Modules)

專案被拆分為多個 Maven 模組，每個模組都有明確的職責：

### 核心層 (Core)
*   **`guard-domain`**:
    *   **職責**: 包含企業核心業務邏輯、實體 (Entities) 和 領域服務 (Domain Services)。
    *   **依賴**: 不依賴任何其他模組 (除了 JDK 和極少數工具庫如 Lombok)。
    *   **內容**: 定義了 Repository 的介面 (Ports)，但不包含實作。目前包含 `auth` 和 `layout` 領域。

*   **`guard-application`**:
    *   **職責**: 應用程式層，負責協調領域物件來執行特定的應用程式邏輯 (Use Cases)。
    *   **依賴**: 依賴 `guard-domain`。
    *   **內容**: Service 類別，DTO 轉換邏輯。

### 適配器層 (Adapters)
*   **`guard-infrastructure`**:
    *   **職責**: 基礎設施層，實作 `guard-domain` 定義的介面 (Driven Adapters)。
    *   **依賴**: 依賴 `guard-domain`。
    *   **內容**: 資料庫存取實作 (Spring Data JPA Repositories)、外部 API 客戶端、檔案系統存取等。

*   **`guard-api`**:
    *   **職責**: 介面層，處理外部請求並呼叫應用程式邏輯 (Driving Adapters)。
    *   **依賴**: 依賴 `guard-application` (以及間接依賴 `guard-domain`)。
    *   **內容**: REST Controllers, Request/Response DTOs, Exception Handlers。

*   **`guard-auth`**:
    *   **職責**: 處理安全性相關邏輯。
    *   **內容**: Spring Security 設定, JWT Token 生成與驗證, UserDetailsService 實作。

### 啟動層 (Bootstrap)
*   **`guard-bootstrap`**:
    *   **職責**: 應用程式的進入點，負責組裝所有模組並啟動 Spring Boot 應用程式。
    *   **依賴**: 依賴所有上述模組。
    *   **內容**: `DemoApplication` (Main class), `application.yml` 設定檔。

## 3. 開發環境設定 (Setup)

### 前置需求
*   JDK 21+
*   Maven 3.8+
*   PostgreSQL 16+

### 資料庫設定
1.  確保 PostgreSQL 正在執行。推薦使用 Docker Compose：
    ```bash
    cd /home/sixson/podmans/PostgreSQLForMe
    docker-compose up -d
    ```
2.  驗證連線資訊 (預設值)：
    - 主機: localhost
    - 埠: 5432
    - 使用者: sa
    - 密碼: A@t123456
    - 資料庫: mydatabase
3.  確認 `guard-bootstrap/src/main/resources/application-db.yml` 中的連線設定與 PostgreSQL 一致。

### 建置與執行
在專案根目錄執行：

```bash
# 建置整個專案
mvn clean install

# 執行應用程式 (指向 guard-bootstrap 模組)
mvn spring-boot:run -pl guard-bootstrap
```

應用程式預設啟動於 `http://localhost:8080`，API Context Path 為 `/api`。

## 4. 開發流程範例 (Development Workflow)

當需要開發一個新功能 (例如：新增一個「產品」管理功能) 時，建議遵循以下順序：

1.  **Domain Layer (`guard-domain`)**:
    *   定義 `Product` 實體 (Entity)。
    *   定義 `ProductRepository` 介面 (Port)。

2.  **Infrastructure Layer (`guard-infrastructure`)**:
    *   實作 `ProductRepository` 介面 (通常繼承 `JpaRepository`)。
    *   設定 Hibernate 映射 (如果需要)。

3.  **Application Layer (`guard-application`)**:
    *   建立 `ProductService`。
    *   定義輸入/輸出的 DTOs。
    *   實作業務流程，呼叫 `ProductRepository`。

4.  **Interface Layer (`guard-api`)**:
    *   建立 `ProductController`。
    *   定義 REST API 端點 (GET, POST 等)。
    *   呼叫 `ProductService` 並回傳結果。

## 5. 設定檔說明 (Configuration)

主要設定檔位於 `guard-bootstrap/src/main/resources/`：

*   `application.yml`: 通用設定。
*   `application-dev.yml`: 開發環境特定設定 (預設啟用 `db` profile)。
*   `application-db.yml`: 資料庫連線設定。

## 6. 程式碼規範 (Coding Standards)

*   **Lombok**: 廣泛使用 `@Data`, `@Builder`, `@RequiredArgsConstructor` (用於建構子注入) 來減少樣板程式碼。
*   **Dependency Injection**: 優先使用建構子注入 (Constructor Injection)。
*   **API Path**: 所有 API 應位於 `/api` 路徑下。
*   **Logging**: 使用 `slf4j` (Lombok `@Slf4j`)。

## 7. 常見問題 (FAQ)

*   **Q: 為什麼 Controller 不直接呼叫 Repository?**
    *   A: 為了保持層次分明和業務邏輯的封裝。Controller 負責 HTTP 協定轉換，Service 負責業務流程，Repository 負責資料存取。

*   **Q: 如何處理跨模組依賴?**
    *   A: 遵循依賴反轉原則。上層模組依賴下層模組，或者依賴介面。`guard-domain` 不應依賴任何其他模組。
