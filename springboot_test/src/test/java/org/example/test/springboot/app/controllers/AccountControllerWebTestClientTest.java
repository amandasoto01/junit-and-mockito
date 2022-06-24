package org.example.test.springboot.app.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.test.springboot.app.models.Account;
import org.example.test.springboot.app.models.TransactionDTO;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
class AccountControllerWebTestClientTest {

    private ObjectMapper objectMapper;
    @Autowired
    private WebTestClient client;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    @Order(1)
    void testTransfer() throws JsonProcessingException {

        // Given
        TransactionDTO dto = new TransactionDTO();
        dto.setOriginAccountId(1L);
        dto.setDestinationAccountId(2L);
        dto.setBankId(1L);
        dto.setAmount(new BigDecimal("100"));

        Map<String, Object> response = new HashMap<>();
        //response.put("date", LocalDate.now());
        response.put("status", "OK");
        response.put("message", "Transfer successfully made");
        response.put("transaction", dto);

        // When
        client.post().uri("/api/accounts/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                // Then
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                // .expectBody(String.class)
                .consumeWith(r -> {
                    try {
                        // String jsonStr =   r.getResponseBody();
                        // JsonNode json = objectMapper.readTree(jsonStr);
                        JsonNode json = objectMapper.readTree(r.getResponseBody());
                        assertEquals("Transfer successfully made", json.path("message").asText());
                        assertEquals(1, json.path("transaction").path("originAccountId").asLong());
                        assertEquals(LocalDate.now().toString(), json.path("date").asText());
                        assertEquals("100", json.path("transaction").path("amount").asText());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .jsonPath("$.message").isNotEmpty()
                .jsonPath("$.message").value(is("Transfer successfully made"))
                .jsonPath("$.message").value(value -> assertEquals("Transfer successfully made", value))
                .jsonPath("$.message").isEqualTo("Transfer successfully made")
                .jsonPath("$.transaction.originAccountId").isEqualTo(dto.getOriginAccountId())
                .jsonPath("$.date").isEqualTo(LocalDate.now().toString())
                .json(objectMapper.writeValueAsString(response));
    }

    @Test
    @Order(2)
    void testDetails() throws JsonProcessingException {
        Account account = new Account(1L, "Andres", new BigDecimal("900"));

        client.get().uri("/api/accounts/1")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.person").isEqualTo("Andres")
                .jsonPath("$.amount").isEqualTo(900)
                .json(objectMapper.writeValueAsString(account));
    }

    @Test
    @Order(3)
    void testDetails2() {
        client.get().uri("/api/accounts/2")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Account.class)
                .consumeWith(response -> {
                    Account account = response.getResponseBody();

                    assertNotNull(account);
                    assertEquals("Jhon", account.getPerson());
                    assertEquals("2100.00", account.getAmount().toPlainString());
                });
    }

    @Test
    @Order(4)
    void testList() {
        client.get().uri("/api/accounts")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$[0].person").isEqualTo("Andres")
                .jsonPath("$[0].id").isEqualTo(1)
                .jsonPath("$[0].amount").isEqualTo(900)
                .jsonPath("$[1].person").isEqualTo("Jhon")
                .jsonPath("$[1].id").isEqualTo(2)
                .jsonPath("$[1].amount").isEqualTo(2100)
                .jsonPath("$").isArray()
                .jsonPath("$").value(hasSize(2));
    }

    @Test
    @Order(5)
    void testList2() {
        client.get().uri("/api/accounts")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Account.class)
                .consumeWith( response -> {
                    List<Account> accounts = response.getResponseBody();

                    assertNotNull(accounts);
                    assertEquals(2, accounts.size());

                    assertEquals(1L, accounts.get(0).getId());
                    assertEquals("Andres", accounts.get(0).getPerson());
                    assertEquals(900, accounts.get(0).getAmount().intValue());

                    assertEquals(2L, accounts.get(1).getId());
                    assertEquals("Jhon", accounts.get(1).getPerson());
                    assertEquals("2100.0", accounts.get(1).getAmount().toPlainString());
                })
                .hasSize(2)
                .value(hasSize(2));
    }

    @Test
    @Order(6)
    void testSave() {
        // Given
        Account account = new Account(null, "Pepe", new BigDecimal("3000"));

        // When
        client.post().uri("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(account)
                .exchange()
                // Then
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isEqualTo(3)
                .jsonPath("$.person").value(is("Pepe"))
                .jsonPath("$.person").isEqualTo("Pepe")
                .jsonPath("$.amount").isEqualTo(3000);
    }

    @Test
    @Order(7)
    void testSave2() {
        // Given
        Account account = new Account(null, "Pepa", new BigDecimal("3500"));

        // When
        client.post().uri("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(account)
                .exchange()
                // Then
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Account.class)
                .consumeWith( response -> {
                   Account ac = response.getResponseBody();

                   assertNotNull(ac);
                   assertEquals(4L, ac.getId());
                   assertEquals("Pepa", ac.getPerson());
                   assertEquals("3500", ac.getAmount().toPlainString());
                });
    }

    @Test
    @Order(8)
    void testDelete() {

        client.get().uri("/api/accounts").exchange()
                        .expectStatus().isOk()
                        .expectHeader().contentType(MediaType.APPLICATION_JSON)
                        .expectBodyList(Account.class)
                                .hasSize(4);

        client.delete().uri("/api/accounts/3")
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty();

        client.get().uri("/api/accounts").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Account.class)
                .hasSize(3);

        client.get().uri("/api/accounts/3")
                .exchange()
                //.expectStatus().is5xxServerError();
                .expectStatus().isNotFound()
                .expectBody().isEmpty();
    }
}