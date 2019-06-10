package fi.minedu.oiva.backend.core.extension;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DateTest extends OivaTest {

    protected String argOperator = "operator";
    protected String argComparedTo = "comparedTo";

    @Override
    public boolean apply(final Object data, final Map<String, Object> map) {
        final Date target = getTarget(data);
        if(null == target) {
            return false;
        }
        if(!argExists(map, argOperator) && !argExists(map, argComparedTo)) {
            return true;
        } else {
            final String operator = getOperator(map);
            return new DateComparatorTest(operator).apply(data, map);
        }
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

    private String getOperator(final Map<String, Object> map) {
        return getArgAsString(map, argOperator);
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
        return Arrays.asList(new String[]{argOperator, argComparedTo});
    }
}
