package antifraud.transactionvalidation.service;

import antifraud.transactionvalidation.web.AntifraudController;
import org.jetbrains.annotations.NotNull;

public interface ITransactionValidationService {
    @NotNull TransactionValidation.TransactionApprovalVerdict getTransactionApprovalStatus(AntifraudController.ValidateTransactionRequest request);
}
