package com.controllers.menu;


import com.applications.common.dto.ApiResponse;
import com.applications.menu.MenuApplicationService;
import com.domain.menu.MenuItem;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Menu Controller
 * <p>
 * Handles menu-related API endpoints
 * 
 * @Author: Eton.Lin
 * @Date: 2026/3/15
 */
@RestController
@RequestMapping("/menu")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Menu", description = "動態選單獲取與過濾")
public class MenuController {

    private final MenuApplicationService menuService;

    /**
     * Get the menu for the current authenticated user
     * <p>
     * Returns a filtered list of menu items based on the user's roles and permissions
     */
    @GetMapping
    @Operation(summary = "獲取使用者選單", description = "根據當前登入使用者的權限，返回經過濾後的導航選單。")
    public ResponseEntity<ApiResponse<List<MenuItem>>> getMenu() {
        log.info("Menu endpoint called");
        List<MenuItem> menuData = menuService.getUserMenu();
        return ResponseEntity.ok(ApiResponse.success(menuData));
    }
}