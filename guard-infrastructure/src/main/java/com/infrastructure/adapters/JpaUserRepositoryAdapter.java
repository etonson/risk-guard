package com.infrastructure.adapters;

import com.domain.user.*;
import com.domain.user.repositories.UserRepository;
import com.infrastructure.entities.CommonUser;
import com.infrastructure.entities.UserPermission;
import com.infrastructure.entities.UserRole;
import com.infrastructure.repositories.CommonUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * User Repository Adapter
 * <p>
 * 負責將 Domain 層的請求轉發給 JPA Repository，並處理實體轉換。
 */
@Repository
@RequiredArgsConstructor
public class JpaUserRepositoryAdapter implements UserRepository {

    private final CommonUserRepository jpaRepository;

    @Override
    public Optional<User> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return jpaRepository.findByUsername(username).map(this::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(this::toDomain);
    }

    @Override
    public User save(User user) {
        CommonUser entity = fromDomain(user);
        return toDomain(jpaRepository.save(entity));
    }

    @Override
    public boolean existsByUsername(String username) {
        return jpaRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    // --- Mappings ---

    private User toDomain(CommonUser entity) {
        if (entity == null) return null;
        return User.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .password(entity.getPassword())
                .realName(entity.getRealName())
                .status(entity.getStatus())
                .roles(mapRolesToDomain(entity.getRoles()))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private CommonUser fromDomain(User user) {
        if (user == null) return null;
        CommonUser entity = new CommonUser();
        entity.setId(user.getId());
        entity.setUsername(user.getUsername());
        entity.setEmail(user.getEmail());
        entity.setPassword(user.getPassword());
        entity.setRealName(user.getRealName());
        entity.setStatus(user.getStatus());
        entity.setRoles(mapRolesFromDomain(user.getRoles()));
        return entity;
    }

    private Set<Role> mapRolesToDomain(Set<UserRole> roles) {
        if (roles == null) return null;
        return roles.stream().map(r -> Role.builder()
                .code(r.getCode())
                .name(r.getName())
                .dataScope(r.getDataScope())
                .permissions(r.getPermissions().stream().map(p -> Permission.builder()
                        .code(p.getCode())
                        .name(p.getName())
                        .type(p.getType())
                        .description(p.getDescription())
                        .build()).collect(Collectors.toSet()))
                .build()).collect(Collectors.toSet());
    }

    private Set<UserRole> mapRolesFromDomain(Set<Role> roles) {
        if (roles == null) return null;
        return roles.stream().map(r -> UserRole.builder()
                .code(r.getCode())
                .name(r.getName())
                .dataScope(r.getDataScope())
                .permissions(r.getPermissions().stream().map(p -> UserPermission.builder()
                        .code(p.getCode())
                        .name(p.getName())
                        .type(p.getType())
                        .description(p.getDescription())
                        .build()).collect(Collectors.toSet()))
                .build()).collect(Collectors.toSet());
    }
}
