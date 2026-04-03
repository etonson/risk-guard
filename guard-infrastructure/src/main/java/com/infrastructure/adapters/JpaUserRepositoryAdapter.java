package com.infrastructure.adapters;

import com.domain.user.User;
import com.domain.user.UserRepository;
import com.domain.user.UserStatus;
import com.infrastructure.entities.CommonUser;
import com.infrastructure.repositories.CommonUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

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
                .status(
                        entity.getStatus() == UserStatus.ACTIVE ? UserStatus.ACTIVE : UserStatus.DISABLED)
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
        entity.setStatus(user.getStatus() == UserStatus.ACTIVE ? UserStatus.ACTIVE : UserStatus.DISABLED);
        return entity;
    }
}
