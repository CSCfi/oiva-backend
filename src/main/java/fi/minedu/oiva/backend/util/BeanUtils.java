package fi.minedu.oiva.backend.util;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;

public class BeanUtils {
    private static final Logger logger = LoggerFactory.getLogger(BeanUtils.class);

    public static class NullAwareBeanUtilsBean extends BeanUtilsBean {
        @Override
        public void copyProperty(Object dest, String name, Object value)
            throws IllegalAccessException, InvocationTargetException {
            if (value != null) super.copyProperty(dest, name, value);
        }
    }

    public static <T, V> void copyNonNullProperties(T dest, V orig) {
        BeanUtilsBean notNull = new NullAwareBeanUtilsBean();
        try {
            notNull.copyProperties(dest, orig);
        } catch (IllegalAccessException | InvocationTargetException e) {
            if (logger.isDebugEnabled()) {
                logger.error("Copy of properties failed", e);
            }
        }
    }

}

