# 🔐 角色與權限系統重構大藍圖 (RBAC 2.0 + ABAC)

本文件定義了 Risk Guard 系統從「簡單角色檢查」演進為「精確權限點控制」的重構計畫。

---

## 🏗️ 1. 核心設計哲學

- **權限點為王 (Permission-Based)**: 代碼中嚴禁出現 `hasRole('ADMIN')`。所有的功能存取控制（API 攔截、按鈕顯示）必須基於權限代碼（如 `user:delete`）。
- **角色即集合 (Role as Container)**: 角色僅作為一組權限的載體，便於管理與分配。
- **數據範疇 (Data Scope)**: 解決「能做什麼 (RBAC)」與「能看哪些數據 (ABAC)」的解耦。

---

## 🗄️ 2. 領域模型設計 (Domain Model)

### A. 實體關係 (E-R Concept)
1. **User (使用者)**: 帳號主體，關聯多個 Role。
2. **Role (角色)**: 權限集合，定義該角色的 `DataScope`（如：全部、本部門、僅本人）。
3. **Permission (權限點)**: 最小功能單元。
   - `code`: `sys:user:view`, `risk:rule:approve`
   - `type`: `MENU` (選單), `BUTTON` (按鈕), `API` (接口)

### B. 數據範疇定義
- `DATA_SCOPE_ALL`: 可查看系統所有數據。
- `DATA_SCOPE_DEPT`: 僅可查看所屬部門及其下屬部門數據。
- `DATA_SCOPE_SELF`: 僅可查看與自身關聯的數據。

---

## 🛠️ 3. 分層實作路徑

### 第一階段：Domain 層 (核心定義)
- 在 `com.domain.user` 下新增 `Role` 與 `Permission` 實體。
- `User` 實體增加 `getFlattenedPermissions()` 方法，遞迴獲取所有角色的權限點。

### 第二階段：Infrastructure 層 (持久化)
- **資料庫表**:
  - `sys_user`, `sys_role`, `sys_permission`
  - `sys_user_role` (關聯表)
  - `sys_role_permission` (關聯表)
- **Adapter**: 實作 `JpaUserRepositoryAdapter`，確保在載入 User 時，使用 `JOIN FETCH` 一次性載入權限樹，避免 N+1 問題。

### 第三階段：Auth 層 (安全性配置)
- **`UserDetailsServiceImpl`**: 將 User 的 `Permissions` 轉換為 Spring Security 的 `GrantedAuthority`。
- **`JwtAuthenticationFilter`**: 保持無狀態，但將權限點編碼進 JWT 或在解析後放入 `SecurityContext`。

### 第四階段：API 層 (強制執法)
- 使用 `@PreAuthorize` 註解保護 Controller 方法：
  ```java
  @PostMapping("/rules")
  @PreAuthorize("hasAuthority('risk:rule:create')")
  public ApiResponse<Void> createRule(...) { ... }
  ```

### 第五階段：Application 層 (選單過濾)
- `MenuFilterService` 升級：
  - 獲取當前用戶所有 `Authorities`。
  - 根據 `MenuItem.requiredPermission` 進行遞迴過濾。
  - **不再傳遞過多角色資訊，只回傳用戶「能看見」的樹狀選單。**

---

## 📊 4. 數據權限 (Data Scope) 實作方案

我們採用 **AOP + SQL 攔截** 的方式實作：

1. **定義註解**: `@DataScope(userField = "creator_id", deptField = "dept_id")`。
2. **實作切面**: 在執行 Repository 查詢前，攔截當前用戶的 `DataScope` 屬性。
3. **注入過濾**: 
   - 如果是 `SELF`，自動在 SQL 尾部追加 `AND creator_id = current_user_id`。
   - 如果是 `DEPT`，自動追加 `AND dept_id IN (sub_dept_ids)`。

---

## 🚀 5. 遷移與兼容性策略

1. **雙軌並行**: 初期保留 `roles` 陣列以兼容舊前端，但在後端標註為 `@Deprecated`。
2. **自動映射**: 啟動時自動掃描所有 `@PreAuthorize` 註解，將權限點自動同步至 `sys_permission` 表，減少手動維護。
3. **前端適配**: 提供 `/api/auth/permissions` 接口，讓前端能根據權限清單動態控制按鈕的 `disabled` 狀態。

---
**文件狀態**: 規劃中 (In Progress)
**維護者**: Risk Guard Architecture Team
