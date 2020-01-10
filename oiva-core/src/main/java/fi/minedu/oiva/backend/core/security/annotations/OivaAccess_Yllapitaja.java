package fi.minedu.oiva.backend.core.security.annotations;

import fi.minedu.oiva.backend.model.security.annotations.OivaAccess;
import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Documented
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize(OivaAccess.Yllapitaja)
public @interface OivaAccess_Yllapitaja {}