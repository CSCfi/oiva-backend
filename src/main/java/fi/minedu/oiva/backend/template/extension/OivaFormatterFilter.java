package fi.minedu.oiva.backend.template.extension;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class OivaFormatterFilter extends OivaFilter {

    private static final Logger logger = LoggerFactory.getLogger(OivaFormatterFilter.class);

    public enum Type {
        number,
        capitalize
    }

    private final Type type;

    public OivaFormatterFilter(final Type type) {
        this.type = type;
    }

    @Override
    public Object apply(final Object source, final Map<String, Object> map) {
        final Optional<Locale> localeOpt = getContextScopeLocale(map);

        if(!NotEmptyTest.check(source)) {
            return "";
        } else if(type == Type.number && NumberUtils.isDigits(String.valueOf(source))) {
            final Long value = NumberUtils.toLong(String.valueOf(source));
            if(localeOpt.isPresent()) {
                return NumberFormat.getNumberInstance(localeOpt.get()).format(value);
            }
        } else if(type == Type.capitalize) {
            return StringUtils.capitalize(String.valueOf(source));
        }
        return source;
    }

    @Override
    public List<String> getArgumentNames() {
        return defaultArgumentNames();
    }
}
