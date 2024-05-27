package com.chinhbean.bookinghotel.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Field;

public class AtLeastOneTrueValidator implements ConstraintValidator<AtLeastOneTrue, Object> {

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        if (obj == null) {
            return true;
        }

        for (Field field : obj.getClass().getDeclaredFields()) {
            if (field.getType().equals(boolean.class) || field.getType().equals(Boolean.class)) {
                try {
                    field.setAccessible(true);
                    if (Boolean.TRUE.equals(field.get(obj))) {
                        return true;
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        return false;
    }
}
