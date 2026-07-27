package com.example.Account_Service.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Account_Service.Enum.AccountType;
import com.example.Account_Service.entity.Account;

public interface AccountRepository extends JpaRepository<Account, UUID> {
    Optional<Account> findFirstByAccountType(AccountType accountType);
     List<Account> findByAccountTypeAndStatus(AccountType accountType, String status);
}
