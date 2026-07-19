package com.example.Account_Service.client;

import com.example.Account_Service.dto.TransferRequest;
import com.example.Account_Service.dto.TransferResponse;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "Transaction-Service")
public interface TransactionClient {
    @PostMapping("transactions/transfer/initiation")
    public TransferResponse transfer(@RequestBody @Valid TransferRequest transferRequest);
}
