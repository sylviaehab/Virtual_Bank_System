package com.example.Account_Service.client;

import com.example.Account_Service.client.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "User-Service")
public interface UserClient {
    @GetMapping("users/{userId}")
    public UserResponse getUser(@PathVariable UUID userid);


}
