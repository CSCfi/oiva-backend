package fi.minedu.oiva.backend.service;

import fi.minedu.oiva.backend.security.SecurityUtil;

import java.util.Optional;

/**
 * Generic OID checker functionality
 * <p>
 * Returns true if entity of interest is tied to one certain OID
 * found on list of given role OIDs.
 * <p>
 * If role has OIDless administrator form (e.g.
 * APP_HENKILONHALLINTA_CRUD), true is always returned.
 */
public abstract class WithOidCheck {
    public boolean hasOid(final Long id, final String oid) {
        if (SecurityUtil.isAdmin()) {
            return true;
        } else {
            return getOid(id).map(o -> o.equals(oid)).orElse(false);
        }
    }

    abstract Optional<String> getOid(Long id);
}
