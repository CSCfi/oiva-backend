package fi.minedu.oiva.backend.core.spring.context.annotation;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Profile;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.MultiValueMap;

import java.util.HashSet;
import java.util.Set;

/**
 * As of writing @Profile does not support AND operation with profiles.
 *
 * Hack is quickly grabbed from: https://jira.spring.io/browse/SPR-12458
 */
public class AndProfileCondition implements Condition {

    public static final String VALUE = "value";
    public static final String DEFAULT_PROFILE = "default";

    @Override
    public boolean matches(final ConditionContext context, final AnnotatedTypeMetadata metadata) {
        if (context.getEnvironment() == null) {
            return true;
        }
        final MultiValueMap<String, Object> attrs = metadata.getAllAnnotationAttributes(Profile.class.getName());
        if (attrs == null) {
            return true;
        }
        final String[] activeProfiles = context.getEnvironment().getActiveProfiles();
        final String[] definedProfiles = (String[]) attrs.getFirst(VALUE);
        final Set<String> allowedProfiles = new HashSet<>(1);
        final Set<String> restrictedProfiles = new HashSet<>(1);
        for (String nextDefinedProfile : definedProfiles) {
            if (!nextDefinedProfile.isEmpty() && nextDefinedProfile.charAt(0) == '!') {
                restrictedProfiles.add(nextDefinedProfile.substring(1, nextDefinedProfile.length()));
                continue;
            }
            allowedProfiles.add(nextDefinedProfile);
        }

        for (String nextActiveProfile : activeProfiles) {
            if (DEFAULT_PROFILE.equals(nextActiveProfile) && allowedProfiles.isEmpty()) {
                continue;
            }
            if (!allowedProfiles.contains(nextActiveProfile) || restrictedProfiles.contains(nextActiveProfile) || allowedProfiles.size() != activeProfiles.length) {
                return false;
            }
        }
        return true;
    }
}
