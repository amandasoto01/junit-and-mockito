package org.example.test.springboot.app.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.test.springboot.app.Data;
import org.example.test.springboot.app.models.Account;
import org.example.test.springboot.app.models.TransactionDTO;
import org.example.test.springboot.app.services.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.example.test.springboot.app.Data.createAccount001;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mvc;

    ObjectMapper objectMapper;
    @MockBean
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void testDetails() throws Exception {
        // Given
        when(accountService.findById(1L)).thenReturn(createAccount001().orElseThrow());

        // When
        mvc.perform(get("/api/accounts/1").contentType(MediaType.APPLICATION_JSON))
                // Then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.person").value("Andres"))
                .andExpect(jsonPath("$.amount").value("1000"));

        verify(accountService).findById(1L);
    }

    @Test
    void testTransfer() throws Exception {

        // Given
        TransactionDTO dto = new TransactionDTO();
        dto.setOriginAccountId(1L);
        dto.setDestinationAccountId(2L);
        dto.setAmount(new BigDecimal("100"));
        dto.setBankId(1L);

        System.out.println(objectMapper.writeValueAsString(dto));

        Map<String, Object> response = new HashMap<>();
        //response.put("date", LocalDate.now());
        response.put("status", "OK");
        response.put("message", "Transfer successfully made");
        response.put("transaction", dto);

        System.out.println(objectMapper.writeValueAsString(response));

        // When
        mvc.perform(post("/api/accounts/transfer").contentType(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                // Then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.date").value(LocalDate.now().toString()))
                .andExpect(jsonPath("$.message").value("Transfer successfully made"))
                .andExpect(jsonPath("$.transaction.originAccountId").value(dto.getOriginAccountId()))
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    void testList() throws Exception {
        // Given
        List<Account> accounts = Arrays.asList(Data.createAccount001().orElseThrow(),
                Data.createAccount002().orElseThrow());
        when(accountService.findAll()).thenReturn(accounts);

        // When
        mvc.perform(get("/api/accounts").contentType(MediaType.APPLICATION_JSON))
                // Then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].person").value("Andres"))
                .andExpect(jsonPath("$[1].person").value("Jhon"))
                .andExpect(jsonPath("$[0].amount").value("1000"))
                .andExpect(jsonPath("$[1].amount").value("2000"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(content().json(objectMapper.writeValueAsString(accounts)));

        verify(accountService).findAll();
    }

    @Test
    void testSave() throws Exception {
        // Given
        Account account = new Account(null, "Pepe", new BigDecimal("3000"));
        when(accountService.save(any())).then( invocation -> {
            Account ac = invocation.getArgument(0);
            ac.setId(3L);
            return ac;
        });

        // When
        mvc.perform(post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(account)))
                // Then
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.person", is("Pepe")))
                .andExpect(jsonPath("$.amount", is(3000)));

        verify(accountService).save(any());
    }
}