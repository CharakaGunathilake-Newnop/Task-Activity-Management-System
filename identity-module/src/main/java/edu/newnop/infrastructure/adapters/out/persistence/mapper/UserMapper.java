package edu.newnop.infrastructure.adapters.out.persistence.mapper;

import edu.newnop.domain.model.User;
import edu.newnop.infrastructure.adapters.out.persistence.UserEntity;

public class UserMapper {

    // Convert JPA Entity -> Domain User
    public static User toDomain(UserEntity entity) {
        User user =  User.builder()
                .name(entity.getName())
                .email(entity.getEmail())
                .password(entity.getPassword())
                .userStatus(entity.getUserStatus())
                .role(entity.getRole())
                .isEnabled(entity.isEnabled())
                .isVerified(entity.isVerified())
                .lastLoginAt(entity.getLastLoginInAt())
                .build();

        user.setId(entity.getId());
        user.setVersion(entity.getVersion());
        user.setCreatedAt(entity.getCreatedAt());
        user.setLastUpdate(entity.getLastUpdate());

        return user;
    }

    // Convert Domain User -> JPA Entity
    public static UserEntity toEntity(User user) {
        return UserEntity.builder()
                .name(user.getName())
                .email(user.getEmail())
                .password(user.getPassword())
                .password(user.getPassword())
                .userStatus(user.getUserStatus())
                .role(user.getRole())
                .isEnabled(user.isEnabled())
                .isVerified(user.isVerified())
                .lastLoginInAt(user.getLastLoginAt())
                .build();
    }
}