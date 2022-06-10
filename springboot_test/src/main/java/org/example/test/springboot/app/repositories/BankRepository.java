package org.example.test.springboot.app.repositories;

import org.example.test.springboot.app.models.Bank;

import java.util.List;

public interface BankRepository {

    List<Bank> findAll();
    Bank findById(Long id);
    void update(Bank bank);
}
