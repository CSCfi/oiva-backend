package fi.minedu.oiva.backend.entity.opintopolku

import java.sql.Timestamp

import scala.beans.BeanProperty

case class KoulutustarjontaDTO(
    @BeanProperty var ytunnus: String,
    @BeanProperty var oid: String,
    @BeanProperty var alkupvm: Timestamp,
    @BeanProperty var loppupvm: Timestamp,
    @BeanProperty var paatospvm: Timestamp,
    @BeanProperty var diaarinro: String,
    @BeanProperty @JsonRawValueField var koulutukset: String,
    @BeanProperty @JsonRawValueField var jarjestamiskunnat: String,
    @BeanProperty @JsonRawValueField var opetuskielet: String) {
}
