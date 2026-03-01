package com.infrastructure.repositories;

import com.infrastructure.entities.CommonUser;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 使用者 Repository 介面。
 * 繼承自 BaseRepository，獲得基本的 CRUD 功能。
 */
@Repository
public interface CommonUserRepository extends BaseRepository<CommonUser, Long> {

    /**
     * 根據使用者名稱查詢使用者。
     *
     * @param username 使用者名稱
     * @return 包含使用者的 Optional 物件，若找不到則為 empty
     */
    Optional<CommonUser> findByUsername(String username);

    /**
     * 根據電子郵箱查詢使用者。
     *
     * @param email 電子郵箱
     * @return 包含使用者的 Optional 物件，若找不到則為 empty
     */
    Optional<CommonUser> findByEmail(String email);

    /**
     * 檢查使用者名稱是否存在。
     *
     * @param username 使用者名稱
     * @return 若存在則回傳 true，否則回傳 false
     */
    boolean existsByUsername(String username);

    /**
     * 檢查電子郵箱是否存在。
     *
     * @param email 電子郵箱
     * @return 若存在則回傳 true，否則回傳 false
     */
    boolean existsByEmail(String email);
}
