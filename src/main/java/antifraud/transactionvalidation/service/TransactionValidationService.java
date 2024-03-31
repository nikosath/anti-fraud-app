package antifraud.transactionvalidation.service;

import antifraud.error.ErrorEnum;
import antifraud.error.Result;
import antifraud.transactionvalidation.Dto;
import antifraud.transactionvalidation.Enum;
import antifraud.transactionvalidation.datastore.IIpAddressEntityDatastore;
import antifraud.transactionvalidation.datastore.IStolenCardEntityDatastore;
import antifraud.transactionvalidation.datastore.ITransactionValidationDatastore;
import antifraud.transactionvalidation.datastore.TransactionValidationEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class TransactionValidationService implements ITransactionValidationService {

    private final IIpAddressEntityDatastore ipDatastore;
    private final IStolenCardEntityDatastore cardDatastore;
    private final ITransactionValidationDatastore transactionDatastore;
    private final IConfigProvider configProvider;

    public Dto.TransactionApprovalVerdict getTransactionApprovalVerdict(long amount, String ip, String number,
                                                                        Enum.RegionCode regionCode, LocalDateTime date) {
        // actions
        boolean isIpBlacklisted = isIpBlacklisted(ip);
        boolean isCreditCardBlacklisted = isCreditCardBlacklisted(number);
        long countTransactionsWithDifferentIp = transactionDatastore.countTransactionsWithDifferentIpInLastHour(
                number, date, ip);
        long countTransactionsWithDifferentRegion = transactionDatastore.countTransactionsWithDifferentRegionInLastHour(
                number, date, regionCode);
        long amountLimitForAllowed = configProvider.getTransactionValidationConfig().amountLimitForAllowed();
        long amountLimitForManualProcessing = configProvider.getTransactionValidationConfig().amountLimitForManualProcessing();

        // calculations
        Dto.TransactionApprovalVerdict approvalVerdict = TransactionValidationCalculations.getTransactionApprovalVerdict(amount
                , isIpBlacklisted,
                isCreditCardBlacklisted, countTransactionsWithDifferentIp, countTransactionsWithDifferentRegion,
                amountLimitForAllowed, amountLimitForManualProcessing);

        // action
        transactionDatastore.save(toEntity(amount, ip, number, regionCode, date, approvalVerdict));
        return approvalVerdict;
    }

    @Override
    public Result<ErrorEnum, TransactionValidationEntity> updateVerdict(Long transactionId, Enum.TransactionStatus feedback) {
        Optional<TransactionValidationEntity> entityOpt = transactionDatastore.findById(transactionId);
        if (entityOpt.isEmpty()) {
            return Result.error(ErrorEnum.ENTITY_NOT_FOUND);
        }
        var entity = entityOpt.get();
        if (entity.getTransactionStatus() == feedback) {
            return Result.error(ErrorEnum.STATE_ALREADY_EXISTS);
        }
        entity.setTransactionStatus(feedback);
        return Result.success(transactionDatastore.save(entity));
    }

    @Override
    public List<TransactionValidationEntity> getTransactionValidationHistoryOrderById() {
        return transactionDatastore.getTransactionValidationHistoryOrderById();
    }
    @Override
    public List<TransactionValidationEntity> getTransactionValidationHistoryOrderById(String creditCardNumber) {
        return transactionDatastore.getTransactionValidationHistoryOrderById(creditCardNumber);
    }

    private boolean isCreditCardBlacklisted(String number) {
        return cardDatastore.existsByCardNumber(number);
    }

    private boolean isIpBlacklisted(String ip) {
        return ipDatastore.existsByIp(ip);
    }

    private TransactionValidationEntity toEntity(long amount, String ip, String number, Enum.RegionCode regionCode,
                                                 LocalDateTime date, Dto.TransactionApprovalVerdict approvalVerdict) {
        return new TransactionValidationEntity(amount, ip, number, regionCode, date, approvalVerdict.transactionStatus(),
                approvalVerdict.statusJustification());
    }

}