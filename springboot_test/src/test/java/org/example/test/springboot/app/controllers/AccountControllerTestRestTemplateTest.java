package org.example.test.springboot.app.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.test.springboot.app.models.Account;
import org.example.test.springboot.app.models.TransactionDTO;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Tag("integration_rt")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AccountControllerTestRestTemplateTest {

    @Autowired
    private TestRestTemplate client;

    private ObjectMapper objectMapper;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    @Order(1)
    void list() throws JsonProcessingException {
        TransactionDTO dto = new TransactionDTO();
        dto.setAmount(new BigDecimal("100"));
        dto.setDestinationAccountId(2L);
        dto.setOriginAccountId(1L);
        dto.setBankId(1L);

        ResponseEntity<String> response = client.postForEntity(createUri("/api/accounts/transfer"), dto, String.class);
        System.out.println(port);

        String json = response.getBody();
        System.out.println(json);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertNotNull(json);
        assertTrue(json.contains("Transfer successfully made"));
        assertTrue(json.contains("{\"originAccountId\":1,\"destinationAccountId\":2,\"bankId\":1,\"amount\":100}"));

        JsonNode jsonNode = objectMapper.readTree(json);
        assertEquals("Transfer successfully made", jsonNode.path("message").asText());
        //assertEquals(LocalDate.now().toString(), jsonNode.path("date").asText());
        assertEquals("100", jsonNode.path("transaction").path("amount").asText());
        assertEquals(1L, jsonNode.path("transaction").path("originAccountId").asLong());

        Map<String, Object> response2 = new HashMap<>();
        //response2.put("date", LocalDate.now());
        response2.put("status", "OK");
        response2.put("message", "Transfer successfully made");
        response2.put("transaction", dto);

        //assertEquals(objectMapper.writeValueAsString(response2), json);
    }

    @Test
    @Order(2)
    void testDetail() {
        ResponseEntity<Account> response = client.getForEntity(createUri("/api/accounts/1"), Account.class);
        Account account = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());

        assertNotNull(account);
        assertEquals("Andres", account.getPerson());
        assertEquals("900.00", account.getAmount().toPlainString());
        assertEquals(new Account(1L, "Andres", new BigDecimal("900.00")), account);
    }

    @Test
    @Order(3)
    void testList() throws JsonProcessingException {
        ResponseEntity<Account[]> response = client.getForEntity(createUri("/api/accounts"), Account[].class);
        List<Account> accounts  = Arrays.asList(response.getBody());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());

        assertEquals(2, accounts.size());
        assertEquals(1L, accounts.get(0).getId());
        assertEquals("Andres", accounts.get(0).getPerson());
        assertEquals("900.00", accounts.get(0).getAmount().toPlainString());

        assertEquals(2L, accounts.get(1).getId());
        assertEquals("Jhon", accounts.get(1).getPerson());
        assertEquals("2100.00", accounts.get(1).getAmount().toPlainString());

        JsonNode json = objectMapper.readTree(objectMapper.writeValueAsString(accounts));
        assertEquals(1L, json.get(0).path("id").asLong());
        assertEquals("Andres", json.get(0).path("person").asText());
        assertEquals("900.0", json.get(0).path("amount").asText());

        assertEquals(2L, json.get(1).path("id").asLong());
        assertEquals("Jhon", json.get(1).path("person").asText());
        assertEquals("2100.0", json.get(1).path("amount").asText());
    }

    @Test
    @Order(4)
    void testSave() {
        Account account = new Account(null, "Pepa", new BigDecimal("3800"));

        ResponseEntity<Account> response = client.postForEntity(createUri("/api/accounts"), account, Account.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());

        Account accountCreated = response.getBody();

        assertNotNull(accountCreated);
        assertEquals(3L,  accountCreated.getId());
        assertEquals("Pepa",  accountCreated.getPerson());
        assertEquals("3800",  accountCreated.getAmount().toPlainString());
    }

    @Test
    @Order(5)
    void testDelete() {

        ResponseEntity<Account[]> response = client.getForEntity(createUri("/api/accounts"), Account[].class);
        List<Account> accounts  = Arrays.asList(response.getBody());
        assertEquals(3, accounts.size());

        //client.delete(createUri("/api/accounts/3"));
        Map<String, Integer> pathVariables = new HashMap<>();
        pathVariables.put("id",3);
        ResponseEntity<Void> exchange = client.exchange(createUri("/api/accounts/{id}"), HttpMethod.DELETE, null, Void.class, pathVariables);
        assertEquals(HttpStatus.NO_CONTENT, exchange.getStatusCode());
        assertFalse(exchange.hasBody());

        response = client.getForEntity(createUri("/api/accounts"), Account[].class);
        accounts = Arrays.asList(response.getBody());
        assertEquals(2, accounts.size());

        ResponseEntity<Account> responseDetail = client.getForEntity(createUri("/api/accounts/3"), Account.class);
        assertEquals(HttpStatus.NOT_FOUND, responseDetail.getStatusCode());
        assertFalse(responseDetail.hasBody());
    }

    private String createUri(String uri) {
        return "http://localhost:" + port + uri;
    }
}