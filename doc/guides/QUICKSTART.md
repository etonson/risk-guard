# 快速開發指南 (Quick Start Guide)

## 1️⃣ 環境準備

### 安裝必要工具
```bash
# 檢查 Java 版本 (需要 Java 21+)
java -version

# 檢查 Maven 版本 (需要 Maven 3.8+)
mvn -version
```

### 啟動 PostgreSQL
```bash
# 使用 Docker Compose (推薦)
cd /home/sixson/podmans/PostgreSQLForMe
docker-compose up -d

# 或 Docker 命令
docker run --name postgres-dev \
  -e POSTGRES_USER=sa \
  -e POSTGRES_PASSWORD=A@t123456 \
  -e POSTGRES_DB=mydatabase \
  -p 5432:5432 \
  -d postgres:16-alpine
```

## 2️⃣ 驗證數據庫連線

```bash
# 使用 psql 連接 (需要安裝 PostgreSQL 客戶端)
psql -h localhost -p 5432 -U sa -d mydatabase

# 或使用 DBeaver 等圖形化工具
# 連線資訊:
# - Host: localhost
# - Port: 5432
# - Username: sa
# - Password: A@t123456
# - Database: mydatabase
```

## 3️⃣ 建置專案

```bash
# 進入專案目錄
cd /home/sixson/IdeaProjects/risk-guard

# 清理並編譯
mvn clean install

# 跳過測試 (可選)
mvn clean install -DskipTests
```

## 4️⃣ 執行應用程式

```bash
# 方式一：使用 Maven Plugin (推薦用於開發)
mvn spring-boot:run -pl guard-bootstrap

# 方式二：執行 JAR 檔案
java -jar guard-bootstrap/target/guard-bootstrap-1.0.0.jar

# 方式三：在 IDE 中執行 DemoApplication 類別
# 右鍵點擊 guard-bootstrap/src/main/java/com/DemoApplication.java
# 選擇 "Run DemoApplication"
```

## 5️⃣ 驗證應用程式運行

```bash
# 檢查應用是否正常啟動 (查看日誌是否有 "Started DemoApplication")
curl http://localhost:8080/api/health

# 獲取當前用戶資訊 (需要認證令牌)
curl -H "Authorization: Bearer YOUR_JWT_TOKEN" \
     http://localhost:8080/api/auth/me
```

## 6️⃣ 常見問題排查

### ❌ 連線到 PostgreSQL 失敗

```
Caused by: org.postgresql.util.PSQLException: 
FATAL: password authentication failed for user "sa"
```

**解決方案**:
1. 確認 PostgreSQL 正在運行: `docker ps | grep postgres`
2. 驗證密碼是否正確: 檢查 `application-db.yml` 中的密碼
3. 驗證用戶名稱: 應為 `sa` (非預設的 `postgres`)
4. 檢查數據庫名稱: 應為 `mydatabase`

### ❌ Port 5432 已被占用

```bash
# 查找佔用 port 5432 的進程
lsof -i :5432

# 停止舊的 PostgreSQL 容器
docker ps -a | grep postgres
docker stop <container_id>
docker rm <container_id>

# 重新啟動
cd /home/sixson/podmans/PostgreSQLForMe
docker-compose up -d
```

### ❌ Java 版本不符

```bash
# 檢查並切換到 Java 21
java -version

# 如使用 SDKMAN (推薦)
sdk install java 21-open
sdk use java 21-open
```

## 7️⃣ 開發工作流程

### 新增新功能的步驟

1. **在 Domain 層定義**
   ```
   guard-domain/src/main/java/com/domain/
   ├── entity/
   │   └── YourEntity.java
   └── port/
       └── YourRepository.java (interface)
   ```

2. **在 Infrastructure 層實現**
   ```
   guard-infrastructure/src/main/java/com/infrastructure/
   └── repository/
       └── YourRepositoryImpl.java
   ```

3. **在 Application 層添加邏輯**
   ```
   guard-application/src/main/java/com/applications/
   └── YourService.java
   ```

4. **在 Interface 層暴露 API**
   ```
   guard-api/src/main/java/com/controllers/
   └── YourController.java
   ```

## 8️⃣ 相關文檔

- 📖 **架構設計**: `/src/main/resources/docs/ARCHITECTURE.md`
- 📖 **開發者指南**: `/src/main/resources/docs/DEVELOPER_GUIDE.md`
- 📖 **技術堆疊掃描**: `/TECH_STACK_SCAN.md`
- 📖 **專案 README**: `/README.md`

## 9️⃣ 實用命令速查表

```bash
# 編譯專案 (包含依賴下載)
mvn clean install

# 只編譯，不執行測試
mvn compile

# 執行應用程式
mvn spring-boot:run -pl guard-bootstrap

# 查看依賴樹
mvn dependency:tree

# 查看特定模組的依賴
mvn dependency:tree -pl guard-bootstrap

# 清理構建文件
mvn clean

# 執行測試
mvn test

# 查看 Maven 幫助
mvn help:active-profiles
```

## 🔟 聯繫與支援

遇到問題？檢查：
1. PostgreSQL 是否正常運行
2. 數據庫密碼是否正確
3. Java 版本是否為 21+
4. Maven 依賴是否下載完整


