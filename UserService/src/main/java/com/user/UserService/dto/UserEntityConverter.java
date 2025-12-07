package com.user.UserService.dto;

import com.user.UserService.entity.UserProfile;
import org.springframework.stereotype.Component;

@Component
public class UserEntityConverter {

    public UserDto convertEntityToDto(UserProfile userProfile)
    {
        UserDto userDto = new UserDto();
        userDto.setId(userProfile.getUserId());
        userDto.setUsername(userProfile.getUsername());
        userDto.setEmail(userProfile.getEmail());
        userDto.setRole(String.valueOf(userProfile.getRole()));
        userDto.setPassword(userProfile.getPassword());
        return userDto;
    }

    public UserProfile convertDtoToEntity(UserDto userDto)
    {
        UserProfile userProfile = new UserProfile();
        userProfile.setUserId(userDto.getId());
        userProfile.setUsername(userDto.getUsername());
        userProfile.setEmail(userDto.getEmail());
        userProfile.setRole(UserProfile.Role.valueOf(userDto.getRole()));
        userProfile.setPassword(userDto.getPassword());
        return userProfile;
    }

}