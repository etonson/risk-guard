package com.controllers.menu;


import com.applications.common.dto.ApiResponse;
import com.applications.menu.MenuApplicationService;
import com.domain.menu.MenuItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Menu Controller
 * 
 * Handles menu-related API endpoints
 * 
 * @Author: Eton.Lin
 * @Date: 2026/3/15
 */
@RestController
@RequestMapping("/menu")
@RequiredArgsConstructor
@Slf4j
public class MenuController {

    private final MenuApplicationService menuService;

    /**
     * Get the menu for the current authenticated user
     * 
     * Returns a filtered list of menu items based on the user's roles and permissions
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<MenuItem>>> getMenu() {
        log.info("Menu endpoint called");
        List<MenuItem> menuData = menuService.getUserMenu();
        return ResponseEntity.ok(ApiResponse.success(menuData));
    }
}