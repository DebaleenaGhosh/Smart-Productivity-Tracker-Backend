package com.auth.AuthServer.dto;

import com.auth.AuthServer.entity.AuthUser;
import org.springframework.stereotype.Component;

@Component
public class UserEntityConverter {

    public AuthUserDto convertEntityToDto(AuthUser authUser)
    {
        AuthUserDto authUserDto = new AuthUserDto();
        authUserDto.setUserId(authUser.getUserId());
        authUserDto.setUserName(authUser.getUsername());
        authUserDto.setEmail(authUser.getEmail());
        authUserDto.setRole(authUser.getRole());
        authUserDto.setPassword(authUser.getPassword());
        //authUserDto.setListOfTasks(userProfile.getListOfTasks());
        return authUserDto;
    }

    public AuthUser convertDtoToEntity(AuthUserDto authUserDto)
    {
        AuthUser authUser = new AuthUser();
        authUser.setUserId(authUserDto.getUserId());
        authUser.setUserName(authUserDto.getUserName());
        authUser.setEmail(authUserDto.getEmail());
        authUser.setRole(authUserDto.getRole());
        authUser.setPassword(authUserDto.getPassword());
        //userProfile.setListOfTasks(userProfile.getListOfTasks());
        return authUser;
    }

}
