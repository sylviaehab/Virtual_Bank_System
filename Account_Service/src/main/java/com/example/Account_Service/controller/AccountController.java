package com.example.Account_Service.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Account_Service.client.dto.TransferRequest;
import com.example.Account_Service.dto.AccountCreateResponse;
import com.example.Account_Service.dto.AccountRequest;
import com.example.Account_Service.dto.RetrieveResponse;
import com.example.Account_Service.dto.TransferResponse;
import com.example.Account_Service.service.AccountService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/accounts")
@Tag(name = "Account Controller", description = "APIs for managing bank accounts")
public class AccountController {
    private AccountService accountService;

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
    public ResponseEntity<RetrieveResponse> getAccount(@PathVariable UUID accoundId) {
        return new ResponseEntity<>(accountService.getAccount(accoundId), HttpStatus.OK);
    }
    @GetMapping
public ResponseEntity<List<RetrieveResponse>> listAccounts(
        @RequestParam(required = false) String accountType,
        @RequestParam(required = false) String status) {
    return ResponseEntity.ok(accountService.listAccounts(accountType, status));
}

}
