package antifraud.transactionvalidation.service;

import antifraud.transactionvalidation.Dto;
import antifraud.transactionvalidation.Enum;
import antifraud.transactionvalidation.datastore.IIpAddressEntityDatastore;
import antifraud.transactionvalidation.datastore.IStolenCardEntityDatastore;
import antifraud.transactionvalidation.datastore.ITransactionValidationDatastore;
import antifraud.transactionvalidation.datastore.TransactionValidationEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static antifraud.transactionvalidation.service.TransactionValidationCalculations.getTransactionApprovalVerdict;

@Service
@RequiredArgsConstructor
public class TransactionValidationService implements ITransactionValidationService {

    private final IIpAddressEntityDatastore ipDatastore;
    private final IStolenCardEntityDatastore cardDatastore;
    private final ITransactionValidationDatastore transactionDatastore;
    private final IConfigProvider configProvider;

    public Dto.TransactionApprovalVerdict getTransactionApprovalStatus(long amount, String ip, String number,
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
        Dto.TransactionApprovalVerdict approvalVerdict = getTransactionApprovalVerdict(amount, isIpBlacklisted,
                isCreditCardBlacklisted, countTransactionsWithDifferentIp, countTransactionsWithDifferentRegion,
                amountLimitForAllowed, amountLimitForManualProcessing);

        // action
        transactionDatastore.save(toEntity(amount, ip, number, regionCode, date, approvalVerdict));
        return approvalVerdict;
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