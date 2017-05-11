package fi.minedu.oiva.backend.service;

import org.jooq.Field;
import org.jooq.Record;

import java.util.Map;

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
}
