package org.example.test.springboot.app;

import org.example.test.springboot.app.exceptions.NotEnoughMoneyException;
import org.example.test.springboot.app.models.Account;
import org.example.test.springboot.app.models.Bank;
import org.example.test.springboot.app.repositories.AccountRepository;
import org.example.test.springboot.app.repositories.BankRepository;
import org.example.test.springboot.app.services.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.example.test.springboot.app.Data.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class SpringbootTestApplicationTests {

	@MockBean
	AccountRepository accountRepository;
	@MockBean
	BankRepository bankRepository;

	@Autowired
	AccountService service;

	@BeforeEach
	void setUp() {
		//accountRepository = mock(AccountRepository.class);
		//bankRepository = mock(BankRepository.class);
		//service = new AccountServiceImpl(accountRepository, bankRepository);

		//Data.ACCOUNT_001.setAmount(new BigDecimal("1000"));
		//Data.ACCOUNT_002.setAmount(new BigDecimal("2000"));
		//Data.BANK.setTotalTransfer(0);
	}
	@Test
	void contextLoads() {
		when(accountRepository.findById(1L)).thenReturn(createAccount001());
		when(accountRepository.findById(2L)).thenReturn(createAccount002());
		when(bankRepository.findById(1L)).thenReturn(createBank());

		BigDecimal originAmount = service.getAmount(1L);
		BigDecimal destinationAmount = service.getAmount(2L);

		assertEquals("1000", originAmount.toPlainString());
		assertEquals("2000", destinationAmount.toPlainString());

		service.transfer(1L, 2L, new BigDecimal("100"), 1L);
		originAmount = service.getAmount(1L);
		destinationAmount = service.getAmount(2L);

		assertEquals("900", originAmount.toPlainString());
		assertEquals("2100", destinationAmount.toPlainString());

		int total = service.getTotalTransfers(1L);
		assertEquals(1, total);

		verify(accountRepository, times(3)).findById(1L);
		verify(accountRepository, times(3)).findById(2L);
		verify(accountRepository, times(2)).save(any(Account.class));

		verify(bankRepository, times(2)).findById(1L);
		verify(bankRepository).save(any(Bank.class));

		verify(accountRepository, times(6)).findById(anyLong());
		verify(accountRepository, never()).findAll();
	}

	@Test
	void contextLoads2() {
		when(accountRepository.findById(1L)).thenReturn(createAccount001());
		when(accountRepository.findById(2L)).thenReturn(createAccount002());
		when(bankRepository.findById(1L)).thenReturn(createBank());

		BigDecimal originAmount = service.getAmount(1L);
		BigDecimal destinationAmount = service.getAmount(2L);

		assertEquals("1000", originAmount.toPlainString());
		assertEquals("2000", destinationAmount.toPlainString());

		assertThrows(NotEnoughMoneyException.class, () -> {
			service.transfer(1L, 2L, new BigDecimal("1200"), 1L);
		});

		originAmount = service.getAmount(1L);
		destinationAmount = service.getAmount(2L);

		assertEquals("1000", originAmount.toPlainString());
		assertEquals("2000", destinationAmount.toPlainString());

		int total = service.getTotalTransfers(1L);
		assertEquals(0, total);

		verify(accountRepository, times(3)).findById(1L);
		verify(accountRepository, times(2)).findById(2L);
		verify(accountRepository, never()).save(any(Account.class));

		verify(bankRepository, times(1)).findById(1L);
		verify(bankRepository, never()).save(any(Bank.class));

		verify(accountRepository, times(5)).findById(anyLong());
	}

	@Test
	void contextLoads3() {
		when(accountRepository.findById(1L)).thenReturn(createAccount001());

		Account account1 = service.findById(1L);
		Account account2 = service.findById(1L);

		assertSame(account1, account2);
		assertTrue(account1 == account2);
		assertEquals("Andres", account1.getPerson());
		assertEquals("Andres", account2.getPerson());

		verify(accountRepository, times(2)).findById(1L);
	}

	@Test
	void testFindAll() {
		// Given
		List<Account> data = Arrays.asList(createAccount001().orElseThrow(), createAccount002().orElseThrow());
		when(accountRepository.findAll()).thenReturn(data);

		//When
		List<Account> accounts = service.findAll();

		// Then
		assertFalse(accounts.isEmpty());
		assertEquals(2, accounts.size());
		assertTrue(accounts.contains(createAccount002().orElseThrow()));

		verify(accountRepository).findAll();
	}

	@Test
	void testSave() {
		// Given
		Account pepeAccount = new Account(null, "Pepe", new BigDecimal("3000"));
		when(accountRepository.save(any())).then(invocation -> {
			Account ac = invocation.getArgument(0);
			ac.setId(3L);
			return ac;
		});

		// When
		Account account = service.save(pepeAccount);

		// Then
		assertEquals("Pepe", account.getPerson());
		assertEquals(3, account.getId());
		assertEquals("3000", account.getAmount().toPlainString());

		verify(accountRepository).save(any());
	}
}
