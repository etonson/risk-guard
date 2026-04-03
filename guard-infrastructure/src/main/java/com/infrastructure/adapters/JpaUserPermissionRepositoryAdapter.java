package com.infrastructure.adapters;

import com.domain.user.UserPermissionRepository;
import com.infrastructure.entities.UserPermission;
import com.infrastructure.repositories.CommonUserPermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class JpaUserPermissionRepositoryAdapter implements UserPermissionRepository {

    private final CommonUserPermissionRepository jpaRepository;

    @Override
    public Set<String> findPermissionNamesByUserId(Long userId) {
        return jpaRepository.findByUserId(userId).stream()
                .map(UserPermission::getPermissionName)
                .collect(Collectors.toSet());
    }
}
