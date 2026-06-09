package ro.flexbiz.efactura

import com.github.scribejava.core.builder.ServiceBuilder
import com.github.scribejava.core.oauth.OAuth20Service
import org.moqui.context.ExecutionContext
import org.moqui.util.SystemBinding

final class OauthServiceFactory {
    static OAuth20Service service(ExecutionContext ec) {
        String baseUrl = ec.web.getWebappRootUrl(true, false)
        String callbackUrl = baseUrl + "/anaf/callback"
        OAuth20Service service = new ServiceBuilder(SystemBinding.getPropOrEnv('ANAF_CLIENT_ID'))
                .apiSecret(SystemBinding.getPropOrEnv('ANAF_CLIENT_SECRET'))
                .callback(callbackUrl)
                .build(AnafApi.instance())
        return service
    }
}
