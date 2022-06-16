package org.example.test.springboot.app;

import org.example.test.springboot.app.exceptions.NotEnoughMoneyException;
import org.example.test.springboot.app.models.Account;
import org.example.test.springboot.app.models.Bank;
import org.example.test.springboot.app.repositories.AccountRepository;
import org.example.test.springboot.app.repositories.BankRepository;
import org.example.test.springboot.app.services.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.example.test.springboot.app.Data.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;

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
}
