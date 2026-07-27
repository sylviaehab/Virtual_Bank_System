package com.example.Account_Service.controller;

import com.example.Account_Service.client.dto.TransferRequest;
import com.example.Account_Service.dto.AccountCreateResponse;
import com.example.Account_Service.dto.AccountRequest;
import com.example.Account_Service.dto.RetrieveResponse;
import com.example.Account_Service.dto.TransferResponse;
import com.example.Account_Service.enums.AccountType;
import com.example.Account_Service.enums.StatusType;
import com.example.Account_Service.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/accounts")
@Tag(name = "Account Controller", description = "APIs for managing bank accounts")
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @Operation(
            summary = "Create Account",
            description = "Creates a new account for an existing user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Account created successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PostMapping
    public ResponseEntity<AccountCreateResponse> createAccount(@RequestBody @Valid AccountRequest accountRequest) {
        return new ResponseEntity<>(accountService.addAccount(accountRequest), HttpStatus.CREATED);

    }

    @PutMapping("/transfer")
    public ResponseEntity<TransferResponse> updateBalance(@RequestBody @Valid TransferRequest transferRequest) {
        return new ResponseEntity<>(accountService.updateBalance(transferRequest), HttpStatus.OK);
    }

    @GetMapping("{accountId}")
    public ResponseEntity<RetrieveResponse> getAccount(@PathVariable UUID accountId) {
        return new ResponseEntity<>(accountService.getAccount(accountId), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<RetrieveResponse>> listAccounts(
            @RequestParam(required = false) AccountType accountType,
            @RequestParam(required = false) StatusType status) {
        return ResponseEntity.ok(accountService.listAccounts(accountType, status));
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<List<RetrieveResponse>> getUserAccounts(@PathVariable UUID userId) {
        return new ResponseEntity<>(accountService.getUserAccounts(userId), HttpStatus.OK);
    }

}
