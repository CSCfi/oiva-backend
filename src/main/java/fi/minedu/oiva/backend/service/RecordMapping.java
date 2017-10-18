package fi.minedu.oiva.backend.service;

import org.apache.commons.lang3.StringUtils;
import org.jooq.Field;
import org.jooq.Record;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface RecordMapping<T> {

    String withAll = "all";

    default <T> T convertFieldsTo(final Record r, final Field<?>[] fields, final Class<T> clazz) {
        return r.into(fields).into(clazz);
    }

    default <T, V> void putToMap(Map<V, T> map, Record r, Field<?>[] fields, Class<T> clazz) {
        final Record intermediateRecord = r.into(fields);
        final V id = (V) intermediateRecord.getValue("id");
        if (id != null && !map.containsKey(id)) {
            map.put(id, intermediateRecord.into(clazz));
        }
    }

    default boolean withOption(final Class<?> clazz, final String... with) {
        final List withOptions = null == with ? Collections.emptyList() : Arrays.asList(with).stream().map(String::toLowerCase).collect(Collectors.toList());
        final Function<Class<?>, Boolean> hasOption = targetClass ->
                withOptions.contains(StringUtils.lowerCase(targetClass.getSimpleName())) || withOptions.contains(withAll);
        return hasOption.apply(clazz);
    }
}
