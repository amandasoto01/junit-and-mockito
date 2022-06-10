package org.example.test.springboot.app.services;

import org.example.test.springboot.app.models.Account;

import java.math.BigDecimal;

public interface AccountService {

    Account findById(Long id);
    int getTotalTransfers(Long bankId);
    BigDecimal getAmount (Long accountId);
    void transfer(Long originAccount, Long destinationAccount, BigDecimal amount, Long bankId);
}
