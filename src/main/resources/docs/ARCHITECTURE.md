# 系統架構說明書 (System Architecture Document)

## 1. 架構概觀 (Architecture Overview)

本專案採用 **六邊形架構 (Hexagonal Architecture)**，又稱為 **埠與適配器架構 (Ports and Adapters Architecture)**。此架構的核心目標是將業務邏輯（Domain）與外部技術細節（如資料庫、UI、外部 API）解耦，使系統更易於測試、維護與擴充。

系統被劃分為幾個同心圓層次，依賴關係只能**由外向內**指向核心。

### 架構圖示 (Conceptual Diagram)

```mermaid
graph TD
    subgraph "External (Driving Adapters)"
        Web[Web / REST API]
    end

    subgraph "Application Core"
        subgraph "Application Layer"
            AppService[Application Services]
        end
        subgraph "Domain Layer"
            DomainModel[Domain Models]
            DomainPorts[Ports (Interfaces)]
        end
    end

    subgraph "Infrastructure (Driven Adapters)"
        DB[Database / JPA]
        Security[Security / JWT]
    end

    Web --> AppService
    AppService --> DomainModel
    AppService --> DomainPorts
    DB -.->|implements| DomainPorts
    Security -.->|implements| DomainPorts
```

## 2. 模組設計 (Module Design)

專案採用 Maven 多模組 (Multi-module) 結構，強制執行架構邊界。

| 模組名稱 | 層級 | 職責描述 | 主要依賴 |
| :--- | :--- | :--- | :--- |
| **`guard-domain`** | 核心層 (Core) | 定義業務實體 (Entities)、值物件 (Value Objects) 與 埠介面 (Ports)。此層**不依賴**任何框架或外部庫 (POJO)。 | 無 (僅 JDK) |
| **`guard-application`** | 應用層 (App) | 實作使用案例 (Use Cases)。協調 Domain 物件完成業務流程。 | `guard-domain` |
| **`guard-api`** | 介面層 (Driving) | 處理外部輸入 (HTTP Requests)。包含 Controllers 與 DTOs。 | `guard-application` |
| **`guard-infrastructure`** | 基礎設施層 (Driven) | 實作 Domain 層定義的介面 (如 Repository)。包含資料庫存取細節。 | `guard-domain` |
| **`guard-auth`** | 安全層 (Cross-cutting) | 處理認證 (Authentication) 與授權 (Authorization)。 | `guard-domain`, `guard-application` |
| **`guard-bootstrap`** | 啟動層 (Bootstrap) | 應用程式進入點。負責組裝所有模組並啟動 Spring Boot。 | 所有其他模組 |

## 3. 詳細層級說明 (Layer Details)

### 3.1 Domain Layer (`guard-domain`)
這是系統的心臟。
*   **內容**:
    *   **Entities**: 具有唯一識別碼的業務物件。
    *   **Repositories Interfaces (Ports)**: 定義資料存取的合約，但不包含實作。
*   **原則**: 純 Java 物件，不依賴 Spring 或 Hibernate 等框架註解（理想情況下，但為了便利有時會包含 JPA 註解，視團隊嚴格程度而定）。

### 3.2 Application Layer (`guard-application`)
負責編排業務流程。
*   **內容**: Service 類別。
*   **職責**:
    *   接收來自 Interface 層的請求。
    *   呼叫 Domain 層的 Repository 介面獲取資料。
    *   執行業務邏輯。
    *   回傳結果給 Interface 層。

### 3.3 Infrastructure Layer (`guard-infrastructure`)
提供技術實作。
*   **內容**:
    *   **Repository Implementation**: 使用 Spring Data JPA 或 Hibernate 實作 Domain 層定義的介面。
    *   **External APIs**: 呼叫第三方服務的客戶端。

### 3.4 Interface Layer (`guard-api`)
系統的門面。
*   **內容**: REST Controllers。
*   **職責**:
    *   解析 HTTP 請求 (JSON, Parameters)。
    *   驗證輸入資料 (Validation)。
    *   呼叫 Application Service。
    *   將結果轉換為 HTTP 回應 (DTOs)。

### 3.5 Security Layer (`guard-auth`)
*   **技術**: Spring Security, JWT (JSON Web Token)。
*   **流程**:
    *   **Authentication**: 驗證使用者身分 (Login)。
    *   **Authorization**: 驗證使用者權限 (Role-based access)。
    *   **Filter**: `JwtAuthenticationFilter` 攔截請求並驗證 Token。

## 4. 資料流向 (Data Flow)

一個典型的 API 請求流程如下：

1.  **Request**: 使用者發送 HTTP 請求至 `guard-api` (Controller)。
2.  **Input**: Controller 將請求轉換為 DTO，並呼叫 `guard-application` (Service)。
3.  **Process**: Service 執行業務邏輯，透過介面呼叫 `guard-domain` (Repository Port)。
4.  **Persistence**: `guard-infrastructure` (Repository Impl) 執行實際資料庫操作。
5.  **Return**: 資料經由 Service 處理後，回傳給 Controller。
6.  **Response**: Controller 將結果包裝成 HTTP Response 回傳給使用者。

## 5. 技術決策 (Technical Decisions)

*   **Java 21**: 使用最新的 LTS 版本，利用新特性 (Records, Pattern Matching 等)。
*   **Spring Boot 4.0.0**: 現代化企業級框架，提供開箱即用的功能。
*   **PostgreSQL 16+**: 開源的強大關聯式資料庫，具有良好的性能與可靠性。
*   **Hibernate 7.1.11 & Spring Data JPA**: 業界標準的 ORM 解決方案。
*   **JWT (JJWT 0.12.6)**: 無狀態的 Token 認證機制，適合微服務與分散式系統。
*   **Lombok 1.18.42**: 簡化 Java 程式碼，自動產生 Getter/Setter/Builder。

## 6. 擴充性與維護 (Scalability & Maintenance)

*   **更換資料庫**: 由於 Domain 層只依賴介面，若要更換資料庫，只需重寫 Infrastructure 層的實作，不影響核心業務邏輯。
*   **新增介面**: 若需新增 GraphQL 或 gRPC 介面，只需新增對應的 Interface 模組，重用現有的 Application 邏輯。
