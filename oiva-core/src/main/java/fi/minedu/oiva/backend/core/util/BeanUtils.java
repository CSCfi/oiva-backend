package fi.minedu.oiva.backend.core.util;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;

public class BeanUtils {

    private static final Logger logger = LoggerFactory.getLogger(BeanUtils.class);

    public static class NullAwareBeanUtilsBean extends BeanUtilsBean {

        @Override
        public void copyProperty(final Object dest, final String name, final Object value) throws IllegalAccessException, InvocationTargetException {
            if (value != null) super.copyProperty(dest, name, value);
        }
    }

    public static <T, V> void copyNonNullProperties(final T dest, final V orig) {
        final BeanUtilsBean notNull = new NullAwareBeanUtilsBean();
        try {
            notNull.copyProperties(dest, orig);
        } catch (IllegalAccessException | InvocationTargetException e) {
            if (logger.isDebugEnabled()) {
                logger.error("Copy of properties failed", e);
            }
        }
    }

    public static <T, V> T copyNonNullPropertiesAndReturn(final T dest, final V orig) {
        copyNonNullProperties(dest, orig);
        return dest;
    }
}
