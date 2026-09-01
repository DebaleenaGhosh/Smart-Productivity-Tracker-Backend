package com.user.UserService.service;

import com.user.UserService.dto.request.UserServiceRequest;
import com.user.UserService.dto.response.UserServiceResponse;
import com.spt.events.UserEvent;
import java.util.List;

public interface UserService
{
    void createUserProfile(UserEvent event);
    UserServiceResponse getUser(Long userId);
    void deleteUser(Long userId);
    List<UserServiceResponse> listOfUsers();
    UserServiceResponse updateUser(Long userId, UserServiceRequest user);
    void taskCountUpdate(Long userId, String updateRequest);
}