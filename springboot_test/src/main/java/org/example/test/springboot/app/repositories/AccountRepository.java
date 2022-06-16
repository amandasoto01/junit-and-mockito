package org.example.test.springboot.app.repositories;

import org.example.test.springboot.app.models.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    @Query("select a from Account a where a.person=?1")
    Optional<Account> findByPerson(String person);

    //List<Account> findAll();
    //Account findById(Long id);
    //void update(Account account);

}
