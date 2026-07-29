package com.example.Transaction_Service.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.example.Transaction_Service.entity.TransactionStatus;
import com.example.Transaction_Service.repository.TransactionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionStatusService {

    private final TransactionRepository transactionRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markTransactionFailed(UUID transactionId) {

        transactionRepository.findById(transactionId).ifPresent(transaction -> {
            transaction.setStatus(TransactionStatus.FAILED);
            transactionRepository.save(transaction);
        });
    }
}