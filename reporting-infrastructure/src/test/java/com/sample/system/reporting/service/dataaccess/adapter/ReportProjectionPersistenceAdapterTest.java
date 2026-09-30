package com.sample.system.reporting.service.dataaccess.adapter;

import com.sample.system.reporting.service.dataaccess.mapper.*;
import com.sample.system.reporting.service.dataaccess.repository.*;
import com.sample.system.reporting.service.dataaccess.entity.query.InstallmentReportEntityQuery;
import com.sample.system.reporting.service.dataaccess.entity.query.LoanReportEntityQuery;
import com.sample.system.reporting.service.domain.model.entity.InstallmentReport;
import com.sample.system.reporting.service.domain.model.entity.LoanReport;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ReportProjectionPersistenceAdapterTest {

    private final PartyReportRepository partyRepository = mock(PartyReportRepository.class);
    private final CardReportRepository cardRepository = mock(CardReportRepository.class);
    private final LoanReportRepository loanRepository = mock(LoanReportRepository.class);
    private final ProductReportRepository productRepository = mock(ProductReportRepository.class);
    private final AccountReportRepository accountRepository = mock(AccountReportRepository.class);
    private final TransactionReportRepository transactionRepository = mock(TransactionReportRepository.class);
    private final InstallmentReportRepository installmentRepository = mock(InstallmentReportRepository.class);
    private final PartyReportDataAccessMapper partyMapper = mock(PartyReportDataAccessMapper.class);
    private final CardReportDataAccessMapper cardMapper = mock(CardReportDataAccessMapper.class);
    private final LoanReportDataAccessMapper loanMapper = mock(LoanReportDataAccessMapper.class);
    private final ProductReportDataAccessMapper productMapper = mock(ProductReportDataAccessMapper.class);
    private final AccountReportDataAccessMapper accountMapper = mock(AccountReportDataAccessMapper.class);
    private final TransactionReportDataAccessMapper transactionMapper = mock(TransactionReportDataAccessMapper.class);
    private final InstallmentReportDataAccessMapper installmentMapper = mock(InstallmentReportDataAccessMapper.class);
    private final IReportProjectionPersistenceAdapter adapter = new IReportProjectionPersistenceAdapter(
            partyRepository,
            cardRepository,
            loanRepository,
            productRepository,
            accountRepository,
            transactionRepository,
            installmentRepository,
            partyMapper,
            cardMapper,
            loanMapper,
            productMapper,
            accountMapper,
            transactionMapper,
            installmentMapper
    );

    @Test
    void ignoresOutOfOrderLoanProjection() {
        LoanReport incoming = new LoanReport();
        incoming.setAggregateId("loan-1");
        incoming.setAggregateVersion(1L);
        incoming.setLastEventId("event-1");
        LoanReportEntityQuery existing = new LoanReportEntityQuery();
        existing.setAggregateVersion(2L);

        when(loanMapper.loanReportToLoanReportEntity(incoming)).thenReturn(new LoanReportEntityQuery());
        when(loanRepository.findByAggregateId("loan-1")).thenReturn(Optional.of(existing));

        adapter.upsertLoan(incoming);

        verify(loanRepository, never()).save(any());
    }

    @Test
    void skipsInstallmentProjectionWhenLoanAggregateIdIsMissing() {
        InstallmentReport incoming = installment(null);

        adapter.upsertInstallment(incoming);

        verifyNoInteractions(installmentMapper, installmentRepository);
        verify(loanRepository, never()).findByAggregateId(anyString());
    }

    @Test
    void skipsInstallmentProjectionWhenParentLoanDoesNotExist() {
        InstallmentReport incoming = installment("loan-1");
        when(loanRepository.findByAggregateId("loan-1")).thenReturn(Optional.empty());

        adapter.upsertInstallment(incoming);

        verify(loanRepository).findByAggregateId("loan-1");
        verifyNoInteractions(installmentMapper, installmentRepository);
    }

    @Test
    void savesInstallmentProjectionWithParentLoanAndDenormalizedLoanFileNo() {
        InstallmentReport incoming = installment("loan-1");
        LoanReportEntityQuery loan = new LoanReportEntityQuery();
        loan.setLoanFileNo("LN-1001");
        InstallmentReportEntityQuery mapped = new InstallmentReportEntityQuery();
        when(loanRepository.findByAggregateId("loan-1")).thenReturn(Optional.of(loan));
        when(installmentMapper.installmentReportToInstallmentReportEntity(incoming)).thenReturn(mapped);
        when(installmentRepository.findByAggregateId("installment-1")).thenReturn(Optional.empty());

        adapter.upsertInstallment(incoming);

        ArgumentCaptor<InstallmentReportEntityQuery> captor = ArgumentCaptor.forClass(InstallmentReportEntityQuery.class);
        verify(installmentRepository).save(captor.capture());
        InstallmentReportEntityQuery saved = captor.getValue();
        assertEquals(loan, saved.getLoan());
        assertEquals("LN-1001", saved.getLoanFileNo());
        assertEquals("event-1", saved.getLastEventId());
        assertEquals(false, saved.getDeleted());
    }

    @Test
    void skipsOutOfOrderInstallmentProjection() {
        InstallmentReport incoming = installment("loan-1");
        LoanReportEntityQuery loan = new LoanReportEntityQuery();
        InstallmentReportEntityQuery existing = new InstallmentReportEntityQuery();
        existing.setAggregateVersion(3L);
        when(loanRepository.findByAggregateId("loan-1")).thenReturn(Optional.of(loan));
        when(installmentMapper.installmentReportToInstallmentReportEntity(incoming))
                .thenReturn(new InstallmentReportEntityQuery());
        when(installmentRepository.findByAggregateId("installment-1")).thenReturn(Optional.of(existing));

        adapter.upsertInstallment(incoming);

        verify(installmentRepository, never()).save(any());
    }

    private InstallmentReport installment(String loanAggregateId) {
        InstallmentReport report = new InstallmentReport();
        report.setAggregateId("installment-1");
        report.setAggregateVersion(2L);
        report.setLastEventId("event-1");
        report.setLoanAggregateId(loanAggregateId);
        return report;
    }
}
