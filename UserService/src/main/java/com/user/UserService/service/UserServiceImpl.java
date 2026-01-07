package com.user.UserService.service;

import com.user.UserService.dto.UserDto;
import com.user.UserService.dto.UserEntityConverter;
import com.user.UserService.dto.UserServiceRequest;
import com.user.UserService.dto.UserServiceResponse;
import com.user.UserService.entity.UserProfile;
import com.user.UserService.exception.UserNotFoundException;
import com.user.UserService.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService
{
    @Autowired
    UserRepository userRepo;
    @Autowired
    UserEntityConverter converter;
    @Autowired
    UserEventPublisher userEventPublisher;

    @Override
    public UserServiceResponse getUser(Long userId)
    {
        UserServiceResponse userServiceResponse = new UserServiceResponse();
        try
        {
            UserProfile userProfileEntity = userRepo.findById(userId).orElseThrow(()
                    -> new UserNotFoundException("User not found!"));
            UserDto userDto = converter.convertEntityToDto(userProfileEntity);
            userServiceResponse.setUsername(userDto.getUsername());
            userServiceResponse.setRole(userDto.getRole());
            userServiceResponse.setEmail(userDto.getEmail());
            userServiceResponse.setTaskCount(userDto.getTaskCount());
            userServiceResponse.setHttpStatus(HttpStatus.OK);
            userServiceResponse.setHttpMessage("User details retrieved successfully");
        }
        catch (UserNotFoundException userNotFoundException)
        {
            userServiceResponse.setHttpStatus(HttpStatus.NOT_FOUND);
            userServiceResponse.setHttpMessage(userNotFoundException.getMessage());
        }
        return userServiceResponse;
    }

    @Override
    public UserServiceResponse deleteUser(Long userId)
    {
        UserServiceResponse userServiceResponse = new UserServiceResponse();
        try
        {
            UserProfile userProfileEntity = userRepo.findById(userId).orElseThrow(()
                    -> new UserNotFoundException("UserProfile was not found"));
            userRepo.delete(userProfileEntity);
            /*Publishing the user event after successful deletion*/
            userEventPublisher.publishUserDeleted(userId);

            userServiceResponse.setHttpStatus(HttpStatus.OK);
            userServiceResponse.setHttpMessage("User deleted successfully");
        }
        catch (UserNotFoundException userNotFoundException)
        {
            userServiceResponse.setHttpStatus(HttpStatus.NOT_FOUND);
            userServiceResponse.setHttpMessage(userNotFoundException.getMessage());
        }
        return userServiceResponse;
    }

    @Override
    public UserServiceResponse updateUser(UserServiceRequest request)
    {
        UserServiceResponse userServiceResponse = new UserServiceResponse();
        try
        {
            UserProfile existingUserProfile = userRepo.findById(request.getUserId()).orElseThrow(()
                    -> new RuntimeException("User not found!"));
            existingUserProfile.setUsername(request.getUserName());
            existingUserProfile.setEmail(request.getEmail());
            existingUserProfile.setRole(UserProfile.Role.valueOf(request.getRole()));
            userRepo.save(existingUserProfile);
            /*Publishing the user event after successful update*/
            userEventPublisher.publishUserUpdated(converter.convertEntityToDto(existingUserProfile));

            userServiceResponse.setUsername(existingUserProfile.getUsername());
            userServiceResponse.setEmail(existingUserProfile.getEmail());
            userServiceResponse.setRole(String.valueOf(existingUserProfile.getRole()));
            userServiceResponse.setHttpStatus(HttpStatus.OK);
            userServiceResponse.setHttpMessage("User updated successfully");
        }
        catch (UserNotFoundException userNotFoundException)
        {
            userServiceResponse.setHttpStatus(HttpStatus.NOT_FOUND);
            userServiceResponse.setHttpMessage(userNotFoundException.getMessage());
        }
        return userServiceResponse;
    }

    @Override
    public List<UserServiceResponse> listOfUsers()
    {
        List<UserProfile> listOfUserProfiles = userRepo.findAll();
        return listOfUserProfiles.stream()
                .map(userProfile -> new UserServiceResponse(
                        userProfile.getUsername(),
                        userProfile.getEmail(),
                        String.valueOf(userProfile.getRole()),
                        userProfile.getTaskCount(),
                        HttpStatus.FOUND,
                        "List of all users fetched"
                ))
                .toList();
    }

    @Override
    public void taskCountUpdate(Long userId, String updateRequest)
    {
        UserServiceResponse userServiceResponse = new UserServiceResponse();
        try {
            UserProfile userProfile = userRepo.findById(userId).orElseThrow(()
                    -> new UserNotFoundException("User not found!"));

            int taskCount = userProfile.getTaskCount();
            if(updateRequest.contains("Increment"))
                userProfile.setTaskCount(taskCount + 1);
            else if(updateRequest.contains("Decrement"))
                userProfile.setTaskCount(taskCount - 1);
            else if(updateRequest.contains("Reset"))
                userProfile.setTaskCount(0);

            userRepo.save(userProfile);
            userServiceResponse.setHttpStatus(HttpStatus.OK);
            userServiceResponse.setHttpMessage("Task count was incremented");
        }
        catch (UserNotFoundException userNotFoundException)
        {
            userServiceResponse.setHttpStatus(HttpStatus.NOT_FOUND);
            userServiceResponse.setHttpMessage(userNotFoundException.getMessage());
        }
    }
}