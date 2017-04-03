package fi.minedu.oiva.backend.util;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Created by aheikkinen on 15/09/16.
 */
public class DateUtils {

    public static boolean isPastYear(final Integer year) {
        final LocalDateTime ldt = LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());
        return null == year ? false : year < ldt.getYear();
    }

    public static Timestamp getFirstOrNullDate(final Stream<Timestamp> source) {
        final Optional<Optional<Timestamp>> opt = source
            .sorted(Comparator.nullsLast((ok1, ok2) -> ok1.compareTo(ok2)))
            .map(Optional::ofNullable).findFirst();
        return opt.isPresent() ? opt.get().orElse(null) : null;
    }

    public static Timestamp getNullOrLastDate(final Stream<Timestamp> source) {
        final Optional<Optional<Timestamp>> opt = source
            .sorted(Comparator.nullsFirst((ok1, ok2) -> ok2.compareTo(ok1)))
            .map(Optional::ofNullable).findFirst();
        return opt.isPresent() ? opt.get().orElse(null) : null;
    }
}
