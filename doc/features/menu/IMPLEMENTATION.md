# 選單功能重構完成總結

**日期**: 2026/3/15
**狀態**: ✅ 完成
**編譯狀態**: ✅ BUILD SUCCESS

## 重構概覽

選單功能已從 auth 模組成功分離，現在擁有獨立的 package 結構，並整合了基於使用者角色和權限的動態選單過濾。

## 建立的新檔案

### 1. Domain Layer (guard-domain)

| 檔案 | 說明 |
|-----|------|
| `com.domain.menu.MenuItem` | 選單項領域模型，新增權限控制欄位 |
| `com.domain.menu.Role` | 角色領域模型 |
| `com.domain.menu.Permission` | 權限領域模型 |

**關鍵變更**:
- MenuItem 新增欄位: `requiredRoles`, `requiredPermissions`, `requireAllRoles`
- 支援遞迴巢狀選單結構
- 支援權限驗證

### 2. Infrastructure Layer (guard-infrastructure)

| 檔案 | 說明 |
|-----|------|
| `com.infrastructure.entities.UserRole` | 使用者-角色對應實體 |
| `com.infrastructure.entities.UserPermission` | 使用者-權限對應實體 |
| `com.infrastructure.repositories.UserRoleRepository` | 使用者角色資料存取層 |
| `com.infrastructure.repositories.UserPermissionRepository` | 使用者權限資料存取層 |

**資料庫資料表**:
- `USER_ROLE_`: 儲存使用者與角色的對應關係
- `USER_PERMISSION_`: 儲存使用者與權限的對應關係

### 3. Application Layer (guard-application)

| 檔案 | 說明 |
|-----|------|
| `com.applications.menu.PermissionService` | 權限服務，取得目前使用者的角色和權限 |
| `com.applications.menu.MenuPermissionFilter` | 選單權限過濾器，實作遞迴權限驗證 |
| `com.applications.menu.MenuConfigProvider` | 選單設定提供者，管理選單項設定 |
| `com.applications.menu.MenuApplicationService` | 選單應用程式服務，整合權限過濾 |

**核心邏輯**:
- `PermissionService`: 從 SecurityContext 取得使用者權限，支援 AND/OR 邏輯
- `MenuPermissionFilter`: 遞迴過濾選單項，當所有子選單被過濾時移除父選單
- `MenuConfigProvider`: 提供選單設定，支援權限要求定義
- `MenuApplicationService`: 呼叫設定提供者和過濾器，回傳使用者有權存取的選單

### 4. API Layer (guard-api)

| 檔案 | 說明 |
|-----|------|
| `com.controllers.menu.MenuController` | 選單 REST 控制器 |

**端點**:
- `GET /api/menu`: 取得目前使用者的動態選單

## 刪除的舊檔案

- ❌ `com.applications.auth.MenuApplicationService`
- ❌ `com.controllers.auth.MenuController`
- ❌ `com.domain.auth.MenuItem`

## 更新的檔案

### 1. 安全設定 (guard-auth)

**檔案**: `com.security.SecurityConfig`
- 更新路由規則: `/menu` → `/menu/**`
- 允許所有使用者存取 `/menu/**` 端點

**檔案**: `com.security.JwtAuthenticationFilter`
- 更新 public endpoints 列表，支援所有 `/menu` 路由
- 更新過濾器邏輯從 `path.endsWith()` 改為 `path.contains()`

## 工作流程

### 選單取得流程

```
1. 使用者請求 GET /api/menu
        ↓
2. MenuController 接收請求
        ↓
3. MenuApplicationService.getUserMenu() 被呼叫
        ↓
4. MenuConfigProvider 回傳所有選單項設定
        ↓
5. MenuPermissionFilter.filterMenuItems() 過濾選單
        ↓
6. PermissionService 取得目前使用者權限
        ↓
7. 比對每個選單項的權限要求
        ↓
8. 遞迴過濾子選單
        ↓
9. 回傳使用者有權存取的選單列表
```

### 權限驗證邏輯

- 如果選單項無權限要求 → ✅ 通過
- 如果需要驗證角色:
  - `requireAllRoles = true` → 使用者必須擁有所有所需角色
  - `requireAllRoles = false` → 使用者只需擁有任一所需角色
- 如果需要驗證權限 → 使用者必須擁有任一所需權限
- 同時滿足角色和權限要求時 → ✅ 選單項顯示

### 巢狀選單處理

- 子選單權限被檢查
- 如果所有子選單都被過濾掉 → 父選單也被移除
- 支援多層級巢狀選單結構

## 選單設定範例

```yaml
選單項:
  - id: blog_posts
    name: blog_posts
    path: /blog-posts
    label: Blog Posts
    icon: article
    order: 1
    requiredRoles: [USER, ADMIN]      # 需要 USER 或 ADMIN 角色
    requiredPermissions: [BLOG_VIEW]
    requireAllRoles: false              # OR 邏輯

  - id: menu-management
    name: menu-management
    path: /menu-management
    label: Menu Management
    icon: settings
    order: 3
    requiredRoles: [ADMIN]              # 只有 ADMIN
    requiredPermissions: [MENU_MANAGE]
    requireAllRoles: true               # AND 邏輯 (必須擁有 ADMIN)
```

## 後續擴充建議

### Phase 1: 完成 ✅
- [x] Package 層級的功能分離
- [x] 權限驗證整合
- [x] 遞迴選單過濾
- [x] 設定化選單管理

### Phase 2: 資料庫持久化
- [ ] 從資料庫載入選單設定（替代硬編碼）
- [ ] 實作 `MenuRepository`
- [ ] 支援動態選單 CRUD 操作

### Phase 3: 效能最佳化
- [ ] 權限查詢快取（Redis）
- [ ] 選單結果快取（基於使用者權限組合）
- [ ] 短期快取策略（5-10 分鐘）

### Phase 4: 進階功能
- [ ] 選單權限管理介面
- [ ] 權限規則引擎
- [ ] 選單版本控制
- [ ] 稽核日誌

## 測試場景

### 場景 1: ADMIN 使用者
```
請求: GET /api/menu
權限: ROLE_ADMIN, MENU_VIEW_ALL, BLOG_MANAGE, CATEGORY_MANAGE, MENU_MANAGE
預期: 回傳所有選單項（包括管理功能）
```

### 場景 2: 普通 USER
```
請求: GET /api/menu
權限: ROLE_USER, BLOG_VIEW, CATEGORY_VIEW, MENU_VIEW
預期: 回傳部落格、分類選單，不回傳系統設定
```

### 場景 3: 未認證使用者
```
請求: GET /api/menu (無 token)
權限: 無
預期: 回傳空選單列表或錯誤
```

## 模組依賴關係

```
guard-api
  ↓ (依賴)
guard-application
  ↓ (依賴)
guard-domain + guard-infrastructure
  ↓ (依賴)
guard-auth (安全設定)
```

## 編譯驗證

✅ **編譯狀態**: SUCCESS
- guard-domain: ✅ SUCCESS
- guard-infrastructure: ✅ SUCCESS
- guard-auth: ✅ SUCCESS
- guard-application: ✅ SUCCESS
- guard-api: ✅ SUCCESS
- guard-bootstrap: ✅ SUCCESS

## 關鍵改進點

1. **清晰的關注點分離** - 選單功能獨立成自己的 package，不與 auth 混淆
2. **靈活的權限模型** - 支援角色和權限的組合驗證
3. **遞迴選單處理** - 支援多層級選單結構和權限檢查
4. **易於擴充** - PermissionService 和 MenuConfigProvider 預留了資料庫整合介面
5. **效能考慮** - 預留快取位置，支援後續效能最佳化

## 下一步行動

1. ✅ 修改 test-menu-auth.sh 腳本更新 URL 對應
2. ⏳ 建立整合測試驗證選單過濾邏輯
3. ⏳ 更新前端以支援權限相關的選單動態顯示
4. ⏳ 建立資料庫遷移腳本（新增權限表）
5. ⏳ 實作選單權限管理介面

---

**重構完成者**: GitHub Copilot
**完成日期**: 2026/3/15
**專案**: risk-guard
