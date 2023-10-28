package com.challenglish.worker.mapper;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;

import com.challenglish.worker.dto.UserDTO;
import com.challenglish.worker.dto.UserProfileDTO;
import com.challenglish.worker.entity.User;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true), nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface UserMapper {

    UserDTO entityToDto(User user);
    User updateProfile(@MappingTarget User userFromDb, UserProfileDTO userProfileDTO);
}
