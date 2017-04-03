package fi.minedu.oiva.backend.repo;

import fi.minedu.oiva.backend.entity.dto.Henkilo;
import javaslang.collection.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.stereotype.Repository;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

@Repository
public class LdapHenkiloRepository {
    @Autowired
    private LdapTemplate ldapTemplate;

    final static Logger logger = LoggerFactory.getLogger(LdapHenkiloRepository.class);

    private static final Pattern OIVA_ROLE_FORMAT = Pattern.compile("^APP_KOUTE_([A-Za-z]+)(?:_((?:\\d+\\.?)+)?)?$");

    @Cacheable("LdapHenkiloRepository:getHenkiloByOid")
    public Henkilo getHenkiloByOid(String oid) {
        java.util.List<Henkilo> res = ldapTemplate.search(
            query().base("ou=People").where("employeeNumber").is(oid),
            LdapHenkiloRepository::attrsToHenkilo);
        return (res.size() > 0) ? res.get(0) : null;
    }

    public java.util.List<Henkilo> getHenkilosByOids(String[] oids) {
        return List.of(oids).map(this::getHenkiloByOid).toJavaList();
    }

    private static Henkilo attrsToHenkilo(Attributes attrs) {
        Henkilo henkilo = new Henkilo();
        henkilo.setOid(asString(attrs, "employeenumber"));
        henkilo.setSukunimi(asString(attrs, "sn"));
        henkilo.setEtunimi(asString(attrs, "cn"));
        henkilo.setRoles(asList(attrs, "description"));
        henkilo.setUsername(asString(attrs, "uid"));

        henkilo.setOrganization(findHomeOid(henkilo.getRoles()).orElse(""));

        return henkilo;
    }

    static Optional<String> findHomeOid(java.util.List<String> roles) {
        return roles.stream()
            .map(OIVA_ROLE_FORMAT::matcher)
            .filter(Matcher::matches)
            .filter(m -> StringUtils.isNotEmpty(m.group(2)))
            .map(m -> m.group(2))
            .findFirst();
    }

    @SuppressWarnings("unchecked")
    private static java.util.List<String> asList(Attributes attrs, String key) {
        return Arrays.asList(asString(attrs, key)
            .split(", "))
            .stream()
            .map(x -> x.replaceAll("[^\\dA-Za-z_\\.]", ""))
            .collect(Collectors.toList());
    }

    private static String asString(Attributes attrs, String key) {
        try {
            Attribute a = attrs.get(key);
            if (a != null) {
                return a.get().toString();
            } else {
                return "";
            }
        } catch (NamingException e) {
            if (logger.isDebugEnabled()) logger.debug("LDAP error", e);
            return "";
        }
    }
}
