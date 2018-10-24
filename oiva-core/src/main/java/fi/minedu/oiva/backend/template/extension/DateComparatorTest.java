package fi.minedu.oiva.backend.template.extension;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DateComparatorTest extends OivaComparatorTest {

    protected String argComparedTo = "comparedTo";

    public DateComparatorTest(final String compareOperator) {
        super(compareOperator);
    }

    @Override
    public boolean apply(final Object data, final Map<String, Object> map) {
        final Date target = getTarget(data);
        final Date comparedTo = getComparedTo(map);
        if (null == target || null == comparedTo) {
            return false;
        }
        return check(target.compareTo(comparedTo));
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

    private Date getComparedTo(final Map<String, Object> map) {
        if(isArgType(map, argComparedTo, Date.class)) {
            return getArgAsDate(map, argComparedTo);
        } else if(isArgType(map, argComparedTo, String.class)) {
            return ToDateFilter.apply(getArgAsString(map, argComparedTo));
        } else {
            return null;
        }
    }

    @Override
    public List<String> getArgumentNames() {
        return Arrays.asList(new String[]{argComparedTo});
    }
}
