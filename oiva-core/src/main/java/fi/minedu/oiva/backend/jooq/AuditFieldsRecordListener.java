package fi.minedu.oiva.backend.jooq;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.RecordContext;
import org.jooq.impl.DefaultRecordListener;

import java.sql.Timestamp;
import java.time.Instant;

public class AuditFieldsRecordListener extends DefaultRecordListener {

    @Override
    @SuppressWarnings("unchecked")
    public void updateStart(final RecordContext ctx) {
        final Record record = ctx.record();
        final Field<?> paivitysPvm = record.field("paivityspvm");
        if (paivitysPvm != null && paivitysPvm.getType().equals(Timestamp.class)) {
            record.setValue((Field<Timestamp>) paivitysPvm, Timestamp.from(Instant.now()));
        }
    }
}
