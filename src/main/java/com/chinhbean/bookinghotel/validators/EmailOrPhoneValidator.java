package com.chinhbean.bookinghotel.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class EmailOrPhoneValidator implements ConstraintValidator<EmailOrPhone, String> {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w-]+(\\.[\\w-]+)*@[\\w-]+(\\.[\\w-]+)*(\\.[a-zA-Z]{2,})$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{10}$");

    @Override
    public void initialize(EmailOrPhone constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true;
        }
        return EMAIL_PATTERN.matcher(value).matches() || PHONE_PATTERN.matcher(value).matches();
    }
}
