package fi.minedu.oiva.backend.service

import fi.minedu.oiva.backend.BaseSuite
import org.mockito.Matchers._
import org.mockito.Mockito._

class OpintopolkuServiceSpec extends BaseSuite {

  test("Get user privileges from opintopolku") {
    val username = "oiva-essi"
    val service = mock[OpintopolkuService]
    when(service.getKayttajaKayttooikeus(matches(username))).thenCallRealMethod()
    when(service.requestWithCasTicket(anyString())(anyObject())).thenReturn(
      readResource("/kayttajakayttooikeus.json").replace("@username@", username)
    )
    val optional = service.getKayttajaKayttooikeus(username)
    assert(optional.isPresent, "result should not be empty")
    val result = optional.get()
    assertResult(2) {
      result.organisaatiot.size()
    }
  }

}
