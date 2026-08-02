package com.auth.AuthServer.exception;

public class BadCredentialsException extends RuntimeException
{
    public BadCredentialsException(String message)
    {
        super(message);
    }
}
