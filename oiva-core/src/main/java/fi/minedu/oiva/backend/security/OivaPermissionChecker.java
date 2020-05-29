package fi.minedu.oiva.backend.security;

import com.aol.cyclops.matcher.builders.Matching;
import fi.minedu.oiva.backend.service.WithOidCheck;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.instanceOf;

/**
 * Spring idiomatic permission checker, use with
 * {@link org.springframework.security.access.prepost.PreAuthorize}
 * <p>
 * example:
 * <pre>
 * {@literal @}PreAuthorize("hasPermission(#hakemusId, 'Hakemus', 'OIVA_APP_ESITTELIJA')")
 * void someMethod(Long hakemusId) { /* something... *&#47; }
 * // or with id, type and explicit oid extraction with oidsFor
 * {@literal @}PreAuthorize("hasPermission(#hakemusId, 'Hakemus', oidsFor('OIVA_APP_ESITTELIJA'))")
 * </pre>
 * </p>
 * <p>
 * Other way to say same thing:
 * <pre>
 * {@literal @}PreAuthorize("@hakemusService.hasOid(#hakemusOsio.hakemusId, oidsFor('OIVA_APP_ESITTELIJA'))")
 * </pre>
 * </p>
 */
@Component
public class OivaPermissionChecker implements PermissionEvaluator, ApplicationContextAware {

    private ApplicationContext applicationContext;

    /**
     * Autowiring of services breaks evaluator for some reasons too busy to find out,
     * so this is for the win and fix. Ugly, yes. Then getBean() is used to resolve services
     * from context.
     *
     * @param context
     * @throws BeansException
     */
    @Override
    public void setApplicationContext(final ApplicationContext context) throws BeansException {
        this.applicationContext = context;
    }

    @Override
    public boolean hasPermission(final Authentication authentication, final Object targetDomainObject, final Object permission) {
        throw new UnsupportedOperationException("use hasPermission(Authentication authentication, " +
            "Serializable targetId, String targetType, Object permission)");
    }

    /**
     * Checks if oids are valid for given business object, called from SpEL of @PreAuthorize
     *
     * Note: if you need to match against multiple role prefixes this not supporting that yet,
     * use: oidsFor({'ROLE1', 'ROLE2'}) in SpEL of @PreAuthorize.
     *
     * TODO: type safe target typing? Problem: SpEL usage of constants is verbose:
     * #{T(OivaPermissionChecker).HAKEMUS}
     *
     * @param authentication auth data, ignored if permission includes needed data
     * @param targetId       business object id
     * @param targetType     business object type
     * @param permission     list of oids for business objects that current authentication can modify
     *                       OR just role prefix to use to get oids
     * @return true if the permission is granted, false otherwise
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean hasPermission(final Authentication authentication, final Serializable targetId, final String targetType, final Object permission) { // TODO: NOT USED -- DO WE NEED THIS?
        return Matching.whenIsType((String s) -> SecurityUtil.roleOids(authentication, s))
            .whenAllMatch(instanceOf(List.class), hasItem(instanceOf(String.class))).thenApply(ignore -> (List<String>) permission)
            .match(permission).map(oids -> Matching
                .whenIsValue("Hakemus").thenApply(ignr -> checkOidsOnCheckable("hakemusService", targetId, oids))
                .whenIsValue("Paatos").thenApply(ignr -> checkOidsOnCheckable("paatosService", targetId, oids))
                .whenIsValue("Koulutustehtava").thenApply(ignr -> checkOidsOnCheckable("koulutustehtavaService", targetId, oids))
                .match(targetType).orElse(false)
            ).orElse(false);
    }

    /**
     * Does actual matching of oids against WithOidCheck-aware business service
     *
     * @param beanName
     * @param targetId
     * @return
     */
    private boolean checkOidsOnCheckable(final String beanName, Serializable targetId, final List<String> oids) {
        final WithOidCheck hakemusService = (WithOidCheck) applicationContext.getBean(beanName);
        return hakemusService.hasOid((Long) targetId, oids);
    }
}