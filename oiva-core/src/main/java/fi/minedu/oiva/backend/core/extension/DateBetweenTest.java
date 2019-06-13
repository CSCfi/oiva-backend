package fi.minedu.oiva.backend.core.extension;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DateBetweenTest extends OivaTest {

    protected String argStartDate = "startDate";
    protected String argEndDate = "endDate";

    protected final DateComparatorTest startDateComparator;
    protected final DateComparatorTest endDateComparator;

    public DateBetweenTest(final String compareStart, final String compareEnd) {
        super();
        this.startDateComparator = new DateComparatorTest(compareStart);
        this.endDateComparator = new DateComparatorTest(compareEnd);
    }

    @Override
    public boolean apply(final Object data, final Map<String, Object> map) {
        final Date target = getTarget(data);
        final Date comparedToStart = getComparedToStart(map);
        final Date comparedToEnd = getComparedToEnd(map);
        if (null == target || null == comparedToStart || null == comparedToEnd) {
            return false;
        }
        return startDateComparator.check(target.compareTo(comparedToStart)) &&
               endDateComparator.check(target.compareTo(comparedToEnd));
    }

    private Date getTarget(final Object data) {
        if(data instanceof Date) {
            return (Date) data;
        } else if(data instanceof String) {
            return ToDateFilter.apply(data);
        } else {
            return null;
        }
    }

    private Date getComparedToStart(final Map<String, Object> map) {
        return getComparedTo(map, argStartDate);
    }

    private Date getComparedToEnd(final Map<String, Object> map) {
        return getComparedTo(map, argEndDate);
    }

    private Date getComparedTo(final Map<String, Object> map, final String argumentName) {
        if(isArgType(map, argumentName, Date.class)) {
            return getArgAsDate(map, argumentName);
        } else if(isArgType(map, argumentName, String.class)) {
            return ToDateFilter.apply(getArgAsString(map, argumentName));
        } else {
            return null;
        }
    }

    @Override
    public List<String> getArgumentNames() {
        return Arrays.asList(new String[]{argStartDate, argEndDate});
    }
}
