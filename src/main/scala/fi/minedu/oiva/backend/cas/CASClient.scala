package fi.minedu.oiva.backend.cas

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

    private def ticketGrantingTicket(username: String, password: String) = {
        val req = url(casTicketsUrl).POST
            .addParameter("username", username)
            .addParameter("password", password)
        for (tgtResponse <- Http(req))
            yield {
                val stUrl = tgtResponse.getHeader("Location")
                (stUrl, stUrl.substring(stUrl.lastIndexOf("/") + 1))
            }
    }

    def getTicket(serviceUrl: String, username: String, password: String): Future[String] = {
        def doReq(rUrl: String, service: String) =
            Http(url(rUrl).POST.addParameter("service", service))
        for {
            (ticketReqUrl, _) <- ticketGrantingTicket(username, password)
            ticketResponse <- doReq(ticketReqUrl, serviceUrl)
        } yield ticketResponse.getResponseBody.trim()
    }
}
