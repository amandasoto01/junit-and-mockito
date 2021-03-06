package org.example.test.springboot.app.controllers;

import org.example.test.springboot.app.models.Account;
import org.example.test.springboot.app.models.TransactionDTO;
import org.example.test.springboot.app.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @GetMapping
    @ResponseStatus(OK)
    public List<Account> list() {
        return accountService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> details(@PathVariable(name="id")  Long id) {

        Account account = null;
        try {
            account = accountService.findById(id);
        } catch ( NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(account);
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public Account save(@RequestBody Account account) {
        return accountService.save(account);
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(@RequestBody TransactionDTO dto) {
        accountService.transfer(dto.getOriginAccountId(),
                dto.getDestinationAccountId(),
                dto.getAmount(),
                dto.getBankId());

        Map<String, Object> response = new HashMap<>();
        response.put("date", LocalDate.now());
        response.put("status", "OK");
        response.put("message", "Transfer successfully made");
        response.put("transaction", dto);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    public void delete(@PathVariable Long id) {
        accountService.deleteById(id);
    }

}
