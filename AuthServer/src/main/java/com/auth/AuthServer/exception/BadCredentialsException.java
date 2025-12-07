package com.auth.AuthServer.exception;

public class BadCredentialsException extends Exception
{
    public BadCredentialsException(String message)
    {
        super(message);
    }
}
