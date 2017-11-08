package fi.minedu.oiva.backend.security;

import fi.minedu.oiva.backend.security.annotations.OivaAccess;

import java.util.HashSet;
import java.util.Set;

public class OivaPermission {

    public final OivaAccess.Type type;
    public final Set<String> oids;

    public OivaPermission(final OivaAccess.Type type) {
        this.type = type;
        this.oids = new HashSet<>();
    }

    public boolean is(final OivaAccess.Type type) {
        return this.type == type;
    }
}