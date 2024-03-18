package antifraud.transactionvalidation.service;

import antifraud.transactionvalidation.web.AntifraudController;

public interface ITransactionValidationService {
    TransactionValidationCalculations.TransactionApprovalVerdict getTransactionApprovalStatus(AntifraudController.ValidateTransactionRequest request);
}
