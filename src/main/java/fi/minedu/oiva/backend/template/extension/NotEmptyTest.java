package fi.minedu.oiva.backend.template.extension;

import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class NotEmptyTest extends OivaTest {

    @Override
    public boolean apply(final Object obj, final Map<String, Object> map) {
        if(null == obj) {
            return false;
        }

        if(obj instanceof Collection) {
            final Collection list = (Collection) obj;
            return null != list && !list.isEmpty();

        } else if(obj instanceof String) {
            return StringUtils.isNotBlank((String) obj);
        }

        return true;
    }

    public static boolean check(final Object obj) {
        return new NotEmptyTest().apply(obj, Collections.emptyMap());
    }

    @Override
    public List<String> getArgumentNames() {
        return emptyArgumentNames();
    }
}
