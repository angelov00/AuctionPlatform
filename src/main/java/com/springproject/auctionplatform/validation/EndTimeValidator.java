package com.springproject.auctionplatform.validation;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;

public class EndTimeValidator implements ConstraintValidator<EndTimeConstraint, LocalDateTime> {

    @Override
    public boolean isValid(LocalDateTime endTime, ConstraintValidatorContext context) {
        if (endTime == null) {
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime maxEndTime = now.plusDays(31);
        return !endTime.isAfter(maxEndTime);
    }
}