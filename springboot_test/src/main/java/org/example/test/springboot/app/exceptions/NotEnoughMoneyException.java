package org.example.test.springboot.app.exceptions;

public class NotEnoughMoneyException extends  RuntimeException{
    public NotEnoughMoneyException(String message) {
        super(message);
    }
}
