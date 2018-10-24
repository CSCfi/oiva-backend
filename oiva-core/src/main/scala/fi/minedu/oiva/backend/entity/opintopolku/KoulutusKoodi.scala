package fi.minedu.oiva.backend.entity.opintopolku

import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.annotation.{JsonIgnoreProperties, JsonInclude}

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
class KoulutusKoodi(
    var koulutustyyppiKoodiArvo: String,
    var koulutusalaKoodiArvo: String) extends fi.minedu.oiva.backend.entity.opintopolku.KoodistoKoodi  {

    def this() = this(null, null)
    def this(koodistoKoodi: KoodistoKoodi, koulutusalaKoodiArvo: String, koulutustyyppiKoodiArvo: String) = {
        this()
        this.koodiArvo = koodistoKoodi.koodiArvo
        this.koodisto = koodistoKoodi.koodisto
        this.versio = koodistoKoodi.versio
        this.metadata = koodistoKoodi.metadata
        this.voimassaAlkuPvm = koodistoKoodi.voimassaAlkuPvm
        this.voimassaLoppuPvm = koodistoKoodi.voimassaLoppuPvm
        this.koulutusalaKoodiArvo = koulutusalaKoodiArvo
        this.koulutustyyppiKoodiArvo = koulutustyyppiKoodiArvo
    }

    def getKoulutustyyppiKoodiArvo = koulutustyyppiKoodiArvo
    def setKoulutustyyppiKoodiArvo(koulutustyyppiKoodiArvo: String): Unit = this.koulutustyyppiKoodiArvo = koulutustyyppiKoodiArvo

    def getKoulutusalaKoodiArvo = koulutusalaKoodiArvo
    def setKoulutusalaKoodiArvo(koulutusalaKoodiArvo: String): Unit = this.koulutusalaKoodiArvo = koulutusalaKoodiArvo
}