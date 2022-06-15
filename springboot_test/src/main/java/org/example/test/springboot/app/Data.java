package org.example.test.springboot.app;

import org.example.test.springboot.app.models.Account;
import org.example.test.springboot.app.models.Bank;

import java.math.BigDecimal;

public class Data {

    //public static final Account ACCOUNT_001 = new Account(1L, "Andres", new BigDecimal("1000"));
    //public static final Account ACCOUNT_002 = new Account(2L, "Jhon", new BigDecimal("2000"));
    //public static final Bank BANK = new Bank(1L, "The Bank", 0);

    public static final Account createAccount001() {
        return new Account(1L, "Andres", new BigDecimal("1000"));
    }

    public static final Account createAccount002() {
        return new Account(1L, "Jhon", new BigDecimal("2000"));
    }

    public static final Bank createBank() {
        return new Bank(1L, "The Bank", 0);
    }

}
