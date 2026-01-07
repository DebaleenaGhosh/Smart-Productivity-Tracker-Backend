package com.user.UserService.service;

import com.user.UserService.dto.UserServiceRequest;
import com.user.UserService.dto.UserServiceResponse;

import java.util.List;

public interface UserService
{
    UserServiceResponse getUser(Long userId);
    UserServiceResponse deleteUser(Long userId);
    List<UserServiceResponse> listOfUsers();
    UserServiceResponse updateUser(UserServiceRequest user);
    void taskCountUpdate(Long userId, String updateRequest);
}