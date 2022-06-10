package org.example.test.springboot.app.repositories;

import org.example.test.springboot.app.models.Account;

import java.util.List;

public interface AccountRepository {

    List<Account> findAll();
    Account findById(Long id);
    void update(Account account);

}
