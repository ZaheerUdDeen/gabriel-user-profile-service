package com.spotlight.platform.userprofile.api.web.validation;

import com.spotlight.platform.userprofile.api.web.dto.UserCommandDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.Map;

public class InputCommandValidator implements ConstraintValidator<ValidCommandType, String> {

    @Override
    public void initialize(ValidCommandType constraintAnnotation) {
    }

    @Override
    public boolean isValid(String commandType, ConstraintValidatorContext constraintValidatorContext) {
        if (commandType == null) {
            return false;
        }

        // Check if the commandType is a valid value
        try {
            UserCommandDto.CommandType.valueOf(commandType);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static boolean validateInputCommand(UserCommandDto userCommandDto) {

        if(userCommandDto.getType().equals(UserCommandDto.CommandType.increment.name())) {
            return validateIncrementCommand(userCommandDto);
        } else if(userCommandDto.getType().equals(UserCommandDto.CommandType.collect.name())) {
            return validateCollectCommand(userCommandDto);
        }
        return true;
    }

    public static boolean validateIncrementCommand(UserCommandDto userCommandDto) {
        boolean inputValueValidFormat = true;

        for (Map.Entry<String, Object> entry : userCommandDto.getProperties().entrySet()) {
            Object inputValue = entry.getValue();

            if (inputValue instanceof Integer) {
                inputValueValidFormat = true;
            } else {
                inputValueValidFormat = false;
                break;
            }
        }
        return inputValueValidFormat;
    }

    public static boolean validateCollectCommand(UserCommandDto userCommandDto) {
        boolean inputValueValidFormat = true;

        for (Map.Entry<String, Object> entry : userCommandDto.getProperties().entrySet()) {
            Object inputValue = entry.getValue();

            if (inputValue instanceof List) {
                inputValueValidFormat = true;
            } else {
                inputValueValidFormat = false;
                break;
            }
        }

        return inputValueValidFormat;
    }


}
