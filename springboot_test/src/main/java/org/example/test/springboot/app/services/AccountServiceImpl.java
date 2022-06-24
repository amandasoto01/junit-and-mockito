package org.example.test.springboot.app.services;

import org.example.test.springboot.app.models.Account;
import org.example.test.springboot.app.models.Bank;
import org.example.test.springboot.app.repositories.AccountRepository;
import org.example.test.springboot.app.repositories.BankRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AccountServiceImpl implements  AccountService {

    private AccountRepository accountRepository;
    private BankRepository bankRepository;

    public AccountServiceImpl(AccountRepository accountRepository, BankRepository bankRepository) {
        this.accountRepository = accountRepository;
        this.bankRepository = bankRepository;
    }

    @Override
    @Transactional (readOnly = true)
    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    @Override
    @Transactional (readOnly = true)
    public Account findById(Long id) {
        return accountRepository.findById(id).orElseThrow();
    }

    @Override
    @Transactional
    public Account save(Account account) {
        return accountRepository.save(account);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        accountRepository.deleteById(id);
    }

    @Override
    @Transactional (readOnly = true)
    public int getTotalTransfers(Long bankId) {
        Bank bank = bankRepository.findById(bankId).orElseThrow();
        return bank.getTotalTransfers();
    }

    @Override
    @Transactional (readOnly = true)
    public BigDecimal getAmount(Long accountId) {
        Account account = accountRepository.findById(accountId).orElseThrow();
        return account.getAmount();
    }

    @Override
    @Transactional
    public void transfer(Long originAccountId, Long destinationAccountId, BigDecimal amount,
                         Long bankId) {
        Account originAccount = accountRepository.findById(originAccountId).orElseThrow();
        originAccount.debit(amount);
        accountRepository.save(originAccount);

        Account destinationAccount = accountRepository.findById(destinationAccountId).orElseThrow();
        destinationAccount.credit(amount);
        accountRepository.save(destinationAccount);

        Bank bank = bankRepository.findById(bankId).orElseThrow();
        int totalTransfers = bank.getTotalTransfers();
        bank.setTotalTransfers(++totalTransfers);
        bankRepository.save(bank);
    }
}
