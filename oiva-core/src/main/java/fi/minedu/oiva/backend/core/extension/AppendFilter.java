package fi.minedu.oiva.backend.core.extension;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class AppendFilter extends OivaFilter {

    private static final Logger logger = LoggerFactory.getLogger(AppendFilter.class);

    public enum Type {
        prefix,
        suffix
    }

    private final Type type;
    private final String content;

    public AppendFilter(final Type type, final String content) {
        this.type = type;
        this.content = content;
    }

    @Override
    public Object apply(final Object source, final Map<String, Object> map) {
        if(!NotEmptyTest.check(source)) {
            return null;
        } else if(type == Type.prefix) {
            return StringUtils.join(this.content, source);
        } else if(type == Type.suffix) {
            return StringUtils.join(source, this.content);
        }
        return null;
    }

    @Override
    public List<String> getArgumentNames() {
        return defaultArgumentNames();
    }
}
