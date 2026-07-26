package com.example.Account_Service.schedular;

import com.example.Account_Service.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccountStatusScheduler {

    private final AccountService accountService;


    @Scheduled(cron = "0 0 0 * * *")
    //@Scheduled(fixedRate = 60000)
    public void toInactive() {
        log.info("Account status update job started");
        accountService.updateInactiveAccounts();
        log.info("Account status update job completed");

    }

}
