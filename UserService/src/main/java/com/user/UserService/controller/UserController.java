package com.user.UserService.controller;

import com.user.UserService.dto.request.UserServiceRequest;
import com.user.UserService.dto.response.UserServiceResponse;
import com.user.UserService.service.UserService;
import jakarta.validation.Valid;
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

    @GetMapping("/fetchUser/{userId}")
    public ResponseEntity<UserServiceResponse> getUserById(@PathVariable Long userId)
    {
        UserServiceResponse userResponse = userService.getUser(userId);
        return ResponseEntity.ok(userResponse);
    }
    @GetMapping("/usersList")
    public ResponseEntity<List<UserServiceResponse>> getListOfAllUsers()
    {
        List<UserServiceResponse> listUser = userService.listOfUsers();
        return ResponseEntity.ok(listUser);
    }
    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<UserServiceResponse> deleteUserByUserId(@PathVariable Long userId)
    {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/updateUser/{userId}")
    public ResponseEntity<UserServiceResponse> updateUser(@PathVariable Long userId,
                                                          @Valid @RequestBody UserServiceRequest userRequest)
    {
        UserServiceResponse userResponse = userService.updateUser(userId, userRequest);
        return ResponseEntity.ok(userResponse);
    }
}
