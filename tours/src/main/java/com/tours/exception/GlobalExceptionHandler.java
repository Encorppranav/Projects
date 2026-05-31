package com.tours.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler
{
    @ExceptionHandler(TransportNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleTransportNotFoundException(TransportNotFoundException ex , WebRequest request)
    {
        ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(),ex.getMessage(), request.getDescription(false));

        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(LodgingNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleLodgingNotFoundException(LodgingNotFoundException ex, WebRequest request)
    {
        ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), ex.getMessage(), request.getDescription((false)));

        return new ResponseEntity<>(errorDetails,HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(LocationNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleLocationNotFoundException(LocationNotFoundException ex , WebRequest request )
    {
        ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now() , ex.getMessage() , request.getDescription((false)));

        return new ResponseEntity<>(errorDetails,HttpStatus.OK);
    }

    @ExceptionHandler(InsufficientTicketsException.class)
    public ResponseEntity<ErrorDetails> handleInsufficientTicketsException(InsufficientTicketsException ex , WebRequest request)
    {
        ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now() , ex.getMessage() , request.getDescription(false));

        return new ResponseEntity<>(errorDetails , HttpStatus.OK);
    }

    @ExceptionHandler(BookingNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleBookingNotFoundException(BookingNotFoundException ex , WebRequest request)
    {
        ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now() , ex.getMessage() , request.getDescription(false));

        return new ResponseEntity<>(errorDetails , HttpStatus.OK);
    }

    @ExceptionHandler(PaymentFailedException.class)
    public ResponseEntity<ErrorDetails> handlePaymentFailedException(PaymentFailedException ex , WebRequest request)
    {
        ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now() , ex.getMessage() , request.getDescription(false));

        return new ResponseEntity<>(errorDetails , HttpStatus.OK);
    }

    @ExceptionHandler(TourNotFoundException.class)
    public ResponseEntity<ErrorDetails> handlTourNotFoundException(TourNotFoundException ex , WebRequest request)
    {
        ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now() , ex.getMessage() , request.getDescription(false));

        return new ResponseEntity<>(errorDetails , HttpStatus.OK);
    }

}
