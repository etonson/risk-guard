# 選單功能重構 - 架構與流程圖

## 1. 模組結構圖

```
┌─────────────────────────────────────────────────────────────┐
│                     GUARD BOOTSTRAP                         │
│              (Application Entry Point)                      │
└─────────────────────────────────────────────────────────────┘
                            ↑
        ┌───────────────────┼───────────────────┐
        │                   │                   │
┌───────┴──────┐   ┌────────┴────────┐  ┌──────┴────────┐
│  GUARD API   │   │  GUARD AUTH     │  │ GUARD INFRA   │
│              │   │                 │  │               │
│ MenuController│  │ SecurityConfig  │  │ UserRole      │
│              │   │ JwtService      │  │ UserPermission│
│ AuthController│  │ JwtFilter       │  │ Repositories  │
└────────┬─────┘   └─────────────────┘  └───────────────┘
         │
         ↓
┌─────────────────────────────────────┐
│   GUARD APPLICATION                 │
│                                     │
│  ┌─────────────────────────────┐   │
│  │ MenuApplicationService      │   │
│  │ - getUserMenu()             │   │
│  └────────────┬────────────────┘   │
│               │                    │
│  ┌────────────┴────────────────┐   │
│  │ MenuConfigProvider          │   │
│  │ - getMenuConfiguration()    │   │
│  │ - getMenuItemById()         │   │
│  └─────────────────────────────┘   │
│                                     │
│  ┌─────────────────────────────┐   │
│  │ MenuPermissionFilter        │   │
│  │ - filterMenuItems()         │   │
│  │ - hasPermissionForMenuItem()│   │
│  └────────────┬────────────────┘   │
│               │                    │
│  ┌────────────┴────────────────┐   │
│  │ PermissionService           │   │
│  │ - getCurrentUserRoles()     │   │
│  │ - getCurrentUserPermissions()│  │
│  │ - hasAnyRole()              │   │
│  │ - hasAllRoles()             │   │
│  └─────────────────────────────┘   │
└─────────────────────────────────────┘
         ↑
         │
┌─────────────────────────────────────┐
│   GUARD DOMAIN                      │
│                                     │
│  ┌─────────────────────────────┐   │
│  │ MenuItem                    │   │
│  │ - id: String                │   │
│  │ - name: String              │   │
│  │ - path: String              │   │
│  │ - requiredRoles: Set        │   │ ← 權限控制欄位
│  │ - requiredPermissions: Set  │   │ ← 權限控制欄位
│  │ - requireAllRoles: boolean  │   │ ← 權限控制欄位
│  │ - children: List<MenuItem>  │   │
│  └─────────────────────────────┘   │
│                                     │
│  ┌─────────────────────────────┐   │
│  │ Role                        │   │
│  └─────────────────────────────┘   │
│                                     │
│  ┌─────────────────────────────┐   │
│  │ Permission                  │   │
│  └─────────────────────────────┘   │
└─────────────────────────────────────┘
```

## 2. 選單取得流程圖

```
                        ┌──────────────┐
                        │ Client/前端   │
                        └────────┬─────┘
                                 │
                        GET /api/menu
                                 │
                                 ↓
                        ┌──────────────────┐
                        │ MenuController   │
                        │ @GetMapping("")  │
                        └────────┬─────────┘
                                 │
                    menuService.getUserMenu()
                                 │
                                 ↓
                    ┌─────────────────────────┐
                    │ MenuApplicationService  │
                    └────────┬────────────────┘
                             │
              ┌──────────────┼──────────────┐
              │              │              │
              ↓              ↓              ↓
    ┌──────────────────┐  ┌─────────────────────────┐
    │MenuConfigProvider│  │MenuPermissionFilter     │
    │                  │  │                         │
    │Get All Menus     │  │Filter Menus             │
    │from Config       │  │                         │
    └────────┬─────────┘  └────────┬────────────────┘
             │                     │
             └─────────┬───────────┘
                       │
                       ↓
            ┌──────────────────────────┐
            │ PermissionService        │
            │ - getCurrentUserRoles()  │
            │ - getCurrentUserPermissions()
            │                          │
            │ Get from SecurityContext │
            └──────────────────────────┘
                       │
              ┌────────┴────────┐
              │                 │
              ↓                 ↓
    ┌──────────────────┐ ┌──────────────────┐
    │ Check Roles      │ │Check Permissions │
    │ ✓ Has ANY?       │ │ ✓ Has ANY?       │
    │ ✓ Has ALL?       │ │ ✓ Has ALL?       │
    └────────┬─────────┘ └────────┬─────────┘
             │                    │
             └────────┬───────────┘
                      │
                      ↓
         ┌────────────────────────┐
         │ Permission Check?      │
         │ (AND logic)            │
         └────────┬───────────────┘
                  │
        ┌─────────┴─────────┐
        │                   │
       YES                  NO
        │                   │
        ↓                   ↓
    ┌─────────┐      ┌─────────┐
    │ Include │      │ Exclude │
    │ Item    │      │ Item    │
    └────┬────┘      └─────────┘
         │
         ↓
   ┌──────────────────┐
   │Filter Children   │
   │(Recursively)     │
   └────────┬─────────┘
            │
         ┌──┴──┐
         │     │
        YES   NO
         │     │
         ↓     ↓
    ┌────────┐ ┌──────────────┐
    │Include │ │Remove Parent │
    │Child   │ │(if all kids  │
    │        │ │ filtered)    │
    └────────┘ └──────────────┘
         │
         ↓
┌──────────────────────────┐
│ Return Filtered Menu     │
│ List<MenuItem>           │
└────────┬─────────────────┘
         │
         ↓
┌──────────────────────────────────────┐
│ HTTP 200 OK                          │
│ {                                    │
│   "success": true,                   │
│   "data": [                          │
│     {id, name, path, label, ...}    │
│   ]                                  │
│ }                                    │
└──────────────────────────────────────┘
         │
         ↓
    Client/前端
```

## 3. 權限檢查邏輯圖

```
                  ┌──────────────────┐
                  │ MenuItem         │
                  │ requiredRoles: ? │
                  │ requiredPerms: ? │
                  │ requireAllRoles:?│
                  └────────┬─────────┘
                           │
                ┌──────────┴───────────┐
                │                      │
                ↓                      ↓
    ┌──────────────────────┐ ┌──────────────────────┐
    │ Both roles and perms │ │ Only one or none?    │
    │ are empty?           │ │                      │
    └─────┬────────────────┘ └─────┬────────────────┘
          │                        │
         NO                       YES
          │                        │
          ↓                        ↓
    ┌───────────────┐      ┌──────────────┐
    │ Check Roles   │      │ PASS ✓       │
    └────────┬──────┘      │ Show Item    │
             │             └──────────────┘
             │
    ┌────────┴────────┐
    │                 │
    ↓                 ↓
┌──────────────┐ ┌──────────────┐
│requireAllRoles
│ = true       │ │requireAllRoles
│ (AND)        │ │ = false      │
│              │ │ (OR)         │
│User has ALL  │ │User has ANY  │
│required?     │ │required?     │
└──────┬───────┘ └──────┬───────┘
       │                │
    ┌──┴──┐          ┌──┴──┐
    │     │          │     │
   YES   NO         YES   NO
    │     │          │     │
    ↓     ↓          ↓     ↓
┌─────┐ ┌────┐   ┌─────┐ ┌────┐
│Pass │ │Fail│   │Pass │ │Fail│
└─────┘ └────┘   └─────┘ └────┘
    │     │          │     │
    └──┬──┘          └──┬──┘
       │                │
       ↓                ↓
    ┌──────────────────────┐
    │ Check Permissions    │
    │ User has ANY perms?  │
    └─────┬────────────────┘
          │
       ┌──┴──┐
       │     │
      YES   NO
       │     │
       ↓     ↓
   ┌──────┐ ┌────┐
   │ PASS │ │FAIL│
   │ ✓    │ │ ✗  │
   └──────┘ └────┘
       │     │
       └──┬──┘
          │
          ↓
   ┌────────────────┐
   │ Final Result   │
   └────────────────┘
```

## 4. 選單項權限設定範例

```
┌─────────────────────────────────────────────────────────────┐
│                    ALL MENU ITEMS                           │
└─────────────────────────────────────────────────────────────┘

▼ Blog Posts (id: blog_posts)
  ├─ requiredRoles: [USER, ADMIN]
  ├─ requiredPermissions: [BLOG_VIEW]
  ├─ requireAllRoles: false  (OR logic)
  └─ 結果: USER 或 ADMIN 使用者可見

▼ Categories (id: categories)
  ├─ requiredRoles: [USER, ADMIN]
  ├─ requiredPermissions: [CATEGORY_VIEW]
  ├─ requireAllRoles: false  (OR logic)
  └─ 結果: USER 或 ADMIN 使用者可見

▼ Menu Management (id: menu-management)  ✓ ADMIN ONLY
  ├─ requiredRoles: [ADMIN]
  ├─ requiredPermissions: [MENU_MANAGE]
  ├─ requireAllRoles: true  (AND logic)
  └─ 結果: 只有 ADMIN 使用者可見

▼ System Settings (id: system-settings)  ✓ ADMIN ONLY
  ├─ requiredRoles: [ADMIN]
  ├─ requiredPermissions: []
  ├─ requireAllRoles: true  (AND logic)
  └─ 結果: 只有 ADMIN 使用者可見


┌─────────────────────────────────────────────────────────────┐
│                    AFTER FILTERING                          │
├─────────────────────────────────────────────────────────────┤
│ ADMIN User sees:                                            │
│  ✓ Blog Posts        ✓ Categories                          │
│  ✓ Menu Management   ✓ System Settings                     │
│                                                             │
│ USER User sees:                                             │
│  ✓ Blog Posts        ✓ Categories                          │
│  ✗ Menu Management   ✗ System Settings                     │
│                                                             │
│ Guest sees:                                                 │
│  ✗ Blog Posts        ✗ Categories                          │
│  ✗ Menu Management   ✗ System Settings                     │
└─────────────────────────────────────────────────────────────┘
```

## 5. 資料庫資料表結構 (新增)

```
USER_ROLE_ 表
┌──────────────────────────────────────┐
│ ID_ (PK)                             │
│ USER_ID_ (FK → COMMON_USER_.ID_)     │
│ ROLE_ID_                             │
│ ROLE_NAME_ (e.g., ADMIN, USER)       │
│ CREATED_AT_                          │
└──────────────────────────────────────┘
  Unique: (USER_ID_, ROLE_ID_)


USER_PERMISSION_ 表
┌──────────────────────────────────────┐
│ ID_ (PK)                             │
│ USER_ID_ (FK → COMMON_USER_.ID_)     │
│ PERMISSION_ID_                       │
│ PERMISSION_NAME_ (e.g., BLOG_VIEW)   │
│ CREATED_AT_                          │
└──────────────────────────────────────┘
  Unique: (USER_ID_, PERMISSION_ID_)
```

## 6. 關鍵類別關係圖

```
┌──────────────────┐
│ MenuController   │ (REST API)
└────────┬─────────┘
         │ uses
         ↓
┌──────────────────────────┐
│ MenuApplicationService   │ (Use Case)
└────────┬─────────────────┘
         │ uses
    ┌────┴─────────────────────────────┐
    │                                  │
    ↓                                  ↓
┌──────────────────────┐    ┌──────────────────────┐
│ MenuConfigProvider   │    │ MenuPermissionFilter │
└──────────────────────┘    └─────────┬────────────┘
                                      │ uses
                                      ↓
                            ┌──────────────────────┐
                            │ PermissionService    │
                            │                      │
                            │ + getCurrentUserRoles
                            │ + getCurrentUserPerms
                            │ + hasAnyRole()
                            │ + hasAllRoles()
                            │ + hasAnyPermission()
                            │ + hasAllPermissions()
                            └──────────────────────┘
                                      │ uses
                                      ↓
                            ┌──────────────────────┐
                            │ SecurityContextHolder│
                            │ (Spring Security)    │
                            └──────────────────────┘
```

---

**架構設計**: Hexagonal Architecture
**更新日期**: 2026/3/15
