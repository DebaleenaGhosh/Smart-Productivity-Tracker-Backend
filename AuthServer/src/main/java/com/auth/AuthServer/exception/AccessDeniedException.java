package com.auth.AuthServer.exception;

public class AccessDeniedException extends RuntimeException
{
    public AccessDeniedException(String message)
    {
        super("AuthUser is not authorized");
    }
}
