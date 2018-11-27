package fi.minedu.oiva.backend.spring.resolver;

import fi.minedu.oiva.backend.security.SecurityUtil;
import fi.minedu.oiva.backend.spring.annotation.OIDs;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Arrays;
import java.util.List;

public class OIDArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        return parameter.hasParameterAnnotation(OIDs.class) && List.class.equals(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(final MethodParameter parameter, final ModelAndViewContainer mavContainer,
        final NativeWebRequest webRequest, final WebDataBinderFactory binderFactory) throws Exception {
        return SecurityUtil.roleOids(Arrays.asList(parameter.getParameterAnnotation(OIDs.class).value()));
    }
}
