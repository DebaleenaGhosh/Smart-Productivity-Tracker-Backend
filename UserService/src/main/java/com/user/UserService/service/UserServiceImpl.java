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
            UserProfile existingUserProfile = userRepo.findById(request.getId()).orElseThrow(()
                    -> new RuntimeException("User not found!"));
            existingUserProfile.setEmail(request.getEmail());
            existingUserProfile.setPassword(request.getPassword());
            existingUserProfile.setRole(UserProfile.Role.valueOf(request.getRole()));
            userRepo.save(existingUserProfile);
            /*Publishing the user event after successful update*/
            userEventPublisher.publishUserUpdated(converter.convertEntityToDto(existingUserProfile));

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
                        HttpStatus.FOUND,
                        "List of all users fetched"
                ))
                .toList();
    }

    @Override
    public void incrementTaskCount(Long userId)
    {
        UserServiceResponse userServiceResponse = new UserServiceResponse();
        try {
            UserProfile userProfile = userRepo.findById(userId).orElseThrow(()
                    -> new UserNotFoundException("User not found!"));
            UserDto userDto = converter.convertEntityToDto(userProfile);
            Long taskCount = userDto.getTaskCount();
            userDto.setTaskCount(taskCount + 1);
            userRepo.save(converter.convertDtoToEntity(userDto));

            userServiceResponse.setHttpStatus(HttpStatus.OK);
            userServiceResponse.setHttpMessage("Task increment happened");
        }
        catch (UserNotFoundException userNotFoundException)
        {
            userServiceResponse.setHttpStatus(HttpStatus.NOT_FOUND);
            userServiceResponse.setHttpMessage(userNotFoundException.getMessage());
        }
    }

//    @Override
//    public UserServiceResponse createUser(UserServiceRequest request)
//    {
//        UserDto userDto = new UserDto();
//        userDto.setPassword(request.getPassword());
//        userDto.setUsername(request.getUsername());
//        userDto.setEmail(request.getEmail());
//        userDto.setRole(request.getRole());
//        userDto.setTaskCount(0L);
//        UserProfile saved = userRepo.save(converter.convertDtoToEntity(userDto));
//
////        /*Publishing the user event after successful creation*/
////        userEventPublisher.publishUserCreated(converter.convertEntityToDto(saved));
//
//        return new UserServiceResponse(saved.getUsername(), saved.getEmail(), String.valueOf(saved.getRole()));
//    }

}