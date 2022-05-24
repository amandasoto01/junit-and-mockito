package org.example.junit5.models;

import org.example.junit5.exceptions.NotEnoughMoneyException;

import java.math.BigDecimal;

public class Account {
    private String name;
    private BigDecimal amount;
    private Bank bank;

    public Account(String name, BigDecimal amount) {
        this.amount = amount;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Bank getBank() {
        return bank;
    }

    public void setBank(Bank bank) {
        this.bank = bank;
    }

    public void debit(BigDecimal amount) {
       BigDecimal newAmount = this.amount.subtract(amount);

       if( newAmount.compareTo(BigDecimal.ZERO) < 0 ) {
           throw new NotEnoughMoneyException("Not enough money");
       }

       this.amount = newAmount;
    }

    public void credit(BigDecimal amount) {
        this.amount = this.amount.add(amount);
    }

    @Override
    public boolean equals(Object obj) {

        if(!(obj instanceof Account)) {
            return false;
        }

        if(this.name == null && this.amount == null ) {
            return false;
        }

        Account ac = (Account) obj;

        return this.name.equals(ac.getName()) && this.amount.equals(ac.getAmount());
    }
}
