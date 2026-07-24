package com.example.Account_Service.repository;

import com.example.Account_Service.entity.Account;
import com.example.Account_Service.enums.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {
    Optional<Account> findFirstByAccountType(AccountType accountType);

    List<Account> findByAccountTypeAndStatus(AccountType accountType, String status);
}
