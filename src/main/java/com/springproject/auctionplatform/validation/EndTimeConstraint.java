package com.springproject.auctionplatform.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = EndTimeValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EndTimeConstraint {
    String message() default "End time must not be more than 31 days from now.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
