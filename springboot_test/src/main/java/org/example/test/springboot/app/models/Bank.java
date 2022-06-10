package org.example.test.springboot.app.models;

public class Bank {
    private Long id;
    private String name;
    private int totalTransfer;

    public Bank() {
    }

    public Bank(Long id, String name, int totalTransfer) {
        this.id = id;
        this.name = name;
        this.totalTransfer = totalTransfer;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTotalTransfers() {
        return totalTransfer;
    }

    public void setTotalTransfer(int totalTransfer) {
        this.totalTransfer = totalTransfer;
    }
}
