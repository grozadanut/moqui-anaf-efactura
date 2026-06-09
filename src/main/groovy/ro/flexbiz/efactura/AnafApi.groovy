package ro.flexbiz.efactura

import com.github.scribejava.core.builder.api.DefaultApi20
import org.moqui.util.SystemBinding

class AnafApi extends DefaultApi20 {
    private AnafApi() {
    }

    static AnafApi instance() {
        return InstanceHolder.INSTANCE
    }

    protected String getAuthorizationBaseUrl() {
        return SystemBinding.getPropOrEnv('ANAF_AUTHORIZATIONURI')
    }

    String getRevokeTokenEndpoint() {
        return SystemBinding.getPropOrEnv('ANAF_REVOKEURI')
    }

    String getAccessTokenEndpoint() {
        return SystemBinding.getPropOrEnv('ANAF_TOKENURI')
    }

    private static class InstanceHolder {
        private static final AnafApi INSTANCE = new AnafApi()

        private InstanceHolder() {
        }
    }
}
