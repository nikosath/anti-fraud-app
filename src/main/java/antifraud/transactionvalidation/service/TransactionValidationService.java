package antifraud.transactionvalidation.service;

import antifraud.transactionvalidation.datastore.IIpAddressEntityDatastore;
import antifraud.transactionvalidation.datastore.IStolenCardEntityDatastore;
import antifraud.transactionvalidation.datastore.ITransactionValidationDatastore;
import antifraud.transactionvalidation.datastore.TransactionValidationEntity;
import antifraud.transactionvalidation.service.TransactionValidation.TransactionApprovalVerdict;
import antifraud.transactionvalidation.web.AntifraudController.ValidateTransactionRequest;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionValidationService implements ITransactionValidationService {

    private final IIpAddressEntityDatastore ipDatastore;
    private final IStolenCardEntityDatastore cardDatastore;
    private final ITransactionValidationDatastore transactionDatastore;

    @Override
    @NotNull
    public TransactionApprovalVerdict getTransactionApprovalStatus(ValidateTransactionRequest request) {
        boolean ipBlacklisted = isIpBlacklisted(request.ip());
        boolean isCreditCardBlacklisted = isCreditCardBlacklisted(request.number());
        List<TransactionValidationEntity> transactionValidationHistory = transactionDatastore.getTransactionValidationHistory(
                request.number(), request.date().minusHours(1), request.date()
        );
        var approvalVerdict = TransactionValidation.determineTransactionApprovalVerdict(
                request.amount(), ipBlacklisted, isCreditCardBlacklisted, transactionValidationHistory);

        transactionDatastore.save(toEntity(request, approvalVerdict));
        return approvalVerdict;
    }

    private TransactionValidationEntity toEntity(ValidateTransactionRequest request, TransactionApprovalVerdict approvalVerdict) {
        return new TransactionValidationEntity(request.amount(), request.ip(), request.number(), request.region(),
                request.date(), approvalVerdict.transactionStatus(), approvalVerdict.statusJustification());
    }

    private boolean isCreditCardBlacklisted(String number)  {
        return cardDatastore.existsByCardNumber(number);
    }

    private boolean isIpBlacklisted(String ip) {
        return ipDatastore.existsByIp(ip);
    }
}