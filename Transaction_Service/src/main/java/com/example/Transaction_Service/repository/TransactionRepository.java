package com.example.Transaction_Service.repository;

import com.example.Transaction_Service.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    /**
     * Retrieves every transaction where the given account is either the sender
     * or the receiver, most recent first.
     */
    List<Transaction> findByFromAccountIdOrToAccountIdOrderByCreatedAtDesc(
            UUID fromAccountId, UUID toAccountId);
}
