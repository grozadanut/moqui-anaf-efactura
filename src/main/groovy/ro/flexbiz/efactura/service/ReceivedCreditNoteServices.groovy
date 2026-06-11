package ro.flexbiz.efactura.service

import org.moqui.context.ExecutionContext
import org.moqui.entity.EntityCondition

class ReceivedCreditNoteServices {
    static Map<String, Object> findAllByIssueDateBetween(ExecutionContext ec) {
        return [resultList: ec.entity.find("ro.flexbiz.efactura.ReceivedCreditNote")
                .condition("issueDate", EntityCondition.ComparisonOperator.BETWEEN, [ec.context.from, ec.context.thru])
                .list()]
    }
}
