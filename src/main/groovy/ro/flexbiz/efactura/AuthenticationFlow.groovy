package ro.flexbiz.efactura

import com.github.scribejava.core.model.OAuth2AccessToken
import com.github.scribejava.core.oauth.OAuth20Service
import org.moqui.context.ExecutionContext
import org.moqui.entity.EntityValue
import org.moqui.service.ServiceException
import org.moqui.util.CollectionUtilities

import java.sql.Timestamp

class AuthenticationFlow {
    static void codeGrant(ExecutionContext ec) {
        OAuth20Service oauthService = OauthServiceFactory.service(ec)
        String authorizationUrl = oauthService.getAuthorizationUrl()
        ec.web.response.sendRedirect(authorizationUrl)
    }

    static void handleCallback(ExecutionContext ec) {
        OAuth20Service oauthService = OauthServiceFactory.service(ec)
        if (!ec.web.requestParameters.code) {
            ec.logger.error("ANAF oauth callback missing 'code' parameter")
            return
        }

        // Exchange the callback 'code' for the access token
        OAuth2AccessToken token = oauthService.getAccessToken(ec.web.requestParameters.code as String)
        ec.logger.info("ANAF oauth refreshToken: " + token.refreshToken)
        ec.logger.info("ANAF oauth expiresIn: " + token.expiresIn)
        ec.logger.info("ANAF oauth tokenType: " + token.tokenType)
        String accessToken = token.getAccessToken()
        String refreshToken = token.getRefreshToken()
        Calendar cal = Calendar.getInstance()
        cal.setTimeInMillis(ec.user.nowTimestamp.getTime())
        cal.add(Calendar.SECOND, token.getExpiresIn() ?: 86400) // default to 24 hours
        Timestamp expiresAt = new Timestamp(cal.getTime().getTime())

        EntityValue credential = ec.entity.makeValue("ro.flexbiz.security.Credential")
        credential.set("credentialId", "ANAF_EFACTURA_OAUTH_TOKEN")
        credential.set("credentialTypeEnumId", "CtLogin")
        credential.store()

        EntityValue accessTokenField = ec.entity.makeValue("ro.flexbiz.security.CredentialField")
        accessTokenField.set("credentialId", "ANAF_EFACTURA_OAUTH_TOKEN")
        accessTokenField.set("name", "accessToken")
        accessTokenField.set("fromDate", "2026-06-01T00:00:00Z")
        accessTokenField.set("thruDate", expiresAt)
        accessTokenField.set("value", accessToken)
        accessTokenField.store()

        if (refreshToken != null && !refreshToken.isEmpty()) {
            EntityValue refreshTokenField = ec.entity.makeValue("ro.flexbiz.security.CredentialField")
            refreshTokenField.set("credentialId", "ANAF_EFACTURA_OAUTH_TOKEN")
            refreshTokenField.set("name", "refreshToken")
            refreshTokenField.set("fromDate", "2026-06-01T00:00:00Z")
            refreshTokenField.set("value", refreshToken)
            refreshTokenField.store()
        }

        EntityValue credentialUser = ec.entity.makeValue("ro.flexbiz.security.CredentialUser")
        credentialUser.set("credentialId", "ANAF_EFACTURA_OAUTH_TOKEN")
        credentialUser.set("userId", ec.user.userId)
        credentialUser.set("fromDate", "2026-06-01T00:00:00Z")
        credentialUser.set("authzActionEnumId", "AUTHZA_ALL")
        credentialUser.store()
    }

    static Map<String, Object> refreshAccessToken(ExecutionContext ec) {
        OAuth20Service oauthService = OauthServiceFactory.service(ec)
        OAuth2AccessToken token = oauthService.refreshAccessToken(ec.context.refreshToken as String)
        ec.logger.info("refresh ANAF oauth refreshToken: " + token.refreshToken)
        ec.logger.info("refresh ANAF oauth expiresIn: " + token.expiresIn)
        ec.logger.info("refresh ANAF oauth tokenType: " + token.tokenType)

        String accessToken = token.getAccessToken()
        Calendar cal = Calendar.getInstance()
        cal.setTimeInMillis(ec.user.nowTimestamp.getTime())
        cal.add(Calendar.SECOND, token.getExpiresIn() ?: 86400) // default to 24 hours
        Timestamp expiresAt = new Timestamp(cal.getTime().getTime())

        if (accessToken == null || accessToken.isEmpty()) {
            throw new ServiceException("Tokenul de acces la ANAF nu a putut fi reimprospatat!")
        }

        EntityValue accessTokenField = ec.entity.makeValue("ro.flexbiz.security.CredentialField")
        accessTokenField.set("credentialId", "ANAF_EFACTURA_OAUTH_TOKEN")
        accessTokenField.set("name", "accessToken")
        accessTokenField.set("fromDate", "2026-06-01T00:00:00Z")
        accessTokenField.set("thruDate", expiresAt)
        accessTokenField.set("value", accessToken)
        accessTokenField.store()

        return [accessToken: token.accessToken]
    }

    static Map<String, Object> findAnafAccessToken(ExecutionContext ec) {
        Map<String, Object> anafToken = ec.service.sync().name("CredentialServices.find#Credential")
                .parameters([credentialId: "ANAF_EFACTURA_OAUTH_TOKEN"])
                .call()

        String accessToken = CollectionUtilities.findFirstByKeyValue(anafToken.resultList, "name", "accessToken")?.value
        String taxId = CollectionUtilities.findFirstByKeyValue(anafToken.resultList, "name", "taxId")?.value
        if (accessToken == null || accessToken.isEmpty()) {
            String refreshToken = CollectionUtilities.findFirstByKeyValue(anafToken.resultList, "name", "refreshToken")?.value

            if (refreshToken == null || refreshToken.isEmpty())
                throw new ServiceException("Nu a fost gasit niciun token de acces la ANAF pentru utilizatorul curent!")

            accessToken = ec.service.sync().name("ro.flexbiz.efactura.AuthServices.refresh#AccessToken")
                    .parameters([refreshToken: refreshToken])
                    .call().accessToken
        }

        return [accessToken: accessToken, taxId: taxId]
    }
}