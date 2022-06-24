package org.example.test.springboot.app.services;

import org.example.test.springboot.app.models.Account;

import java.math.BigDecimal;
import java.util.List;

public interface AccountService {

    List<Account> findAll();
    Account findById(Long id);
    Account save(Account account);
    void deleteById(Long id);
    int getTotalTransfers(Long bankId);
    BigDecimal getAmount (Long accountId);
    void transfer(Long originAccount, Long destinationAccount, BigDecimal amount, Long bankId);
}
