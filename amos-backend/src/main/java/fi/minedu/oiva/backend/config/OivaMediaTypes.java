package fi.minedu.oiva.backend.config;

import org.springframework.http.MediaType;

public class OivaMediaTypes {

    public static final String APPLICATION_VND_JSON_VALUE = "application/vnd.minedu.oiva-1.0+json";
    public static final MediaType APPLICATION_VND_JSON = MediaType.valueOf(APPLICATION_VND_JSON_VALUE);

    public static final String APPLICATION_PDF_VALUE = "application/pdf";
    public static final MediaType APPLICATION_PDF = MediaType.valueOf(APPLICATION_VND_JSON_VALUE);
}

// from backend-config