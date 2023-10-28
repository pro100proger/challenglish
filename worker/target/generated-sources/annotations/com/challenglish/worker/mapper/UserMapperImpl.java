package com.challenglish.worker.mapper;

import com.challenglish.worker.dto.UserDTO;
import com.challenglish.worker.dto.UserProfileDTO;
import com.challenglish.worker.entity.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-10-28T19:58:16+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.1 (Oracle Corporation)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserDTO entityToDto(User user) {
        if ( user == null ) {
            return null;
        }

        UserDTO userDTO = new UserDTO();

        if ( user.getId() != null ) {
            userDTO.setId( user.getId() );
        }
        if ( user.getFirstName() != null ) {
            userDTO.setFirstName( user.getFirstName() );
        }
        if ( user.getLastName() != null ) {
            userDTO.setLastName( user.getLastName() );
        }
        if ( user.getEmail() != null ) {
            userDTO.setEmail( user.getEmail() );
        }
        if ( user.getRole() != null ) {
            userDTO.setRole( user.getRole() );
        }
        if ( user.getPassword() != null ) {
            userDTO.setPassword( user.getPassword() );
        }
        if ( user.getIsActive() != null ) {
            userDTO.setIsActive( user.getIsActive() );
        }
        if ( user.getCreateDateTime() != null ) {
            userDTO.setCreateDateTime( user.getCreateDateTime() );
        }
        if ( user.getUpdateDateTime() != null ) {
            userDTO.setUpdateDateTime( user.getUpdateDateTime() );
        }

        return userDTO;
    }

    @Override
    public User updateProfile(User userFromDb, UserProfileDTO userProfileDTO) {
        if ( userProfileDTO == null ) {
            return userFromDb;
        }

        if ( userProfileDTO.getFirstName() != null ) {
            userFromDb.setFirstName( userProfileDTO.getFirstName() );
        }
        else {
            userFromDb.setFirstName( null );
        }
        if ( userProfileDTO.getLastName() != null ) {
            userFromDb.setLastName( userProfileDTO.getLastName() );
        }
        else {
            userFromDb.setLastName( null );
        }
        if ( userProfileDTO.getEmail() != null ) {
            userFromDb.setEmail( userProfileDTO.getEmail() );
        }
        else {
            userFromDb.setEmail( null );
        }
        if ( userProfileDTO.getUpdateDateTime() != null ) {
            userFromDb.setUpdateDateTime( userProfileDTO.getUpdateDateTime() );
        }
        else {
            userFromDb.setUpdateDateTime( null );
        }

        return userFromDb;
    }
}
