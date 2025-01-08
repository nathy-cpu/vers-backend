package org.vers.backend.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.vers.backend.entity.Location;

public class LocationValidator
    implements ConstraintValidator<ValidLocation, Location> {

    @Override
    public boolean isValid(
        Location location,
        ConstraintValidatorContext context
    ) {
        if (location == null) return false;

        return (
            location.country != null &&
            !location.country.isBlank() &&
            location.region != null &&
            location.zone != null &&
            !location.zone.isBlank() &&
            location.woreda != null &&
            !location.woreda.isBlank()
        );
    }
}
