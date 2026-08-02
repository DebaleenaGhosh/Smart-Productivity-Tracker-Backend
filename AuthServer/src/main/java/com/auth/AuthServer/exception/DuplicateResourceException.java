package com.auth.AuthServer.exception;

public class DuplicateResourceException extends RuntimeException
{
    public DuplicateResourceException(String message) {
        super(message);
    }
}
