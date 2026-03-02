package com.post_hub.iam_service.mapper;

import com.post_hub.iam_service.model.dto.role.RoleDTO;
import com.post_hub.iam_service.model.dto.user.UserDTO;
import com.post_hub.iam_service.model.dto.user.UserProfileDTO;
import com.post_hub.iam_service.model.dto.user.UserSearchDTO;
import com.post_hub.iam_service.model.entity.Role;
import com.post_hub.iam_service.model.entity.User;
import com.post_hub.iam_service.model.enums.RegistrationStatus;
import com.post_hub.iam_service.model.request.user.NewUserRequest;
import com.post_hub.iam_service.model.request.user.UpdateUserRequest;
import org.hibernate.type.descriptor.DateTimeUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        imports = {RegistrationStatus.class, Objects.class, DateTimeUtils.class}
)
public interface UserMapper {


    @Mapping(target = "roles", expression = "java(mapRoles(user.getRoles()))")
    UserDTO toDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "registrationStatus", expression = "java(RegistrationStatus.ACTIVE)")
    User createUser(NewUserRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    void updateUser(@MappingTarget User user, UpdateUserRequest request);

    @Mapping(source = "deleted", target = "isDeleted")
    @Mapping(target = "roles", expression = "java(mapRoles(user.getRoles()))")
    UserSearchDTO toUserSearchDto(User user);

    @Mapping(target = "roles", expression = "java(mapRoles(user.getRoles()))")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "token", source = "token")
    UserProfileDTO toUserProfileDto(User user, String token);

    default List<RoleDTO> mapRoles(Collection<Role> roles) {
        return roles.stream()
                .map(role -> new RoleDTO(role.getId(), role.getName()))
                .toList();
    }

}
