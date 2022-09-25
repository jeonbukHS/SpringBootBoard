package com.mysite.sbb;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.annotation.Resources;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "entity is not found")
public class DataNotFoundException extends RuntimeException{
    private final long serialVersionID = 1L;
    public DataNotFoundException(String message){
        super(message);
    }
}
