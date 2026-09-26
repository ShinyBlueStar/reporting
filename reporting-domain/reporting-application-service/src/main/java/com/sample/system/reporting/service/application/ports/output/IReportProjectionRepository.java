package com.sample.system.reporting.service.application.ports.output;

import com.sample.system.reporting.service.domain.model.entity.*;

public interface IReportProjectionRepository {

    void upsertParty(PartyReport partyReport);

    void upsertCard(CardReport cardReport);

    void upsertLoan(LoanReport loanReport);

    void upsertProduct(ProductReport productReport);

    void upsertAccount(AccountReport accountReport);

    void upsertTransaction(TransactionReport transactionReport);

    void upsertInstallment(InstallmentReport installmentReport);
}
