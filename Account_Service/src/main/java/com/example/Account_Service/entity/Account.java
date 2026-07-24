package com.example.Account_Service.entity;

import com.example.Account_Service.enums.AccountType;
import com.example.Account_Service.enums.StatusType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "account")
public class Account {
    @Id
    @GeneratedValue(
            strategy = GenerationType.UUID
    )
    @Column(name = "accountId")
    private UUID accountId;
    @Column(name = "accountNumber", nullable = false, unique = true)
    private UUID accountNumber;
    @Column(name = "accountType", nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountType accountType;
    @Column(name = "balance", nullable = false)
    private BigDecimal balance;
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusType status;
    @Column(name = "userId")
    private UUID userId;
}
