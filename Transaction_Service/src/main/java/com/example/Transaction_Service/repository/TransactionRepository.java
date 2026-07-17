package com.example.Transaction_Service.repository;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Transaction_Service.entity.Transaction;
import com.example.Transaction_Service.entity.TransactionStatus;


public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findByReferenceNumber(String referenceNumber);
      Page<Transaction> findBySourceAccountNumberOrDestinationAccountNumber(
            String sourceAccountNumber,
            String destinationAccountNumber,
            Pageable pageable
    );

    List<Transaction> findByStatus(TransactionStatus status);

    boolean existsByReferenceNumber(String referenceNumber);
}