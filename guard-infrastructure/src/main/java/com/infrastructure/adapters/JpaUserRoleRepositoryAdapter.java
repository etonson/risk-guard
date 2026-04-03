package com.infrastructure.adapters;

import com.domain.user.UserRoleRepository;
import com.infrastructure.entities.UserRole;
import com.infrastructure.repositories.CommonUserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class JpaUserRoleRepositoryAdapter implements UserRoleRepository {

    private final CommonUserRoleRepository jpaRepository;

    @Override
    public Set<String> findRoleNamesByUserId(Long userId) {
        return jpaRepository.findByUserId(userId).stream()
                .map(UserRole::getRoleName)
                .collect(Collectors.toSet());
    }
}
