package fi.minedu.oiva.backend.core.security

import java.util.{Collections, Optional}

import fi.minedu.oiva.backend.BaseSuite
import fi.minedu.oiva.backend.core.service.OpintopolkuService
import fi.minedu.oiva.backend.model.entity.opintopolku.{KayttajaKayttooikeus, Kayttooikeus, OrganisaatioKayttooikeus}
import fi.minedu.oiva.backend.model.security.annotations.OivaAccess
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.session.SessionRegistry

import scala.collection.JavaConverters._
import scala.collection.mutable.ArrayBuffer

class OivaUserDetailsServiceTest extends BaseSuite {

  test("Load user details by username") {
    val username = "test"
    val orgOid = "1.1.1.1111"
    val mockOpintopolku = mockOpintopolkuService(username, orgOid, OivaAccess.Context_Esittelija)
    val mockRegistry = mock[SessionRegistry]

    val service = new OivaUserDetailsService(mockOpintopolku, mockRegistry)
    val user = service.loadUserByUsername(username)
    assert(user != null, "User should not be null")
    assert(user.isInstanceOf[OivaUserDetails], "User must be instance of OivaUserDetails")
    val oivaUser = user.asInstanceOf[OivaUserDetails]
    assertResult(username) {
      oivaUser.getUsername
    }
    assertResult(orgOid) {
      oivaUser.getOrganisationOid
    }
    assertResult(Seq(OivaAccess.Role_Application, OivaAccess.Role_Esittelija)) {
      oivaUser.getAuthorities.asScala.map(a => a.getAuthority)
    }
  }


  test("Decrease role when same organization has already logged in user with role 'Kayttaja' or 'Nimenkirjoittaja'") {
    val username = "test"
    val orgOid = "1.1.1.1111"
    val mockOpintopolku = mockOpintopolkuService(username, orgOid, OivaAccess.Context_Nimenkirjoittaja)
    val mockRegistry = mock[SessionRegistry]

    val list = Collections.singletonList(new OivaUserDetails("another_test", "",
      Collections.singleton(new SimpleGrantedAuthority(OivaAccess.Role_Kayttaja)), orgOid, false)
      .asInstanceOf[Object])
    when(mockRegistry.getAllPrincipals).thenReturn(list)

    val service = new OivaUserDetailsService(mockOpintopolku, mockRegistry)
    val user = service.loadUserByUsername(username)
    assert(user != null, "User should not be null")
    assert(user.isInstanceOf[OivaUserDetails], "User must be instance of OivaUserDetails")
    val oivaUser = user.asInstanceOf[OivaUserDetails]
    assertResult(username) {
      oivaUser.getUsername
    }
    assertResult(orgOid) {
      oivaUser.getOrganisationOid
    }
    // Role should be decreased to Katselija
    assertResult(Seq(OivaAccess.Role_Application, OivaAccess.Role_Katselija)) {
      oivaUser.getAuthorities.asScala.map(a => a.getAuthority)
    }
    assert(oivaUser.isPermissionsDecreased, "Permission decreased flag should be true")
  }

  test("Allow multiple users with 'Esittelija'-role logged in without decreasing permission.") {
    val username = "test"
    val orgOid = "1.1.1.1111"
    val mockOpintopolku = mockOpintopolkuService(username, orgOid, OivaAccess.Context_Esittelija)
    val mockRegistry = mock[SessionRegistry]

    val list = Collections.singletonList(new OivaUserDetails("another_test", "",
      Collections.singleton(new SimpleGrantedAuthority(OivaAccess.Role_Esittelija)), orgOid, false)
      .asInstanceOf[Object])
    when(mockRegistry.getAllPrincipals).thenReturn(
      list)

    val service = new OivaUserDetailsService(mockOpintopolku, mockRegistry)
    val user = service.loadUserByUsername(username)
    assert(user != null, "User should not be null")
    assert(user.isInstanceOf[OivaUserDetails], "User must be instance of OivaUserDetails")
    val oivaUser = user.asInstanceOf[OivaUserDetails]
    assertResult(username) {
      oivaUser.getUsername
    }
    assertResult(orgOid) {
      oivaUser.getOrganisationOid
    }

    assertResult(Seq(OivaAccess.Role_Application, OivaAccess.Role_Esittelija)) {
      oivaUser.getAuthorities.asScala.map(a => a.getAuthority)
    }
    assert(!oivaUser.isPermissionsDecreased, "Permission decreased flag should be false")
  }

  test("Case when organisation has already _same_ user logged in.") {
    val username = "test"
    val orgOid = "1.1.1.1111"
    val mockOpintopolku = mockOpintopolkuService(username, orgOid, OivaAccess.Context_Esittelija)
    val mockRegistry = mock[SessionRegistry]

    val list = List(new OivaUserDetails(username, "",
      Collections.singleton(new SimpleGrantedAuthority(OivaAccess.Role_Esittelija)), orgOid, false)
      .asInstanceOf[AnyRef],
      new OivaUserDetails(username + "_another", "",
        Collections.singleton(new SimpleGrantedAuthority(OivaAccess.Role_Katselija)), orgOid, false)
        .asInstanceOf[AnyRef])

    when(mockRegistry.getAllPrincipals).thenReturn(list.asJava)

    val service = new OivaUserDetailsService(mockOpintopolku, mockRegistry)
    val oivaUser = service.loadUserByUsername(username).asInstanceOf[OivaUserDetails]
    // Role should not be decreased
    assertResult(Seq(OivaAccess.Role_Application, OivaAccess.Role_Esittelija)) {
      oivaUser.getAuthorities.asScala.map(a => a.getAuthority)
    }
    assert(!oivaUser.isPermissionsDecreased, "Permission decreased flag should be false")
  }

  private def mockOpintopolkuService(username: String, orgOid: String, accessRight: String) = {
    val mockOpintopolku = mock[OpintopolkuService]
    var orgKayttooikeusList: ArrayBuffer[OrganisaatioKayttooikeus] = ArrayBuffer()
    var kayttooikeusList: ArrayBuffer[Kayttooikeus] = ArrayBuffer()
    kayttooikeusList += Kayttooikeus(OivaAccess.Context_Oiva, accessRight)

    orgKayttooikeusList += OrganisaatioKayttooikeus(orgOid, kayttooikeusList.asJava)
    when(mockOpintopolku.getKayttajaKayttooikeus(matches(username)))
      .thenReturn(Optional.of(KayttajaKayttooikeus("1.2.2.2222", orgKayttooikeusList.asJava)))
    mockOpintopolku
  }
}
