package fi.minedu.oiva.backend.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;

public class RequestUtils {

    private static final Logger logger = LoggerFactory.getLogger(RequestUtils.class);

    public static Long getPathVariableAsLong(final HttpServletRequest request, final String basePath) {
        return Long.parseLong(getPathVariable(request, basePath));
    }

    public static String getPathVariable(final HttpServletRequest request, final String basePath) {
        if(null == basePath) {
            return null;
        } else {
            final String invokeUrl = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
            final int pathStarts = StringUtils.lastIndexOf(invokeUrl, basePath);
            if(pathStarts > -1) {
                return StringUtils.substring(invokeUrl, StringUtils.lastIndexOf(invokeUrl, basePath) + basePath.length());
            } else {
                logger.warn("Failed to get pathVariable from: {}", invokeUrl);
                return null;
            }
        }
    }
}
