package org.example.test.springboot.app.services;

import org.example.test.springboot.app.models.Account;
import org.example.test.springboot.app.models.Bank;
import org.example.test.springboot.app.repositories.AccountRepository;
import org.example.test.springboot.app.repositories.BankRepository;

import java.math.BigDecimal;

public class AccountServiceImpl implements  AccountService {

    private AccountRepository accountRepository;
    private BankRepository bankRepository;

    public AccountServiceImpl(AccountRepository accountRepository, BankRepository bankRepository) {
        this.accountRepository = accountRepository;
        this.bankRepository = bankRepository;
    }

    @Override
    public Account findById(Long id) {
        return accountRepository.findById(id);
    }

    @Override
    public int getTotalTransfers(Long bankId) {
        Bank bank = bankRepository.findById(bankId);
        return bank.getTotalTransfers();
    }

    @Override
    public BigDecimal getAmount(Long accountId) {
        Account account = accountRepository.findById(accountId);
        return account.getAmount();
    }

    @Override
    public void transfer(Long originAccountId, Long destinationAccountId, BigDecimal amount,
                         Long bankId) {
        Bank bank = bankRepository.findById(bankId);
        int totalTransfers = bank.getTotalTransfers();
        bank.setTotalTransfer(++totalTransfers);
        bankRepository.update(bank);

        Account originAccount = accountRepository.findById(originAccountId);
        originAccount.debit(amount);
        accountRepository.update(originAccount);

        Account destinationAccount = accountRepository.findById(destinationAccountId);
        destinationAccount.credit(amount);
        accountRepository.update(destinationAccount);
    }
}
