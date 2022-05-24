package org.example.junit5.exceptions;

public class NotEnoughMoneyException extends  RuntimeException{

    public NotEnoughMoneyException(String message) {
        super(message);
    }
}
