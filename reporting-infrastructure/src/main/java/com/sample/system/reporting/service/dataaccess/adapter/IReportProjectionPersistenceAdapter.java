package com.sample.system.reporting.service.dataaccess.adapter;

import com.sample.system.reporting.service.application.ports.output.IReportProjectionRepository;
import com.sample.system.reporting.service.dataaccess.entity.query.*;
import com.sample.system.reporting.service.dataaccess.mapper.*;
import com.sample.system.reporting.service.dataaccess.repository.*;
import com.sample.system.reporting.service.domain.model.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
public class IReportProjectionPersistenceAdapter implements IReportProjectionRepository {

    private final PartyReportRepository partyReportRepository;
    private final CardReportRepository cardReportRepository;
    private final LoanReportRepository loanReportRepository;
    private final ProductReportRepository productReportRepository;
    private final AccountReportRepository accountReportRepository;
    private final TransactionReportRepository transactionReportRepository;
    private final InstallmentReportRepository installmentReportRepository;
    private final PartyReportDataAccessMapper partyMapper;
    private final CardReportDataAccessMapper cardMapper;
    private final LoanReportDataAccessMapper loanMapper;
    private final ProductReportDataAccessMapper productMapper;
    private final AccountReportDataAccessMapper accountMapper;
    private final TransactionReportDataAccessMapper transactionMapper;
    private final InstallmentReportDataAccessMapper installmentMapper;

    public IReportProjectionPersistenceAdapter(PartyReportRepository partyReportRepository,
                                               CardReportRepository cardReportRepository,
                                               LoanReportRepository loanReportRepository,
                                               ProductReportRepository productReportRepository,
                                               AccountReportRepository accountReportRepository,
                                               TransactionReportRepository transactionReportRepository,
                                               InstallmentReportRepository installmentReportRepository,
                                               PartyReportDataAccessMapper partyMapper,
                                               CardReportDataAccessMapper cardMapper,
                                               LoanReportDataAccessMapper loanMapper,
                                               ProductReportDataAccessMapper productMapper,
                                               AccountReportDataAccessMapper accountMapper,
                                               TransactionReportDataAccessMapper transactionMapper,
                                               InstallmentReportDataAccessMapper installmentMapper) {
        this.partyReportRepository = partyReportRepository;
        this.cardReportRepository = cardReportRepository;
        this.loanReportRepository = loanReportRepository;
        this.productReportRepository = productReportRepository;
        this.accountReportRepository = accountReportRepository;
        this.transactionReportRepository = transactionReportRepository;
        this.installmentReportRepository = installmentReportRepository;
        this.partyMapper = partyMapper;
        this.cardMapper = cardMapper;
        this.loanMapper = loanMapper;
        this.productMapper = productMapper;
        this.accountMapper = accountMapper;
        this.transactionMapper = transactionMapper;
        this.installmentMapper = installmentMapper;
    }

    @Override
    public void upsertParty(PartyReport partyReport) {
        upsert(partyReport, partyMapper.partyReportToPartyReportEntity(partyReport),
                partyReportRepository.findByAggregateId(partyReport.getAggregateId()),
                partyReportRepository::save);
    }

    @Override
    public void upsertCard(CardReport cardReport) {
        upsert(cardReport, cardMapper.cardReportToCardReportEntity(cardReport),
                cardReportRepository.findByAggregateId(cardReport.getAggregateId()),
                cardReportRepository::save);
    }

    @Override
    public void upsertLoan(LoanReport loanReport) {
        upsert(loanReport, loanMapper.loanReportToLoanReportEntity(loanReport),
                loanReportRepository.findByAggregateId(loanReport.getAggregateId()),
                loanReportRepository::save);
    }

    @Override
    public void upsertProduct(ProductReport productReport) {
        upsert(productReport, productMapper.productReportToProductReportEntity(productReport),
                productReportRepository.findByAggregateId(productReport.getAggregateId()),
                productReportRepository::save);
    }

    @Override
    public void upsertAccount(AccountReport accountReport) {
        upsert(accountReport, accountMapper.accountReportToAccountReportEntity(accountReport),
                accountReportRepository.findByAggregateId(accountReport.getAggregateId()),
                accountReportRepository::save);
    }

    @Override
    public void upsertTransaction(TransactionReport transactionReport) {
        upsert(transactionReport, transactionMapper.transactionReportToTransactionReportEntity(transactionReport),
                transactionReportRepository.findByAggregateId(transactionReport.getAggregateId()),
                transactionReportRepository::save);
    }

    @Override
    public void upsertInstallment(InstallmentReport installmentReport) {
        if (installmentReport.getLoanAggregateId() == null || installmentReport.getLoanAggregateId().isBlank()) {
            log.warn("Skipping installment projection aggregateId={} because loanAggregateId is missing",
                    installmentReport.getAggregateId());
            return;
        }

        Optional<LoanReportEntityQuery> loan =
                loanReportRepository.findByAggregateId(installmentReport.getLoanAggregateId());
        if (loan.isEmpty()) {
            log.warn("Skipping installment projection aggregateId={} because loan aggregateId={} was not found",
                    installmentReport.getAggregateId(),
                    installmentReport.getLoanAggregateId());
            return;
        }

        InstallmentReportEntityQuery entity =
                installmentMapper.installmentReportToInstallmentReportEntity(installmentReport);
        entity.setLoan(loan.get());
        if (entity.getLoanFileNo() == null) {
            entity.setLoanFileNo(loan.get().getLoanFileNo());
        }

        upsert(installmentReport, entity,
                installmentReportRepository.findByAggregateId(installmentReport.getAggregateId()),
                installmentReportRepository::save);
    }

    private <E extends BaseReportEntityQuery> void upsert(ProjectionReport report,
                                                          E entity,
                                                          Optional<E> existing,
                                                          java.util.function.Function<E, E> saver) {
        if (existing.isPresent() && !isStale(report.getAggregateVersion(), existing.get().getAggregateVersion())) {
            return;
        }
        existing.ifPresent(value -> entity.setId(value.getId()));
        entity.setLastEventId(report.getLastEventId());
        entity.setDeleted(false);
        saver.apply(entity);
    }

    private boolean isStale(Long incomingVersion, Long currentVersion) {
        return currentVersion == null || incomingVersion == null || incomingVersion > currentVersion;
    }
}
