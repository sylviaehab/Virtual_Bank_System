package com.example.Account_Service.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EnumValidator implements ConstraintValidator<ValidEnum, String> {
    private Class<? extends Enum<?>> enumClass;

    @Override
    public void initialize(ValidEnum annotation) {
        this.enumClass = annotation.enumClass();
    }

    @Override
    public boolean isValid(
            String value,
            ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true;
        }
        for (Enum<?> enumValue : enumClass.getEnumConstants()) {
            if (enumValue.name().equals(value)) {
                return true;
            }
        }

        return false;
    }
}