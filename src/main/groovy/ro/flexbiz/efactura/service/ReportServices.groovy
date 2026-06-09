package ro.flexbiz.efactura.service

import org.moqui.context.ExecutionContext
import org.moqui.service.ServiceException

class ReportServices {
    static Map<String, Object> reportInvoice(ExecutionContext ec) {
        String accessToken = ec.service.sync()
                .name("ro.flexbiz.efactura.AuthServices.find#AnafAccessToken")
                .call().accessToken
        if (accessToken == null || accessToken.isEmpty())
            throw new ServiceException("Nu aveti un token de acces la ANAF!")

        ec.logger.info("token ok: "+accessToken)

        return [:]
    }
}
