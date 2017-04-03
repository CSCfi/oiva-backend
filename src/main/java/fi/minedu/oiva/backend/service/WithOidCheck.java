package fi.minedu.oiva.backend.service;

import fi.minedu.oiva.backend.spring.annotation.OIDs;

import java.util.List;
import java.util.Optional;

/**
 * Generic OID checker functionality
 *
 * Returns true if entity of interest is tied to one certain OID
 * found on list of given role OIDs.
 *
 * If role has OIDless administrator form (e.g.
 * APP_HENKILONHALLINTA_CRUD), true is always returned.
 */
public abstract class WithOidCheck {
    public boolean hasOid(Long id, List<String> oids) {
        if (oids.contains(OIDs.ADMINISTRATOR)) {
            return true;
        } else {
            return getOid(id).map(oids::contains).orElse(false);
        }
    }

    abstract Optional<String> getOid(Long id);
}
