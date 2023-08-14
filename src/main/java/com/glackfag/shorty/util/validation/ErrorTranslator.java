package com.glackfag.shorty.util.validation;

import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import java.util.Objects;

public abstract class ErrorTranslator  {
    public static void translateError(Errors errors, FieldError fieldError){
        errors.rejectValue(fieldError.getField(),
                Objects.requireNonNull(fieldError.getCode()),
                Objects.requireNonNull(fieldError.getDefaultMessage()));
    }
}
