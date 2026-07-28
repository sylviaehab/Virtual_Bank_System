package com.example.BFF_Service.exceptionHandler;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GlobalError {
    private int status;
    private String error;
    private String message;
}
