package com.user.UserService.service;

import com.user.UserService.dto.UserDto;
import com.user.UserService.mapper.UserEntityMapper;
import com.user.UserService.dto.request.UserServiceRequest;
import com.user.UserService.dto.response.UserServiceResponse;
import com.user.UserService.entity.UserProfile;
import com.user.UserService.exception.UserNotFoundException;
import com.user.UserService.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService
{
    private final UserRepository userRepo;
    private final UserEntityMapper mapper;
    private final UserEventPublisher userEventPublisher;

    public UserServiceImpl(UserRepository userRepo, UserEntityMapper mapper, UserEventPublisher userEventPublisher) {
        this.userRepo = userRepo;
        this.mapper = mapper;
        this.userEventPublisher = userEventPublisher;
    }

    @Override
    public UserServiceResponse getUser(Long userId)
    {
        UserServiceResponse userServiceResponse = new UserServiceResponse();

        UserProfile userProfileEntity = userRepo.findById(userId).orElseThrow(()
                -> new UserNotFoundException("User not found with id: " + userId));
        userServiceResponse = mapper.toResponse(userProfileEntity);
        log.info(
                "Fetching user profile. userId={}",userId
        );
        return userServiceResponse;
    }

    @Override
    public void deleteUser(Long userId)
    {
        UserProfile userProfileEntity = userRepo.findById(userId).orElseThrow(()
                -> new UserNotFoundException("User not found with id: " + userId));
        userRepo.delete(userProfileEntity);
        /*Publishing the user event after successful deletion*/
        userEventPublisher.publishUserDeleted(userId);
        log.info("User deleted: userId = {}", userId);
    }

    @Override
    public UserServiceResponse updateUser(Long userId, UserServiceRequest request)
    {
        UserProfile user = userRepo.findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found with id: " + userId
                        )
                );
        if (request.getUserName() != null) {
            user.setUsername(request.getUserName());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if ( request.getRole() != null ) {
            user.setRole(UserProfile.Role.valueOf(request.getRole()));
        }
        UserProfile updatedUser = userRepo.save(user);

        log.info(
                "User profile updated successfully. userId={}",
                userId
        );

        return mapper.toResponse(updatedUser);
    }

    @Override
    public List<UserServiceResponse> listOfUsers()
    {
        List<UserProfile> listOfUserProfiles = userRepo.findAll();
        log.info("List of all users fetched");
        return listOfUserProfiles.stream()
                .map(userProfile -> new UserServiceResponse(
                        userProfile.getUserId(),
                        userProfile.getUsername(),
                        userProfile.getEmail(),
                        String.valueOf(userProfile.getRole()),
                        userProfile.getTaskCount()
                ))
                .toList();
    }

    @Override
    public void taskCountUpdate(Long userId, String updateRequest)
    {
        UserProfile userProfile = userRepo.findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found: userId = {}, userId")
                        );

        int taskCount = userProfile.getTaskCount();
        if(updateRequest.contains("Increment"))
            userProfile.setTaskCount(taskCount + 1);
        else if(updateRequest.contains("Decrement"))
            userProfile.setTaskCount(taskCount - 1);
        else if(updateRequest.contains("Reset"))
            userProfile.setTaskCount(0);

        userRepo.save(userProfile);
    }
}