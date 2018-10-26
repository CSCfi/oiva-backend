package fi.minedu.oiva.backend.entity;

import org.jooq.impl.EnumConverter;

public enum AsiatyyppiValue {
    UUSI,
    MUUTOS,
    AK,
    LUKIO,
    PERUUTUS;

    public static class Converter extends EnumConverter<String, AsiatyyppiValue> {
        public Converter() {
            super(String.class, AsiatyyppiValue.class);
        }
    }
}
