package ro.flexbiz.efactura.service

import org.moqui.context.ExecutionContext
import org.moqui.entity.EntityCondition

class ReceivedInvoiceServices {
    static Map<String, Object> receivedInvoicesBetween(ExecutionContext ec) {
        return [resultList: ec.entity.find("ro.flexbiz.efactura.ReceivedInvoice")
                .condition("issueDate", EntityCondition.ComparisonOperator.BETWEEN, [ec.context.from, ec.context.thru])
                .list()]
    }

    static Map<String, Object> findByInvoiceIdIn(ExecutionContext ec) {
        return [resultList: ec.entity.find("ro.flexbiz.efactura.ReceivedInvoice")
                .condition("invoiceId", EntityCondition.ComparisonOperator.IN, ec.context.invoiceIds)
                .list()]
    }

    static Map<String, Object> updateInvoiceId(ExecutionContext ec) {
        return ec.entity.find("ro.flexbiz.efactura.ReceivedInvoice")
                .forUpdate(true)
                .condition("id", ec.context.id)
                .one()
                .set("invoiceId", ec.context.invoiceId)
                .store()
    }
}
