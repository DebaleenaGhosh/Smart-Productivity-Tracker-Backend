package com.user.UserService.mapper;

import com.user.UserService.dto.UserDto;
import com.user.UserService.dto.response.UserServiceResponse;
import com.user.UserService.entity.UserProfile;
import org.springframework.stereotype.Component;

@Component
public class UserEntityMapper {

    public UserProfile convertDtoToEntity(UserDto userDto)
    {
        UserProfile userProfile = new UserProfile();
        userProfile.setUserId(userDto.getId());
        userProfile.setUsername(userDto.getUsername());
        userProfile.setEmail(userDto.getEmail());
        userProfile.setRole(UserProfile.Role.valueOf(userDto.getRole()));
        userProfile.setTaskCount(userDto.getTaskCount());
        return userProfile;
    }

    public UserServiceResponse toResponse(UserProfile user)
    {
        return UserServiceResponse.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(String.valueOf(user.getRole()))
                .build();
    }
}