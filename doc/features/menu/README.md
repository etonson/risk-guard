# 📦 選單功能模組 (Menu Module)

選單模組是 Risk Guard 的核心組件，負責根據使用者的角色與權限，動態生成並過濾導航選單。

---

## 📋 模組概覽

該模組提供高靈活性的選單管理，支援：
- **動態過濾**: 基於 `SecurityContext` 中的角色與權限進行過濾。
- **遞迴結構**: 支援多層級巢狀選單。
- **權限規約**: 可配置 AND (需所有角色) 或 OR (任一角色) 的驗證邏輯。

---

## 🏗️ 架構與實作

### 1. 關鍵組件
- **`MenuController`**: 公開 `GET /api/menu` 端點。
- **`MenuApplicationService`**: 編排選單獲取流程。
- **`MenuConfigProvider`**: 管理選單的基礎配置 (當前為硬編碼，可擴展至資料庫/YAML)。
- **`MenuFilterService`**: 執行遞迴過濾邏輯的核心。
- **`PermissionService`**: 提供與安全層的對接，驗證具體權限。

### 2. 權限判斷邏輯
在 `MenuFilterService` 中，選單的可見性由以下規則決定：
- 如果選單項目未設置 `requiredRoles` 或 `requiredPermissions`，則對所有已認證用戶開放。
- **Roles 判斷**: 
  - `requireAllRoles = true`: 用戶必須擁有所有指定角色。
  - `requireAllRoles = false`: 用戶只需擁有其中任一角色。
- **Permissions 判斷**: 只要用戶具備其中任一指定權限即可通過。
- **最終結果**: 角色通過 **且** 權限通過，選單才可見。

---

## ⚙️ 選單配置範例

配置位於 `MenuConfigProvider.java`：

```java
menuItems.add(MenuItem.builder()
        .id("menu-management")
        .label("Menu Management")
        .path("/menu-management")
        .requiredRoles(Set.of("ADMIN"))
        .requiredPermissions(Set.of("MENU_MANAGE"))
        .requireAllRoles(true)
        .build());
```

---

## 📡 API 參考

### 獲取當前用戶選單
- **端點**: `GET /api/menu`
- **認證**: 需要 Bearer Token 或 Cookie。
- **回應**: 
  ```json
  {
    "code": 200,
    "message": "success",
    "data": [
      {
        "id": "blog_posts",
        "label": "Blog Posts",
        "path": "/blog-posts",
        "children": [...]
      }
    ]
  }
  ```

---
**版本**: 2.1  
**最後更新**: 2026-03-29
