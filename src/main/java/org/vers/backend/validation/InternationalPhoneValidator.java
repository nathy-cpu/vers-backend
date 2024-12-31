package org.vers.backend.validation;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class InternationalPhoneValidator
    implements ConstraintValidator<InternationalPhone, String> {

    private final PhoneNumberUtil phoneNumberUtil =
        PhoneNumberUtil.getInstance();

    @Override
    public boolean isValid(
        String phoneNumber,
        ConstraintValidatorContext context
    ) {
        if (phoneNumber == null || phoneNumber.isBlank()) {
            return true; // Allow null or blank (use @NotBlank separately if needed)
        }

        try {
            Phonenumber.PhoneNumber parsedNumber = phoneNumberUtil.parse(
                phoneNumber,
                "ET"
            ); // Default region, e.g., "ET" for Ethiopia
            return phoneNumberUtil.isValidNumber(parsedNumber);
        } catch (NumberParseException e) {
            return false;
        }
    }
}
