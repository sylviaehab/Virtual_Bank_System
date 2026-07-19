package com.example.Account_Service.mapping;

import com.example.Account_Service.dto.AccountCreateResponse;
import com.example.Account_Service.dto.AccountRequest;
import com.example.Account_Service.dto.RetrieveResponse;
import com.example.Account_Service.entity.Account;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AccountMapper {
    public Account toAccount(AccountRequest accountRequest) {
        Account account = new Account();

        account.setAccountType(accountRequest.accountType());
        account.setAccountNumber(UUID.randomUUID());
        account.setBalance(accountRequest.initialBalance());
        account.setStatus("ACTIVE");

        return account;
    }

    public AccountCreateResponse toCreateResponse(Account account) {


        return new AccountCreateResponse(account.getAccountId(),
                account.getAccountNumber(),
                "Account created successfully.");
    }

    public RetrieveResponse toRetrieveResponse(Account account) {
        return new RetrieveResponse(account.getAccountId(),
                account.getAccountNumber(),
                account.getAccountType(),
                account.getBalance(),
                account.getStatus());
    }


}
