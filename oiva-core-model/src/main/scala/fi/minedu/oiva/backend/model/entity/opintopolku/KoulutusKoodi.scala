package fi.minedu.oiva.backend.model.entity.opintopolku

import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.annotation.{JsonIgnoreProperties, JsonInclude}
import scala.beans.BeanProperty

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
class KoulutusKoodi(
    @BeanProperty var koulutustyyppiKoodiArvo: String,
    @BeanProperty var koulutusalaKoodiArvo: String,
    @BeanProperty var osaamisalat: java.util.Set[KoodistoKoodi]) extends KoodistoKoodi  {

    def this() = this(null, null, null)

    def this(koodistoKoodi: KoodistoKoodi, koulutusalaKoodiArvo: String, koulutustyyppiKoodiArvo: String, osaamisalat: java.util.Set[KoodistoKoodi]) = {
        this()
        this.koodiArvo = koodistoKoodi.koodiArvo
        this.koodisto = koodistoKoodi.koodisto
        this.versio = koodistoKoodi.versio
        this.metadata = koodistoKoodi.metadata
        this.voimassaAlkuPvm = koodistoKoodi.voimassaAlkuPvm
        this.voimassaLoppuPvm = koodistoKoodi.voimassaLoppuPvm
        this.koulutusalaKoodiArvo = koulutusalaKoodiArvo
        this.koulutustyyppiKoodiArvo = koulutustyyppiKoodiArvo
        this.osaamisalat = osaamisalat
    }

}