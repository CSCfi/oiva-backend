package fi.minedu.oiva.backend.security;

import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;

import java.util.List;

public class CustomSecurityExpressionRoot extends SecurityExpressionRoot implements MethodSecurityExpressionOperations {

    private Object filterObject;
    private Object returnObject;
    private Object target;

    CustomSecurityExpressionRoot(final Authentication auth) {
        super(auth);
    }

    public void setFilterObject(Object filterObject) {
        this.filterObject = filterObject;
    }

    public Object getFilterObject() {
        return filterObject;
    }

    public void setReturnObject(Object returnObject) {
        this.returnObject = returnObject;
    }

    public Object getReturnObject() {
        return returnObject;
    }

    void setThis(Object target) {
        this.target = target;
    }

    public Object getThis() {
        return target;
    }

    /**
     * Returns oids related to the given role
     *
     * @param role target role
     * @return list of role related oids
     */
    public List<String> oidsFor(final String role) {
        return SecurityUtil.roleOids(role);
    }

    /**
     * Returns oids related to the given role
     *
     * @param roles Role prefixes
     * @return list of OIDs for role prefix
     */
    public List<String> oidsFor(final List<String> roles) {
        return SecurityUtil.roleOids(roles);
    }
}
