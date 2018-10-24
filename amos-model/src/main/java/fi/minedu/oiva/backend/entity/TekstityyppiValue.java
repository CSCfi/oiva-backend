package fi.minedu.oiva.backend.entity;

import org.jooq.impl.EnumConverter;

public enum TekstityyppiValue {
    KYSYMYS,
    VASTAUS,
    PERUSTELU,
    MUU;

    public static class Converter extends EnumConverter<String, TekstityyppiValue> {
        public Converter() {
            super(String.class, TekstityyppiValue.class);
        }
    }
}
