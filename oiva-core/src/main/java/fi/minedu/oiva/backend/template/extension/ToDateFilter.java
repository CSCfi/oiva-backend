package fi.minedu.oiva.backend.template.extension;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ToDateFilter extends OivaFilter {

    private static String defaultFormat = "yyyy-MM-dd";

    protected String argFormat = "format";

    @Override
    public Object apply(final Object data, final Map<String, Object> map) {
        if (data == null) {
            return null;
        } else if(data instanceof Date) {
            return (Date) data;
        } else if(data instanceof String) {
            try {
                return new SimpleDateFormat(getFormat(map)).parse((String) data);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private String getFormat(final Map<String, Object> map) {
        return argExists(map, argFormat) ? getArgAsString(map, argFormat) : defaultFormat;
    }

    @Override
    public List<String> getArgumentNames() {
        return Arrays.asList(new String[]{ argFormat });
    }

    public static Date apply(final Object obj) {
        return (Date) new ToDateFilter().apply(obj, Collections.emptyMap());
    }
}
