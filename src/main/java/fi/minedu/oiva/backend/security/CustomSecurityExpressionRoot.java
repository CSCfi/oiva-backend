package fi.minedu.oiva.backend.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;

import java.util.List;

public class CustomSecurityExpressionRoot extends SecurityExpressionRoot implements MethodSecurityExpressionOperations {
    private Object filterObject;
    private Object returnObject;
    private Object target;

    private Logger log = LoggerFactory.getLogger(CustomSecurityExpressionRoot.class);

    CustomSecurityExpressionRoot(Authentication a) {
        super(a);
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
     * Returns every OID and possible administration privilege of given role prefix.
     *
     * @param partialRole Role prefix
     * @return list of OIDs for role prefix
     */
    public List<String> oidsFor(String partialRole) {
        return SecurityUtil.roleOIDs(partialRole);
    }

    /**
     * Multi role prefix version of oidsFor
     * @param partialRoles Role prefixes
     * @return list of OIDs for role prefix
     */
    public List<String> oidsFor(List<String> partialRoles) {
        return SecurityUtil.roleOIDs(partialRoles);
    }

    /**
     * just convenience method to see if user is really authenticated (= signed in)
     */
    public boolean isSignedIn() {
        if (log.isDebugEnabled()) log.debug("check if signed in: " + !this.isAnonymous());
        return !this.isAnonymous();
    }
}
