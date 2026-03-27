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
        UserEntity userEntity = new UserEntity();

        userEntity.setId(user.getId());
        userEntity.setVersion(user.getVersion());
        userEntity.setName(user.getName());
        userEntity.setEmail(user.getEmail());
        userEntity.setPassword(user.getPassword());
        userEntity.setUserStatus(user.getUserStatus());
        userEntity.setRole(user.getRole());
        userEntity.setEnabled(user.isEnabled());
        userEntity.setVerified(user.isVerified());
        userEntity.setCreatedAt(user.getCreatedAt());
        userEntity.setLastUpdate(user.getLastUpdate());

        // Mapping the domain 'lastLoginAt' to entity 'lastLoginInAt'
        userEntity.setLastLoginInAt(user.getLastLoginAt());

        return userEntity;
    }
}