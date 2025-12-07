package com.user.UserService.controller;

import com.user.UserService.dto.UserServiceRequest;
import com.user.UserService.dto.UserServiceResponse;
import com.user.UserService.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController
{
    @Autowired
    private UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<UserServiceResponse> getUserById(@PathVariable Long userId)
    {
        UserServiceResponse userResponse = userService.getUser(userId);
        return new ResponseEntity<>(userResponse, userResponse.getHttpStatus());
    }
    @GetMapping("/usersList")
    public ResponseEntity<List<UserServiceResponse>> getListOfAllUsers()
    {
        List<UserServiceResponse> listUser = userService.listOfUsers();
        return new ResponseEntity<>(listUser, HttpStatus.OK);
    }
    @DeleteMapping("/{userId}")
    public ResponseEntity<UserServiceResponse> deleteUserByUserId(@PathVariable Long userId)
    {
        UserServiceResponse userResponse = userService.deleteUser(userId);
        return new ResponseEntity<>(userResponse.getHttpStatus());
    }
    @PutMapping("/updateUser")
    public ResponseEntity<UserServiceResponse> updateUser(@RequestBody UserServiceRequest userRequest)
    {
        UserServiceResponse userResponse = userService.updateUser(userRequest);
        return new ResponseEntity<>(userResponse, userResponse.getHttpStatus());
    }
}
