package com.applications.menu;

import com.domain.menu.MenuItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Menu Filter Service - 精簡優化版本 (重構後)
 * <p>
 * 目標：降低認知負擔，將「過濾、遞迴、建構」邏輯分離。
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MenuFilterService {

    private final PermissionService permissionService;

    /**
     * 過濾使用者選單
     * @param menuItems 原始選單列表
     * @param userIdentifier 使用者識別 (email/username)
     * @return 經過濾後的樹狀選單
     */
    public List<MenuItem> filterMenuItems(List<MenuItem> menuItems, String userIdentifier) {
        return filter(menuItems, userIdentifier);
    }

    /**
     * 核心遞迴邏輯：一眼看清「遞迴 -> 判斷 -> 建構」
     */
    private List<MenuItem> filter(List<MenuItem> items, String userIdentifier) {
        if (items == null || items.isEmpty()) return List.of();

        List<MenuItem> result = new ArrayList<>();

        for (MenuItem item : items) {
            // 1. 先遞迴處理子節點 (Bottom-up)
            List<MenuItem> filteredChildren = filter(item.getChildren(), userIdentifier);

            // 2. 判斷顯示條件：自己有權限 OR 子節點有內容 (解決父無權限但子有的 UX 問題)
            if (hasAccess(item, userIdentifier) || !filteredChildren.isEmpty()) {
                result.add(copy(item, filteredChildren));
            }
        }

        return result;
    }

    /**
     * 權限判斷：收斂成語意清楚的單一判斷邏輯
     */
    private boolean hasAccess(MenuItem item, String identifier) {
        boolean noRoleRequired = item.getRequiredRoles() == null || item.getRequiredRoles().isEmpty();
        boolean noPermissionRequired = item.getRequiredPermissions() == null || item.getRequiredPermissions().isEmpty();

        // 無限制條件則直接放行
        if (noRoleRequired && noPermissionRequired) return true;

        // 角色驗證
        boolean rolesPass = noRoleRequired || (item.isRequireAllRoles() 
            ? permissionService.hasAllRoles(identifier, item.getRequiredRoles())
            : permissionService.hasAnyRole(identifier, item.getRequiredRoles()));

        // 權限驗證 (只要具備其中之一權限點即可)
        boolean permissionsPass = noPermissionRequired || 
            permissionService.hasAnyPermission(identifier, item.getRequiredPermissions());

        return rolesPass && permissionsPass;
    }

    /**
     * 隔離 Builder 噪音：負責單純的資料投影 (Mapping/Copying)
     */
    private MenuItem copy(MenuItem item, List<MenuItem> children) {
        return MenuItem.builder()
                .id(item.getId())
                .name(item.getName())
                .path(item.getPath())
                .label(item.getLabel())
                .icon(item.getIcon())
                .order(item.getOrder())
                .canDelete(item.getCanDelete())
                .description(item.getDescription())
                .meta(item.getMeta())
                .children(children.isEmpty() ? null : children)
                .requiredRoles(item.getRequiredRoles())
                .requiredPermissions(item.getRequiredPermissions())
                .requireAllRoles(item.isRequireAllRoles())
                .build();
    }
}
