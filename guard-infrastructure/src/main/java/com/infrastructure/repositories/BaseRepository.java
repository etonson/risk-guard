package com.infrastructure.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

/**
 * 基礎 Repository 介面，提供通用的 CRUD 操作。
 * 所有具體的 Repository 介面都應該繼承此介面。
 *
 * @param <T> 實體類型
 * @param <ID> 主鍵類型
 */
@NoRepositoryBean
public interface BaseRepository<T, ID extends Serializable> extends JpaRepository<T, ID> {
}
