package fi.minedu.oiva.backend.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Utilities for validation checks
 *
 * Created by aheikkinen on 22/09/16.
 */
public class ValidationUtils {

    final static Logger logger = LoggerFactory.getLogger(ValidationUtils.class);

    public static boolean isNotValid(final ValidationUtils.Validation... validationEntries) {
        return !validate(validationEntries);
    }

    public static boolean validate(final Object value, final String message) {
        return validate(ValidationUtils.validation(value, message));
    }

    public static boolean validate(final ValidationUtils.Validation... validationEntries) {

        final Collection<String> validationErrors = new ArrayList<>();

        for(ValidationUtils.Validation entry : validationEntries) {
            if(isEmpty(entry.value)) {
                validationErrors.add(entry.message);
            }
        }

        if(!validationErrors.isEmpty()) {
            logger.error("Validation errors:");
        }
        validationErrors.forEach( s -> {
            logger.error("  {}", s);
        });

        return validationErrors.isEmpty();
    }

    private static boolean isEmpty(final Object o) {
        if(null == o) return true;
        else if(o instanceof String) return String.valueOf(o).trim().equalsIgnoreCase("");
        else if(o instanceof Object[]) return ((Object[])o).length == 0;
        else if(o instanceof Collection) return ((Collection)o).isEmpty();
        else if(o.getClass().getName().equals("long")) return ((long)o) < 1;
        else return false;
    }

    public static Validation validation(final Object value, final String message) {
        return new ValidationUtils.Validation(value, message);
    }

    public static class Validation {
        public final Object value;
        public final String message;
        public Validation(final Object value, final String message) {
            this.value = value;
            this.message = message;
        }
    }
}
