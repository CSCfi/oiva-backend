package fi.minedu.oiva.backend.core.service;

import fi.minedu.oiva.backend.core.util.With;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class BaseService {

    protected boolean withOption(final Class<?> clazz, final String... with) {
        final List withOptions = null == with ? Collections.emptyList() :
                Arrays.stream(with).map(String::toLowerCase).collect(Collectors.toList());
        final Function<Class<?>, Boolean> hasOption = targetClass ->
                withOptions.contains(StringUtils.lowerCase(targetClass.getSimpleName())) ||
                        withOptions.contains(With.all);
        return hasOption.apply(clazz);
    }
}
