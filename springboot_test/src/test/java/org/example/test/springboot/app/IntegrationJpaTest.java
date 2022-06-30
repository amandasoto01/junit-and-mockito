package org.example.test.springboot.app;

import org.example.test.springboot.app.models.Account;
import org.example.test.springboot.app.repositories.AccountRepository;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Tag("integration_jpa")
@DataJpaTest
public class IntegrationJpaTest {

    @Autowired
    AccountRepository accountRepository;

    @Test
    void findById() {
        Optional<Account> account = accountRepository.findById(1L);

        assertTrue(account.isPresent());
        assertEquals("Andres", account.orElseThrow().getPerson());
    }

    @Test
    void findByPerson() {
        Optional<Account> account = accountRepository.findByPerson("Andres");

        assertTrue(account.isPresent());
        assertEquals("Andres", account.orElseThrow().getPerson());
        assertEquals("1000.00", account.orElseThrow().getAmount().toPlainString());
    }

    @Test
    void findByPersonThrowException() {
        Optional<Account> account = accountRepository.findByPerson("Rod");

        assertThrows(NoSuchElementException.class, account::orElseThrow);
        assertFalse(account.isPresent());
    }

    @Test
    void findAll() {
        List<Account> accounts = accountRepository.findAll();

        assertFalse(accounts.isEmpty());
        assertEquals(2, accounts.size());
    }

    @Test
    void testSave() {
        // Given
        Account accountPepe = new Account(null,"Pepe", new BigDecimal("3000"));

        // When
        Account account = accountRepository.save(accountPepe);
        //Account account = accountRepository.findByPerson("Pepe").orElseThrow();
        //Account account = accountRepository.findById(save.getId()).orElseThrow();

        // Then
        assertEquals("Pepe", account.getPerson());
        assertEquals("3000", account.getAmount().toPlainString());
        //assertEquals(3, account.getId());
    }

    @Test
    void testUpdate() {
        // Given
        Account accountPepe = new Account(null,"Pepe", new BigDecimal("3000"));

        // When
        Account account = accountRepository.save(accountPepe);
        //Account account = accountRepository.findByPerson("Pepe").orElseThrow();
        //Account account = accountRepository.findById(save.getId()).orElseThrow();

        // Then
        assertEquals("Pepe", account.getPerson());
        assertEquals("3000", account.getAmount().toPlainString());
        //assertEquals(3, account.getId());

        // When
        account.setAmount(new BigDecimal("3800"));
        Account accountUpdated = accountRepository.save(account);

        // Then
        assertEquals("Pepe", accountUpdated.getPerson());
        assertEquals("3800", accountUpdated.getAmount().toPlainString());
    }

    @Test
    void testDelete() {
        Account account = accountRepository.findById(2L).orElseThrow();
        assertEquals("Jhon", account.getPerson());

        accountRepository.delete(account);

        assertThrows(NoSuchElementException.class, () -> {
            //accountRepository.findByPerson("Jhon").orElseThrow();
            accountRepository.findById(2L).orElseThrow();
        });

        assertEquals(1, accountRepository.findAll().size());
    }
}
