package fi.minedu.oiva.backend.core.extension;

import org.apache.commons.lang3.math.NumberUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class NumberComparatorTest extends OivaTest {

    protected String argOperator = "operator";
    protected String argComparedTo = "comparedTo";

    @Override
    public boolean apply(final Object data, final Map<String, Object> map) {
        final Long target = getTarget(data);
        if(null == target) {
            return false;
        }
        if(!argExists(map, argOperator) && !argExists(map, argComparedTo)) {
            return true;
        } else {
            final String operator = getOperator(map);
            return new NumberComparator(operator).apply(data, map);
        }
    }

    private Long getTarget(final Object data) {
        if(data instanceof Number) {
            return (Long) data;
        } else if(data instanceof String) {
            return NumberUtils.toLong((String) data);
        } else {
            return null;
        }
    }

    private String getOperator(final Map<String, Object> map) {
        return getArgAsString(map, argOperator);
    }

    private Long getComparedTo(final Map<String, Object> map) {
        return getArgAsLong(map, argComparedTo);
    }

    @Override
    public List<String> getArgumentNames() {
        return Arrays.asList(new String[]{argOperator, argComparedTo});
    }

    class NumberComparator extends OivaComparatorTest {
        public NumberComparator(final String operator) {
            super(operator);
        }
        public boolean apply(Object data, Map<String, Object> map) {
            final Long target = getTarget(data);
            final Long comparedTo = getComparedTo(map);
            if (null == target || null == comparedTo) {
                return false;
            }
            return check(target.compareTo(comparedTo));
        }
        public List<String> getArgumentNames() {
            return Collections.emptyList();
        }
    }
}
