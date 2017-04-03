package fi.minedu.oiva.backend.service

import java.util.concurrent.CompletionStage
import javax.annotation.PostConstruct

import fi.minedu.oiva.backend.cache.CacheAware
import fi.minedu.oiva.backend.cas.CASClient
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
    private lazy val maakunnatKoodistoUrl: String = koodistoServiceUrl + "/maakunta/koodi"
    private lazy val maakuntaKunnatKoodistoUrl: String = koodistoServiceUrl + "/relaatio/sisaltyy-ylakoodit/maakunta_%s"
    private lazy val kunnatKoodistoUrl: String = koodistoServiceUrl + "/kunta/koodi/"
    private lazy val kuntaKoodistoUrl: String = koodistoServiceUrl + "/kunta/koodi/kunta_%s"
    private lazy val kieletKoodistoUrl: String = koodistoServiceUrl + "/kieli/koodi"
    private lazy val kieliKoodistoUrl: String = koodistoServiceUrl + "/kieli/koodi/kieli_%s"
    private lazy val oppilaitoksenOpetuskielietKoodistoUrl: String = koodistoServiceUrl + "/oppilaitoksenopetuskieli/koodi"
    private lazy val eiTutkintoTavottaisetKoodistoUrl: String = koodistoServiceUrl + "/relaatio/sisaltyy-ylakoodit/opintoalaoph2002_998"
    private lazy val alueHallintovirastotKoodistoUrl: String = koodistoServiceUrl + "/aluehallintovirasto/koodi"
    private lazy val alueHallintovirastonKunnatKoodistoUrl: String = koodistoServiceUrl + "/relaatio/sisaltyy-alakoodit/aluehallintovirasto_%s"
    private lazy val koulutustyypitKoodistoUrl: String = koodistoServiceUrl + "/relaatio/sisaltyy-ylakoodit/koulutustyyppi_1"
    private lazy val koulutusKoodistoUrl: String = koodistoServiceUrl + "/koulutus/koodi/koulutus_%s"
    private lazy val koulutusAlaKoodistoUrl: String = koodistoServiceUrl + "/relaatio/sisaltyy-alakoodit/koulutus_%s"
    private lazy val koulutusAlaOph2002KoodistoUrl: String = koodistoServiceUrl + "/koulutusalaoph2002/koodi/koulutusalaoph2002_%s"
    private lazy val koulutusOsaamisalaKoodistoUrl: String = koodistoServiceUrl + "/osaamisala/koodi/osaamisala_%s"
    private lazy val koulutusItemUrl: String = koodistoServiceUrl + "/codeelement/latest/%s_%s"

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
    def getOrganisaatioAsRawScalaFuture(oid: String) = {
        cache(oid) {
            Http(url(organisaatioQueryServiceUrl.format(oid)) OK as.String)
        }
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

    def requestRx[T](url: String, clazz: Class[T]) = {
        rxClient.target(url).request().rx().get(clazz)
    }

    def request[T](url: String, klazz: Class[T]) = {
        rxClient.target(url).request().get(klazz)
    }

    def toJson[T](str: String, klazz: Class[T]) = ObjectMapperSingleton.mapper.readValue(str, klazz)

    /**
     * Maps raw JSON string to Organisaatio entity
     * @param oid oid of org
     * @return entity
     */
    def getOrganisaatioAsScalaFuture(oid: String) =
        for (orgStr <- getOrganisaatioAsRawScalaFuture(oid)) yield toJson(orgStr, classOf[Organisaatio])

    def getBlockingOrganisaatio(oid: String) = getOrganisaatio(oid).toCompletableFuture.join()

    def getOrganisaatiosAsCSString(oids: Array[String]): CompletionStage[String] = {
        Future.sequence(oids.distinct.toList.map(oid => getOrganisaatioAsRawScalaFuture(oid))).collect {
            case x: List[String] => x.mkString("[", ",", "]")
        }
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
                ticket => Http(url(organisaatiohenkiloUrl.format(oid))
                    .GET
                    .addQueryParameter("ticket", ticket)
                    OK as.String)
            }.apply()
        ObjectMapperSingleton.mapper.readValue(strResp, new TypeReference[java.util.List[OrganisaatioInfo]](){})
    }

    implicit def future2CS[T](future: Future[T]): CompletionStage[T] = FutureConverters.toJava(future)

    /**
     * Hakee ja palauttaa maakunnat ja maakuntaan kuuluvat kaikki kunnat
     */
    def getMaakuntaKunnat: java.util.List[Maakunta] =
        for (maakunta <- request(maakunnatKoodistoUrl, classOf[Array[Maakunta]]).toList) yield {
            maakunta.setKunta(request(maakuntaKunnatKoodistoUrl.format(maakunta.koodiArvo), classOf[Array[Kunta]]))
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

    /**
     * Aluehallintovirastokoodisto
     */
    def getAlueHallintovirastotKoodisto = getKoodistoList(alueHallintovirastotKoodistoUrl, false)
    def getKunnatForAlueHallintovirastoKoodisto(koodi: String) = getKoodistoList(alueHallintovirastonKunnatKoodistoUrl.format(koodi), false)

    /**
     * Maakuntakoodisto
     */
    def getMaakunnatKoodisto = getKoodistoList(maakunnatKoodistoUrl, false)
    def getMaakuntaKunnatKoodisto(koodi: String) = getKoodistoList(maakuntaKunnatKoodistoUrl.format(koodi), false)

    /**
     * Kuntakoodisto
     */
    def getKunnatKoodisto = getKoodistoList(kunnatKoodistoUrl, false)
    def getKuntaKoodisto(koodi: String) = getKoodisto(kuntaKoodistoUrl, koodi)

    /**
     * Kielikoodisto
     */
    def getKieletKoodisto = getKoodistoList(kieletKoodistoUrl, false)
    def getKieliKoodisto(koodi: String) = getKoodisto(kieliKoodistoUrl, koodi.toLowerCase)
    def getOppilaitoksenOpetuskieletKoodisto = getKoodistoList(oppilaitoksenOpetuskielietKoodistoUrl, false)

    /**
     * Koulutuskoodistot
     */
    def getKoulutustyypitKoodisto = getKoodistoList(koulutustyypitKoodistoUrl, true)
    def getKoulutusKoodisto(koodi: String) = getKoodisto(koulutusKoodistoUrl, koodi)
    def getKoulutusAlaKoodisto(koodi: String) = getKoodistoList(koulutusAlaKoodistoUrl.format(koodi), true)
    def getKoulutusAlaOph2002Koodisto(koodi: String) = getKoodisto(koulutusAlaOph2002KoodistoUrl, koodi)
    def getKoulutusOsaamisalaKoodisto(koodi: String) = getKoodisto(koulutusOsaamisalaKoodistoUrl, koodi)

    /**
     * Muut koodistot
     */
    def getEiTutkintoTavotteisetKoodisto = getKoodistoList(eiTutkintoTavottaisetKoodistoUrl, false)

    private def getKoodistoList(url: String, showExpired: Boolean): java.util.List[Koodisto] =
        request(url, classOf[Array[Koodisto]]).toList.filter(koodisto => showExpired || koodisto.isValidDate).distinct.asJava

    private def getKoodisto(urlPattern: String, koodi: String): Koodisto =
        try {
            request(urlPattern.format(koodi), classOf[Koodisto])
        } catch {
            case e: Exception =>  Koodisto.notFound(koodi)
        }
}
