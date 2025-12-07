package com.user.UserService.service;

import com.user.UserService.dto.UserServiceRequest;
import com.user.UserService.dto.UserServiceResponse;

import java.util.List;

public interface UserService
{
    UserServiceResponse getUser(Long userId);
//    UserServiceResponse createUser(UserServiceRequest user);
    UserServiceResponse deleteUser(Long userId);
    List<UserServiceResponse> listOfUsers();
    UserServiceResponse updateUser(UserServiceRequest user);
    void incrementTaskCount(Long userId);
}