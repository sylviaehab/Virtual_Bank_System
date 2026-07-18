package com.example.Account_Service.controller;

import com.example.Account_Service.dto.RetrieveResponse;
import com.example.Account_Service.service.AccountService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@Tag(name = "User_Account Controller", description = "APIs for managing User accounts")
public class UserAccountController {
    private final AccountService accountService;

    public UserAccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/{userId}/accounts")
    public ResponseEntity<List<RetrieveResponse>> getAllAccounts(@PathVariable UUID userId) {
        return new ResponseEntity<>(accountService.getAllAccounts(userId), HttpStatus.OK);
    }
}
