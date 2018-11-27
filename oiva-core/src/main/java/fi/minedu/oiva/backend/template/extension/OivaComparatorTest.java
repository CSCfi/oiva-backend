package fi.minedu.oiva.backend.template.extension;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

abstract public class OivaComparatorTest extends OivaTest {

    private final String compareOperator;

    public OivaComparatorTest(final String compareOperator) {
        if(null == compareOperator) {
            throw new IllegalArgumentException("Compare operator cannot be NULL");
        }
        this.compareOperator = compareOperator;
    }

    protected boolean check(final int comparedValue) {
        final List<Boolean> conditions = new ArrayList<Boolean>();
        for(char c : compareOperator.toCharArray()) {
            conditions.add(checkOperator(String.valueOf(c), comparedValue));
        }
        return BooleanUtils.or(conditions.toArray(new Boolean[conditions.size()]));
    }

    protected boolean checkOperator(final String op, final int value) {
        if(StringUtils.equals(">", op)) return value > 0;
        else if(StringUtils.equals("<", op)) return value < 0;
        else if(StringUtils.equals("=", op)) return value == 0;
        else return false;
    }
}
