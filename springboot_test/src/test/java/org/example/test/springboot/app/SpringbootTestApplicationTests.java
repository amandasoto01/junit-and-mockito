package org.example.test.springboot.app;

import org.example.test.springboot.app.repositories.AccountRepository;
import org.example.test.springboot.app.repositories.BankRepository;
import org.example.test.springboot.app.services.AccountService;
import org.example.test.springboot.app.services.AccountServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

@SpringBootTest
class SpringbootTestApplicationTests {

	AccountRepository accountRepository;
	BankRepository bankRepository;

	AccountService service;


	@BeforeEach
	void setUp() {
		accountRepository = mock(AccountRepository.class);
		bankRepository = mock(BankRepository.class);
		service = new AccountServiceImpl(accountRepository, bankRepository);
	}
	@Test
	void contextLoads() {
		when(accountRepository.findById(1L)).thenReturn(Data.ACCOUNT_001);
		when(accountRepository.findById(2L)).thenReturn(Data.ACCOUNT_002);
		when(bankRepository.findById(1L)).thenReturn(Data.BANK);

		BigDecimal originAmount = service.getAmount(1L);
		BigDecimal destinationAmount = service.getAmount(2L);

		assertEquals("1000", originAmount.toPlainString());
		assertEquals("2000", destinationAmount.toPlainString());

		service.transfer(1L, 2L, new BigDecimal("100"), 1L);
		originAmount = service.getAmount(1L);
		destinationAmount = service.getAmount(2L);

		assertEquals("900", originAmount.toPlainString());
		assertEquals("2100", destinationAmount.toPlainString());
	}

}
