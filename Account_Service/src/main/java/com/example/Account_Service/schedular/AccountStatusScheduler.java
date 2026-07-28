package com.example.Account_Service.schedular;

import com.example.Account_Service.kafka.Producer.KafkaLogProducer;
import com.example.Account_Service.schedular.dto.SchedulerResponse;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.Account_Service.service.AccountService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccountStatusScheduler {

    private final AccountService accountService;
    private final KafkaLogProducer kafkaLogProducer;

    @Scheduled(cron = "0 0 0 * * *")
    //@Scheduled(fixedRate = 60000)
    public void toInactive() {
        log.info("Account status update job started");

        try {

            int updatedAccounts = accountService.updateInactiveAccounts();

            SchedulerResponse response =
                    new SchedulerResponse(updatedAccounts, Instant.now());

            kafkaLogProducer.sendResponse(response);

            log.info("Account status update job completed");

        } catch (Exception ex) {

            log.error("Scheduler failed", ex);

        }
        log.info("Account status update job completed");

    }

}
