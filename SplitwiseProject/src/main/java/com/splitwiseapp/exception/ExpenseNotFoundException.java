package com.splitwiseapp.exception;

public class ExpenseNotFoundException extends RuntimeException {

    public ExpenseNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
