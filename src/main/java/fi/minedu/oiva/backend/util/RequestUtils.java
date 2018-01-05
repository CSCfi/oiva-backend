package fi.minedu.oiva.backend.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;

public class RequestUtils {

    public static String getPathVariable(final HttpServletRequest request, final String basePath) {
        final String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        final String bestMatchingPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        final String arguments = new AntPathMatcher().extractPathWithinPattern(bestMatchingPattern, path);
        return basePath + (StringUtils.isNotBlank(arguments) ? "/" + arguments : "");
    }
}
