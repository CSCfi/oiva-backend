package fi.minedu.oiva.backend.entity;

import org.jooq.impl.EnumConverter;

public enum LupatilaValue {
    LUONNOS,
    PASSIVOITU,
    VALMIS,
    HYLATTY;

    public static class Converter extends EnumConverter<String, LupatilaValue> {
        public Converter() {
            super(String.class, LupatilaValue.class);
        }
    }
}
