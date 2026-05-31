package com.tours.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorDetails
{
    private LocalDateTime timeStamp;
    private String message;
    private String details;

}
