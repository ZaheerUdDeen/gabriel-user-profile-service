package com.spotlight.platform.userprofile.api.web.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = InputCommandValidator.class)
public @interface ValidCommandType {
    String message() default "Command type is not correct";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
