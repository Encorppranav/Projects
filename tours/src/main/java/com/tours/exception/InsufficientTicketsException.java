package com.tours.exception;

public class InsufficientTicketsException extends RuntimeException{
    public InsufficientTicketsException(String msg){super(msg);}
}
