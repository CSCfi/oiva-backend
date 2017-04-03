package fi.minedu.oiva.backend.entity.opintopolku

import java.util.Date

import scala.beans.BeanProperty

case class ReportingMaaraDTO(
    @BeanProperty var ytunnus: String,
    @BeanProperty var paatospvm: Date,
    @BeanProperty var diaarinro: String,
    @BeanProperty var alkupvm: Date,
    @BeanProperty var loppupvm: Date,
    @BeanProperty var kokonaisopiskelijamaara: Int,
    @BeanProperty var kokonaisopiskelijamaara_1: Int,
    @BeanProperty var kokonaisopiskelijamaara_2: Int,
    @BeanProperty var erityisopiskelijamaara: Int,
    @BeanProperty var erityisopiskelijamaara_1: Int,
    @BeanProperty var erityisopiskelijamaara_2: Int,
    @BeanProperty var sisaoppilaitosopiskelijamaara: Int,
    @BeanProperty var sisaoppilaitosopiskelijamaara_1: Int,
    @BeanProperty var sisaoppilaitosopiskelijamaara_2: Int,
    @BeanProperty var opetuskieli: String,
    @BeanProperty var valma: Boolean,
    @BeanProperty var tutkintoonvalmistava_erityinen: Boolean,
    @BeanProperty var valma_erityinen: Boolean,
    @BeanProperty var telma_erityinen: Boolean,
    @BeanProperty var kehittamis_erityinen: Boolean,
    @BeanProperty var urheilija: Boolean,
    @BeanProperty var sisaoppilaitos: Boolean) {
}
