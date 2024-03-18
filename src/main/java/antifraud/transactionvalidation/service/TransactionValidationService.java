package antifraud.transactionvalidation.service;

import antifraud.transactionvalidation.datastore.IIpAddressEntityDatastore;
import antifraud.transactionvalidation.datastore.IStolenCardEntityDatastore;
import antifraud.transactionvalidation.datastore.ITransactionValidationDatastore;
import antifraud.transactionvalidation.web.AntifraudController.ValidateTransactionRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static antifraud.transactionvalidation.service.TransactionValidationCalculations.*;

@Service
@RequiredArgsConstructor
public class TransactionValidationService implements ITransactionValidationService {

    private final IIpAddressEntityDatastore ipDatastore;
    private final IStolenCardEntityDatastore cardDatastore;
    private final ITransactionValidationDatastore transactionDatastore;

//    @PostConstruct
//    void saveTransactionsForTest() {
//        for (int i = 0; i < 2; i++) {
//            String cardNumber = "4000008449433403";
//            String ip = "169.254.123.22" + i;
//            int minute = i;
//            LocalDateTime localDateTime = LocalDateTime.of(2023, 1, 1, 0, minute);
//            TransactionValidationEntity entity = new TransactionValidationEntity(10, ip, cardNumber, RegionCodeEnum.EAP,
//                    localDateTime, TransactionStatusEnum.ALLOWED, "none");
//            transactionDatastore.save(entity);
//        }
//    }

    @Override
    public TransactionApprovalVerdict getTransactionApprovalStatus(ValidateTransactionRequest request) {
        // actions
        boolean isIpBlacklisted = isIpBlacklisted(request.ip());
        boolean isCreditCardBlacklisted = isCreditCardBlacklisted(request.number());
        long countTransactionsWithDifferentIp = transactionDatastore.countTransactionsWithDifferentIpInLastHour(
                request.number(), request.date(), request.ip());
        long countTransactionsWithDifferentRegion = transactionDatastore.countTransactionsWithDifferentRegionInLastHour(
                request.number(), request.date(), request.region());
//        List<TransactionValidationEntity> transactionValidationHistory = transactionDatastore.getTransactionValidationHistory(
//                request.number(), request.date().minusHours(1), request.date()
//        );

        // calculations
        TransactionApprovalVerdict approvalVerdict = getTransactionApprovalVerdict(request.amount(), isIpBlacklisted,
                isCreditCardBlacklisted, countTransactionsWithDifferentIp, countTransactionsWithDifferentRegion);

        // action
        transactionDatastore.save(toEntity(request, approvalVerdict));
        return approvalVerdict;
    }

    private boolean isCreditCardBlacklisted(String number) {
        return cardDatastore.existsByCardNumber(number);
    }

    private boolean isIpBlacklisted(String ip) {
        return ipDatastore.existsByIp(ip);
    }
}