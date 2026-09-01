package com.user.UserService.service;

import com.spt.events.UserEvent;
import com.spt.events.TaskEvent;
import com.user.UserService.event.UserEventPublisher;
import com.user.UserService.mapper.UserEntityMapper;
import com.user.UserService.dto.request.UserServiceRequest;
import com.user.UserService.dto.response.UserServiceResponse;
import com.user.UserService.entity.UserProfile;
import com.user.UserService.exception.UserNotFoundException;
import com.user.UserService.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
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
    public void createUserProfile( UserEvent event )
    {
        if( userRepo.existsById(event.getUserId())){
            log.info(
                    "User profile already exists. userId = {}", event.getUserId()
            );
            return;
        }
        UserProfile userProfile = new UserProfile();
        userProfile.setUserId(event.getUserId());
        userProfile.setUsername(event.getUsername());
        userProfile.setEmail(event.getEmail());
        userProfile.setRole(UserProfile.Role.valueOf(event.getRole()));
        userProfile.setTaskCount(1);

        userRepo.save(userProfile);

        log.info("New user registered: userId = {}", userProfile.getUserId());
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
        UserProfile userProfile = userRepo.findById(userId)
                .orElse(null);

        if (userProfile == null) {
            log.warn(
                    "Ignoring task count update because user no longer exists. userId={}",userId
            );
            return;
        }
        userRepo.delete(userProfile);
        /*Publishing the user event after successful deletion*/
        userEventPublisher.publishUserDeleted( userId );
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
        UserProfile userProfile = userRepo.findById(userId).orElse(null);

        if (userProfile == null) {
            log.warn(
                    "Ignoring task count update. User does not exist. userId={}",
                    userId
            );
            return;
        }

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