package fi.minedu.oiva.backend.core.service

import fi.minedu.oiva.backend.BaseSuite
import fi.minedu.oiva.backend.model.security.annotations.OivaAccess
import org.mockito.Matchers._
import org.mockito.Mockito._
import scala.collection.JavaConverters._

class OpintopolkuServiceSpec extends BaseSuite {

  test("Get user privileges from opintopolku") {
    val username = "test-user"
    val service: OpintopolkuService = mockOpintoPolku(username)
    val optional = service.getKayttajaKayttooikeus(username)
    assert(optional.isPresent, "result should not be empty")
    val result = optional.get()
    assertResult(3) {
      result.organisaatiot.size()
    }
  }

  test("Get user organisation oid from opintopolku and check organisation permissions") {
    val username = "test-user"
    val service: OpintopolkuService = mockOpintoPolku(username)
    val optional = service.getKayttajaKayttooikeus(username)
    assert(optional.isPresent, "result should not be empty")
    val result = optional.get()
    assert(result.getOivaOrganisaatioOid.isPresent, "There should be organisation oids")
    val oid = result.getOivaOrganisaatioOid.get()
    assertResult("1.2.1.2") {oid}
    val oikeudet = result.getOivaOikeudet(oid).get()
    assertResult(Seq(OivaAccess.Role_Application, OivaAccess.Role_Esittelija, OivaAccess.Role_Nimenkirjoittaja)) {
      oikeudet.asScala
    }
  }

  private def mockOpintoPolku(username: String) = {
    val service = mock[OpintopolkuService]
    when(service.getKayttajaKayttooikeus(matches(username))).thenCallRealMethod()
    when(service.requestWithCasTicket(anyString())(anyObject())).thenReturn(
      readResource("/kayttajakayttooikeus.json").replace("@username@", username)
    )
    service
  }
}
