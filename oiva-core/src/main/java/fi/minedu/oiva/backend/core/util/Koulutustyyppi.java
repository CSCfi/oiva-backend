package fi.minedu.oiva.backend.core.util;

public enum Koulutustyyppi {
    ESI_JA_PERUSOPETUS("1"),
    LUKIO("2"),
    VAPAASIVISTYSTYO("3");

    private String value;

    Koulutustyyppi(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
