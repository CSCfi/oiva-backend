package fi.minedu.oiva.backend.core.cas

import dispatch.Defaults._
import dispatch._
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

/**
 * Use in the case of possible authorized CAS calls
 */
@Component
class CASClient {

    @Value("${cas.baseUrl}${cas.url.prefix}/v1/tickets")
    private val casTicketsUrl: String = null
    @Value("${opintopolku.apiCaller.header}")
    private val callerHeader: String = null
    @Value("${opintopolku.apiCaller.id}")
    private val callerId: String = null

    private def post(requestUrl: String) = url(requestUrl).POST

    private def ticketGrantingTicket(username: String, password: String) = {
        val tgtRequest = Http(post(casTicketsUrl)
            .addHeader(callerHeader, callerId)
            .addParameter("username", username).addParameter("password", password))
        for (tgtResponse <- tgtRequest) yield tgtResponse.getHeader("Location")
    }

    def getTicket(serviceUrl: String, username: String, password: String): Future[String] = {
        for {
            stRequestUrl <- ticketGrantingTicket(username, password)
            stResponse <- Http(post(stRequestUrl)
                .addHeader(callerHeader, callerId)
                .addParameter("service", serviceUrl + "/j_spring_cas_security_check"))
        } yield stResponse.getResponseBody.trim()
    }
}
