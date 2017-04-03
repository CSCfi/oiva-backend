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
 * {@literal @}PreAuthorize("hasPermission(#hakemusId, 'Hakemus', 'APP_KOUTE_ESITTELIJA')")
 * void someMethod(Long hakemusId) { /* something... *&#47; }
 * // or with id, type and explicit oid extraction with oidsFor
 * {@literal @}PreAuthorize("hasPermission(#hakemusId, 'Hakemus', oidsFor('APP_KOUTE_ESITTELIJA'))")
 * </pre>
 * </p>
 * <p>
 * Other way to say same thing:
 * <pre>
 * {@literal @}PreAuthorize("@hakemusService.hasOid(#hakemusOsio.hakemusId, oidsFor('APP_KOUTE_ESITTELIJA'))")
 * </pre>
 * </p>
 */
@Component
public class OivaPermissionChecker implements PermissionEvaluator, ApplicationContextAware {

    ApplicationContext applicationContext;

    /**
     * Autowiring of services breaks evaluator for some reasons too busy to find out,
     * so this is for the win and fix. Ugly, yes. Then getBean() is used to resolve services
     * from context.
     *
     * @param applicationContext
     * @throws BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
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
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return Matching
            .whenIsType((String s) -> SecurityUtil.roleOIDs(authentication, s))
            .whenAllMatch(instanceOf(List.class), hasItem(instanceOf(String.class))).thenApply(ignr -> (List<String>) permission)
            .match(permission).map(oids -> Matching
                    .whenIsValue("Hakemus")
                        .thenApply(ignr -> checkOidsOnCheckable("hakemusService", targetId, oids))
                    .whenIsValue("Paatos")
                        .thenApply(ignr -> checkOidsOnCheckable("paatosService", targetId, oids))
                    .whenIsValue("Koulutustehtava")
                        .thenApply(ignr -> checkOidsOnCheckable("koulutustehtavaService", targetId, oids))
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
    private boolean checkOidsOnCheckable(String beanName, Serializable targetId, List<String> oids) {
        WithOidCheck hakemusService = (WithOidCheck) applicationContext.getBean(beanName);
        return hakemusService.hasOid((Long) targetId, oids);
    }
}
