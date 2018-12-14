package fi.minedu.oiva.backend.template.extension;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class MatchComparatorTest extends OivaTest {

    private final Type type;
    private final String argTarget = "targetName";

    public enum Type {
        any
    }

    public MatchComparatorTest(final Type type) {
        this.type = type;
    }

    @Override
    public boolean apply(final Object data, final Map<String, Object> map) {
        if(null == data) {
            return false;
        }
        final String source = String.valueOf(data);
        final Optional<Collection<String>> targetValues = getTargetValues(map);
        if(targetValues.isPresent()) {
            if(type == Type.any) {
                return targetValues.get().stream().anyMatch(target -> StringUtils.equalsIgnoreCase(target, source));
            }
        }
        return false;
    }

    private Optional getTargetValues(final Map<String, Object> map) {
        if(argExists(map, argTarget)) {
            final Object obj = map.get(argTarget);
            return Optional.of((obj instanceof Collection) ? toStringCollection((Collection) obj) : Collections.singletonList(String.valueOf(obj)));
        } else return Optional.empty();
    }

    private Collection<String> toStringCollection(final Collection data) {
        return (Collection<String>) data.stream().map(String::valueOf).collect(Collectors.toList());
    }

    @Override
    public List<String> getArgumentNames() {
        return Arrays.asList(new String[]{ argTarget });
    }
}
