package fi.minedu.oiva.backend.model.entity;

import org.jooq.impl.EnumConverter;

public enum MaaraystyyppiValue {
    OIKEUS,
    RAJOITE,
    VELVOITE,
    POIKKEUS;

    public static class Converter extends EnumConverter<String, MaaraystyyppiValue> {
        public Converter() {
            super(String.class, MaaraystyyppiValue.class);
        }
    }
}
