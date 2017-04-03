package fi.minedu.oiva.backend.entity.opintopolku

import scala.beans.BeanProperty

case class ReportingValosDTO(
    @BeanProperty var jarjestaja: String,
    @BeanProperty var ytunnus: String,
    @BeanProperty var kotipaikka: String,
    @BeanProperty var vuosiopiskelijamaara_kuluvav: Int,
    @BeanProperty var vuosiopiskelijamaara_tulevav: Int,
    @BeanProperty var koulutusala1: Boolean,
    @BeanProperty var koulutusala2: Boolean,
    @BeanProperty var koulutusala3: Boolean,
    @BeanProperty var koulutusala4: Boolean,
    @BeanProperty var koulutusala5: Boolean,
    @BeanProperty var koulutusala6: Boolean,
    @BeanProperty var koulutusala7: Boolean,
    @BeanProperty var koulutusala8: Boolean,
    @BeanProperty var valma: Boolean,
    @BeanProperty var erit_tutkinto: Boolean,
    @BeanProperty var erit_valma: Boolean,
    @BeanProperty var erit_telma: Boolean,
    @BeanProperty var erit_vuosiopiskelijamaara_kuluvav: Int,
    @BeanProperty var erit_vuosiopiskelijamaara_tulevav: Int,
    @BeanProperty var sisaoppilaitos: Boolean,
    @BeanProperty var sisaoppilaitos_vuosiopiskelijamaara_kuluvav: Int,
    @BeanProperty var sisaoppilaitos_vuosiopiskelijamaara_tulevav: Int ) {
}
