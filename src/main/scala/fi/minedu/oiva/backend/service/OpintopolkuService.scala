package fi.minedu.oiva.backend.service

import java.util.concurrent.CompletionStage
import javax.annotation.PostConstruct

import fi.minedu.oiva.backend.cache.CacheAware
import fi.minedu.oiva.backend.cas.CASClient
import fi.minedu.oiva.backend.entity.Maarays
import fi.minedu.oiva.backend.entity.dto.OrganisaatioInfo
import fi.minedu.oiva.backend.entity.json.ObjectMapperSingleton
import fi.minedu.oiva.backend.entity.opintopolku._

import scala.collection.JavaConversions._
import com.fasterxml.jackson.core.`type`.TypeReference
import dispatch.Defaults._
import dispatch._
import org.glassfish.jersey.client.rx.RxClient
import org.glassfish.jersey.client.rx.java8.RxCompletionStageInvoker
import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.{Scope, ScopedProxyMode}
import org.springframework.stereotype.Service

import scala.collection.JavaConverters._
import scala.compat.java8.FutureConverters

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON, proxyMode = ScopedProxyMode.TARGET_CLASS)
class OpintopolkuService extends CacheAware {

    @Value("${opintopolku.baseUrl}${opintopolku.organisaatio.restUrl}")
    private val organisaatioServiceUrl: String = null
    private lazy val organisaatioQueryServiceUrl: String = organisaatioServiceUrl + "/%s?includeImage=false"
    private lazy val koulutustoimijatServiceUrl: String = organisaatioServiceUrl + "/v2/hae?organisaatioTyyppi=Koulutustoimija&aktiiviset=true&suunnitellut=true&lakkautetut=true"

    @Value("${opintopolku.baseUrl}${opintopolku.koodisto.restUrl}")
    private val koodistoServiceUrl: String = null

    // Koodisto relaatiot
    private lazy val relaatioAlakoodiUrl: String = koodistoServiceUrl + "/relaatio/sisaltyy-alakoodit/"
    private lazy val relaatioYlakoodiUrl: String = koodistoServiceUrl + "/relaatio/sisaltyy-ylakoodit/"

    // Koodisto koodit
    private lazy val alueHallintovirastoKoodiUrl: String = koodistoServiceUrl + "/aluehallintovirasto/koodi/"
    private lazy val maakuntaKoodiUrl: String = koodistoServiceUrl + "/maakunta/koodi/"
    private lazy val kuntaKoodiUrl: String = koodistoServiceUrl + "/kunta/koodi/"
    private lazy val koulutusAlaOph2002KoodiUrl: String = koodistoServiceUrl + "/koulutusalaoph2002/koodi/"
    private lazy val koulutusKoodiUrl: String = koodistoServiceUrl + "/koulutus/koodi/"
    private lazy val koulutusOsaamisalaKoodiUrl: String = koodistoServiceUrl + "/osaamisala/koodi/"
    private lazy val kieliKoodiUrl: String = koodistoServiceUrl + "/kieli/koodi/"
    private lazy val oppilaitoksenOpetuskieliKoodiUrl: String = koodistoServiceUrl + "/oppilaitoksenopetuskieli/koodi"

    def koodistoKoodiUrl(koodistoUri: String) = koodistoServiceUrl + s"/${koodistoUri}/koodi/"
    def koodiUri(koodistoUri: String, koodiArvo: String): String = s"${koodistoUri}_${koodiArvo}"

    // Koodi urit
    def aluehallintovirastoKoodiUri(koodiArvo: String) = s"aluehallintovirasto_${koodiArvo}"
    def maakuntaKoodiUri(koodiArvo: String) = s"maakunta_${koodiArvo}"
    def kuntaKoodiUri(koodiArvo: String) = s"kunta_${koodiArvo}"
    def koulutusAlaOph2002KoodiUri(koodiArvo: String) = s"koulutusalaoph2002_${koodiArvo}"
    def koulutustyyppiKoodiUri(koodiArvo: String) = s"koulutustyyppi_${koodiArvo}"
    def koulutusKoodiUri(koodiArvo: String) = s"koulutus_${koodiArvo}"
    def osaamisalaKoodiUri(koodiArvo: String) = s"osaamisala_${koodiArvo}"
    def kieliKoodiUri(koodiArvo: String) = s"kieli_${koodiArvo}"

    @Value("${opintopolku.baseUrl}${opintopolku.autentikaatio.restUrl}")
    private val authenticationServiceUrl: String = null
    private lazy val securityCheckUrl: String = authenticationServiceUrl + "/j_spring_cas_security_check"

    @Value("${opintopolku.baseUrl}${opintopolku.henkilo.restUrl}")
    private val henkiloServiceUrl: String = null
    private lazy val organisaatiohenkiloUrl: String = henkiloServiceUrl + "/%s/organisaatiohenkilo"

    @Value("${opintopolku.apiCredentials.username}")
    private val opintopolkuApiUsername: String = null

    @Value("${opintopolku.apiCredentials.password}")
    private val opintopolkuApiPassword: String = null

    @Autowired
    private val casClient: CASClient = null

    @Autowired
    private val rxClient: RxClient[RxCompletionStageInvoker] = null

    @PostConstruct
    def postConstruct() {
        setCache("OpintopolkuService")
    }

    /**
     * Fetches organization raw string data from Opintopolku
     *
     * NOTE! OID can also be Y-tunnus!
     *
     * @param oid oid of org
     * @return Future of raw string
     */
    def getOrganisaatioAsRawScalaFuture(oid: String) = cache(oid) {
        Http(url(organisaatioQueryServiceUrl.format(oid)) OK as.String)
    }

    def getOrganisaatio(oid: String) = {
        val url = organisaatioQueryServiceUrl.format(oid)
        cacheRx(oid) {
            requestRx(url, classOf[Organisaatio])
        }
    }

    def getOrganisaatioAsRaw(oid: String) = {
        val url = organisaatioQueryServiceUrl.format(oid)
        cacheRx(url) {
            requestRx(url, classOf[String])
        }
    }

    def requestRx[T](url: String, clazz: Class[T]) = rxClient.target(url).request().rx().get(clazz)
    def request[T](url: String, clazz: Class[T]) = rxClient.target(url).request().get(clazz)
    def toJson[T](str: String, clazz: Class[T]) = ObjectMapperSingleton.mapper.readValue(str, clazz)

    /**
     * Maps raw JSON string to Organisaatio entity
     * @param oid oid of org
     * @return entity
     */
    def getOrganisaatioAsScalaFuture(oid: String) =
        for (orgStr <- getOrganisaatioAsRawScalaFuture(oid)) yield toJson(orgStr, classOf[Organisaatio])

    def getBlockingOrganisaatio(oid: String) = getOrganisaatio(oid).toCompletableFuture.join()

    def getOrganisaatiosAsCSString(oids: Array[String]): CompletionStage[String] =
        Future.sequence(oids.distinct.toList.map(oid => getOrganisaatioAsRawScalaFuture(oid))).collect {
            case x: List[String] => x.mkString("[", ",", "]")
        }

    /**
     * Get by list of oids
     */
    def getOrganisaatiosAsScalaFuture(oids: Array[String]): Future[List[Organisaatio]] =
        Future.sequence(oids.distinct.toList.map(getOrganisaatioAsScalaFuture))

    /**
     * Fetches person's organizational data from Opintopolku
     *
     * @param oid
     * @return Organisaatiohenkilo data
     */
    def getOrganisaatiohenkilo(oid: String): java.util.List[OrganisaatioInfo] = {
        val strResp = casClient.getTicket(securityCheckUrl, opintopolkuApiUsername, opintopolkuApiPassword)
            .flatMap {
                ticket => Http(url(organisaatiohenkiloUrl.format(oid)).GET.addQueryParameter("ticket", ticket) OK as.String)
            }.apply()
        ObjectMapperSingleton.mapper.readValue(strResp, new TypeReference[java.util.List[OrganisaatioInfo]](){})
    }

    implicit def future2CS[T](future: Future[T]): CompletionStage[T] = FutureConverters.toJava(future)

    /**
     * Hakee ja palauttaa maakunnat ja maakuntaan kuuluvat kaikki kunnat
     */
    def getMaakuntaKunnat: java.util.List[Maakunta] =
        for (maakunta <- request(maakuntaKoodiUrl, classOf[Array[Maakunta]]).toList) yield {
            maakunta.setKunta(request(relaatioYlakoodiUrl + maakuntaKoodiUri(maakunta.koodiArvo), classOf[Array[Kunta]]))
            maakunta
        }

    /**
     * Hakee ja palauttaa kaikki koulutustoimijat
     */
    def getKoulutustoimijat: java.util.List[Organisaatio] =
        request(koulutustoimijatServiceUrl, classOf[OrganisaatioHits]).organisaatiot.toList

    /**
     * Muodostaa maakunta-jarjestajat tietueet
     */
    def getMaakuntaJarjestajat: java.util.List[Maakunta] = {
        val koulutustoimijat = getKoulutustoimijat
        for(maakunta <- getMaakuntaKunnat) yield {
            maakunta.setKunta(for(kunta <- maakunta.getKunnat) yield {
                val jarjestajat = for(koulutustoimija <- koulutustoimijat if koulutustoimija.isKunta(kunta)) yield Jarjestaja(koulutustoimija)
                if(!jarjestajat.isEmpty) kunta.setJarjestaja(jarjestajat.toArray)
                kunta
            })
            maakunta
        }
    }

    def getKoodi(maarays: Maarays): KoodistoKoodi = getKoodi(maarays.getKoodisto, maarays.getKoodiarvo)
    def getKoodi(koodistoUri: String, koodiArvo: String) = getKoodistoKoodiBlocking(koodistoKoodiUrl(koodistoUri), koodiUri(koodistoUri, koodiArvo))

    def getAlueHallintovirastoKoodit = getKoodistoKooditList(alueHallintovirastoKoodiUrl)

    def getMaakuntaKoodit = getKoodistoKooditList(maakuntaKoodiUrl)

    def getKunnatKoodit = getKoodistoKooditList(kuntaKoodiUrl)
    def getKuntaKoodi(koodiArvo: String) = getKoodistoKoodiBlocking(kuntaKoodiUrl, kuntaKoodiUri(koodiArvo))
    def getKuntaKooditForAlueHallintovirasto(koodiArvo: String) = getKoodistoKooditList(relaatioAlakoodiUrl + aluehallintovirastoKoodiUri(koodiArvo))
    def getKuntaKooditForMaakunta(koodiArvo: String) = getKoodistoKooditList(relaatioYlakoodiUrl + maakuntaKoodiUri(koodiArvo))

    def getKieliKoodit = getKoodistoKooditList(kieliKoodiUrl)
    def getKieliKoodi(koodiArvo: String) = getKoodistoKoodiBlocking(kieliKoodiUrl, kieliKoodiUri(koodiArvo.toLowerCase))

    def getOppilaitoksenOpetuskieliKoodit = getKoodistoKooditList(oppilaitoksenOpetuskieliKoodiUrl)

    def getKoulutustyyppiKoodiForKoulutus(koodiArvo: String) = {
        val koulutustyyppiKoodit = getKoulutusAlaKoodit(koodiArvo).filter(_.isKoodisto("koulutustyyppi"))
        if(!koulutustyyppiKoodit.isEmpty) koulutustyyppiKoodit.head else null
    }
    def getKoulutusAlaKoodit(koodiArvo: String) = getKoodistoKooditList(relaatioAlakoodiUrl + koulutusKoodiUri(koodiArvo))

//    def getKoulutusKooditForKoulutustyyppi = getKoodistoKooditList(relaatioYlakoodiUrl + koulutustyyppiKoodiUri("1"), true)
//    def getKoulutusKoodi(koodiArvo: String) = getKoodistoKoodiBlocking(koulutusKoodiUrl, koulutusKoodiUri(koodiArvo))
//    def getKoulutusAlaKoodit(koodiArvo: String) = getKoodistoKooditList(relaatioAlakoodiUrl + koulutusKoodiUri(koodiArvo), true)
//    def getKoulutusAlaOph2002Koodisto(koodiArvo: String) = getKoodistoKoodiBlocking(koulutusAlaOph2002KoodiUrl, koulutusAlaOph2002KoodiUri(koodiArvo))
//    def getKoulutusOsaamisalaKoodisto(koodiArvo: String) = getKoodistoKoodiBlocking(koulutusOsaamisalaKoodiUrl, osaamisalaKoodiUri(koodiArvo))

    private def getKoodistoKooditList(koodistoKoodiUrl: String, includeExpired: Boolean = false): java.util.List[KoodistoKoodi] =
        getKoodistoKooditBlocking(koodistoKoodiUrl).toList.filter(koodi => includeExpired || koodi.isValidDate).distinct.asJava

    private def getKoodistoKooditBlocking(koodistoKoodiUrl: String): Array[KoodistoKoodi] =
        try requestKoodistoKoodit(koodistoKoodiUrl).toCompletableFuture.join
        catch { case e: Exception => Array.empty }

    private def getKoodistoKoodiBlocking(koodistoUrl: String, koodiUri: String): KoodistoKoodi =
        try requestKoodistoKoodi(koodistoUrl, koodiUri).toCompletableFuture.join
        catch { case e: Exception => KoodistoKoodi.notFound(koodiUri) }

    private def requestKoodistoKoodit(koodistoKoodiUrl: String) =
        cacheRx(koodistoKoodiUrl) { requestRx(koodistoKoodiUrl, classOf[Array[KoodistoKoodi]]) }

    private def requestKoodistoKoodi(koodistoKoodiUrl: String, koodiUri: String) =
        cacheRx(koodiUri) { requestRx(koodistoKoodiUrl + koodiUri, classOf[KoodistoKoodi]) }
}
