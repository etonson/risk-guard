# 選單功能重構 - 快速參考指南

**完成日期**: 2026/3/15
**狀態**: ✅ 編譯通過

---

## 📁 新建立的檔案清單

### Domain Layer (guard-domain)
```
src/main/java/com/domain/menu/
├── MenuItem.java          ✅ 選單項模型 (+ 權限欄位)
├── Role.java              ✅ 角色模型
└── Permission.java        ✅ 權限模型
```

### Infrastructure Layer (guard-infrastructure)
```
src/main/java/com/infrastructure/
├── entities/
│   ├── UserRole.java      ✅ 使用者-角色實體
│   └── UserPermission.java ✅ 使用者-權限實體
└── repositories/
    ├── UserRoleRepository.java        ✅ 使用者角色倉儲
    └── UserPermissionRepository.java  ✅ 使用者權限倉儲
```

### Application Layer (guard-application)
```
src/main/java/com/applications/menu/
├── MenuApplicationService.java     ✅ 選單應用程式服務
├── MenuConfigProvider.java         ✅ 選單設定提供者
├── MenuPermissionFilter.java       ✅ 選單權限過濾器
└── PermissionService.java          ✅ 權限服務
```

### API Layer (guard-api)
```
src/main/java/com/controllers/menu/
└── MenuController.java             ✅ 選單 REST 控制器
```

---

## 🗑️ 刪除的檔案

```
❌ guard-application/src/.../com/applications/auth/MenuApplicationService.java
❌ guard-api/src/.../com/controllers/auth/MenuController.java
❌ guard-domain/src/.../com/domain/auth/MenuItem.java
```

---

## 📝 修改的檔案

### guard-auth/src/main/java/com/security/SecurityConfig.java
```java
// 第 70 行
// 修改前: .requestMatchers("/auth/login", "/auth/register", "/auth/refresh", "/menu")
// 修改後: .requestMatchers("/auth/login", "/auth/register", "/auth/refresh", "/menu/**")
```

### guard-auth/src/main/java/com/security/JwtAuthenticationFilter.java
```java
// 第 35-39 行
// 修改前: || path.endsWith("/api/menu")
// 修改後: || path.contains("/api/menu")
```

---

## 🔧 核心類別說明

### PermissionService
**功能**: 取得和驗證使用者權限

```java
// 取得目前使用者的所有角色
Set<String> getCurrentUserRoles()

// 取得目前使用者的所有權限
Set<String> getCurrentUserPermissions()

// 檢查使用者是否擁有任一角色 (OR 邏輯)
boolean hasAnyRole(Set<String> requiredRoles)

// 檢查使用者是否擁有所有角色 (AND 邏輯)
boolean hasAllRoles(Set<String> requiredRoles)

// 檢查使用者是否擁有任一權限
boolean hasAnyPermission(Set<String> requiredPermissions)

// 檢查使用者是否擁有所有權限
boolean hasAllPermissions(Set<String> requiredPermissions)
```

### MenuPermissionFilter
**功能**: 遞迴過濾選單項

```java
// 過濾選單列表
List<MenuItem> filterMenuItems(List<MenuItem> menuItems)

// 私有方法: 檢查選單項是否有權限
private boolean hasPermissionForMenuItem(MenuItem item)

// 私有方法: 遞迴過濾子選單
private List<MenuItem> filterMenuChildren(List<MenuItem> children)
```

### MenuConfigProvider
**功能**: 提供選單設定

```java
// 取得完整選單設定
List<MenuItem> getMenuConfiguration()

// 取得特定選單項
Optional<MenuItem> getMenuItemById(String id)
```

### MenuApplicationService
**功能**: 整合選單服務

```java
// 取得目前使用者的選單 (主要方法)
List<MenuItem> getUserMenu()
```

---

## 🔐 權限驗證規則

### 角色 (Role) 檢查

```java
// 場景 1: 需要 ADMIN 角色
MenuItem item = MenuItem.builder()
    .requiredRoles(Set.of("ADMIN"))
    .requireAllRoles(true)  // AND 邏輯
    .build();

// 場景 2: 需要 USER 或 ADMIN 角色
MenuItem item = MenuItem.builder()
    .requiredRoles(Set.of("USER", "ADMIN"))
    .requireAllRoles(false)  // OR 邏輯 (推薦)
    .build();
```

### 權限 (Permission) 檢查

```java
// 場景 1: 需要 BLOG_MANAGE 權限
MenuItem item = MenuItem.builder()
    .requiredPermissions(Set.of("BLOG_MANAGE"))
    .build();

// 場景 2: 需要 BLOG_VIEW 或 BLOG_MANAGE 權限
MenuItem item = MenuItem.builder()
    .requiredPermissions(Set.of("BLOG_VIEW", "BLOG_MANAGE"))
    .build();
```

### 組合驗證

```java
// 需要 (ADMIN 或 EDITOR) 角色 AND (BLOG_MANAGE 權限)
MenuItem item = MenuItem.builder()
    .requiredRoles(Set.of("ADMIN", "EDITOR"))
    .requiredPermissions(Set.of("BLOG_MANAGE"))
    .requireAllRoles(false)  // 角色用 OR 邏輯
    .build();

// 結果: (user.roles 包含 ADMIN 或 EDITOR) && (user.permissions 包含 BLOG_MANAGE)
```

---

## 📊 選單項設定範例

```java
// 部落格文章 - 所有使用者可見
MenuItem.builder()
    .id("blog_posts")
    .name("blog_posts")
    .label("Blog Posts")
    .path("/blog-posts")
    .icon("article")
    .order(1)
    .canDelete(true)
    .requiredRoles(Set.of("USER", "ADMIN"))
    .requiredPermissions(Set.of("BLOG_VIEW"))
    .requireAllRoles(false)
    .build()

// 選單管理 - 僅 ADMIN 可見
MenuItem.builder()
    .id("menu-management")
    .name("menu-management")
    .label("Menu Management")
    .path("/menu-management")
    .icon("settings")
    .order(3)
    .canDelete(false)
    .requiredRoles(Set.of("ADMIN"))
    .requiredPermissions(Set.of("MENU_MANAGE"))
    .requireAllRoles(true)
    .build()
```

---

## 🧪 測試場景

### 場景 A: 未認證使用者

```
請求: GET /api/menu
預期: 回傳無權限要求的選單項或空列表
```

### 場景 B: USER 使用者

```
請求: GET /api/menu (with Bearer token, ROLE_USER)
權限: ROLE_USER, BLOG_VIEW, CATEGORY_VIEW
預期: Blog Posts, Categories (不包括 Menu Management)
```

### 場景 C: ADMIN 使用者

```
請求: GET /api/menu (with Bearer token, ROLE_ADMIN)
權限: ROLE_ADMIN, 所有權限
預期: 所有選單項 (Blog Posts, Categories, Menu Management, System Settings)
```

---

## 🚀 API 使用範例

### 取得選單

```bash
# 不認證
curl http://localhost:8080/api/menu

# 帶認證
curl -H "Authorization: Bearer eyJhbGc..." http://localhost:8080/api/menu

# 完整回應範例
{
  "success": true,
  "message": "success",
  "data": [
    {
      "id": "blog_posts",
      "name": "blog_posts",
      "path": "/blog-posts",
      "label": "Blog Posts",
      "icon": "article",
      "order": 1,
      "canDelete": true,
      "children": null
    },
    {
      "id": "categories",
      "name": "categories",
      "path": "/categories",
      "label": "Categories",
      "icon": "category",
      "order": 2,
      "canDelete": true,
      "children": null
    }
  ]
}
```

---

## 📈 權限流程圖

```
使用者發送請求
    ↓
MenuController 接收
    ↓
MenuApplicationService.getUserMenu()
    ↓
MenuConfigProvider 回傳所有選單
    ↓
MenuPermissionFilter 過濾
    ↓
PermissionService 取得使用者權限
    ↓
逐項檢查權限
    ├─ 角色檢查 (hasAnyRole / hasAllRoles)
    ├─ 權限檢查 (hasAnyPermission)
    └─ 組合驗證
    ↓
遞迴處理子選單
    ├─ 子選單都被過濾 → 移除父選單
    └─ 至少有一個子選單 → 保留父選單
    ↓
回傳過濾後的選單
```

---

## 🔄 後續整合步驟

### Step 1: 資料庫遷移
```sql
-- 建立使用者角色表
CREATE TABLE USER_ROLE_ (
    ID_ BIGSERIAL PRIMARY KEY,
    USER_ID_ BIGINT NOT NULL,
    ROLE_ID_ VARCHAR(50) NOT NULL,
    ROLE_NAME_ VARCHAR(50) NOT NULL,
    CREATED_AT_ TIMESTAMP NOT NULL,
    UNIQUE(USER_ID_, ROLE_ID_),
    FOREIGN KEY(USER_ID_) REFERENCES COMMON_USER_(ID_)
);

-- 建立使用者權限表
CREATE TABLE USER_PERMISSION_ (
    ID_ BIGSERIAL PRIMARY KEY,
    USER_ID_ BIGINT NOT NULL,
    PERMISSION_ID_ VARCHAR(50) NOT NULL,
    PERMISSION_NAME_ VARCHAR(50) NOT NULL,
    CREATED_AT_ TIMESTAMP NOT NULL,
    UNIQUE(USER_ID_, PERMISSION_ID_),
    FOREIGN KEY(USER_ID_) REFERENCES COMMON_USER_(ID_)
);
```

### Step 2: 測試
```bash
# 單元測試
mvn test -Dtest=MenuApplicationServiceTest

# 整合測試
mvn verify

# 執行應用程式
mvn spring-boot:run
```

### Step 3: 前端整合
```typescript
// 前端已支援，無需修改
async function fetchMenu() {
  const response = await fetch('/api/menu', {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  return response.json();
}
```

---

## ✅ 驗證清單

- [x] 選單功能從 auth 分離到獨立 package
- [x] 權限驗證整合
- [x] 遞迴選單過濾實作
- [x] 設定化選單管理
- [x] 編譯通過 (BUILD SUCCESS)
- [x] SecurityConfig 更新
- [x] JwtAuthenticationFilter 更新
- [x] 舊檔案清理

## 🐛 常見問題

### Q1: 如何新增選單項？
**A**: 在 `MenuConfigProvider.getMenuConfiguration()` 中新增新的 `MenuItem.builder()` 設定

### Q2: 如何修改權限要求？
**A**: 編輯選單項的 `requiredRoles` 和 `requiredPermissions` 欄位

### Q3: 如何測試權限過濾？
**A**: 修改 `PermissionService.getCurrentUserPermissions()` 回傳不同的權限集合進行測試

### Q4: 快取在哪裡？
**A**: 目前無快取。建議後續新增 Redis 快取層

### Q5: 如何從資料庫載入選單？
**A**: 擴充 `MenuConfigProvider` 新增 `MenuRepository` 依賴即可

---

## 📚 相關文件

- 📄 [選單重構總結](./MENU_REFACTOR_SUMMARY.md)
- 📊 [架構與流程圖](./MENU_ARCHITECTURE_DIAGRAMS.md)
- 📋 [契約實作對應](./MENU_CONTRACT_IMPLEMENTATION.md)
- 📖 [後端契約](./backend-menu-contract.yml)

---

**快速參考完成**: 2026/3/15
**專案**: risk-guard
**模組**: guard-menu (Package 層級)
