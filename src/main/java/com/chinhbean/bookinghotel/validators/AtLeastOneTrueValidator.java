package com.chinhbean.bookinghotel.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

public class AtLeastOneTrueValidator implements ConstraintValidator<AtLeastOneTrue, Object> {

    private static final Logger logger = LoggerFactory.getLogger(AtLeastOneTrueValidator.class);

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
                    logger.error("Illegal access to field: {}", field.getName(), e);
                }
            }
        }
        return false;
    }
}
