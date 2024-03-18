package antifraud.transactionvalidation.service;

import antifraud.TestHelper;
import antifraud.transactionvalidation.service.TransactionValidationCalculations.TransactionApprovalVerdict;
import antifraud.transactionvalidation.service.TransactionValidationCalculations.TransactionStatusEnum;
import antifraud.transactionvalidation.web.AntifraudController;
import lombok.Setter;

@Setter
public class FakeTransactionValidationService implements ITransactionValidationService {
    TestHelper.TestBehaviorEnum getTransactionApprovalStatusBehavior;

    @Override
    public TransactionApprovalVerdict getTransactionApprovalStatus(AntifraudController.ValidateTransactionRequest request) {
        if (getTransactionApprovalStatusBehavior.equals(TestHelper.TestBehaviorEnum.SUCCEEDS)) {
            return new TransactionApprovalVerdict(TransactionStatusEnum.ALLOWED, "none");
        }
        return new TransactionApprovalVerdict(TransactionStatusEnum.PROHIBITED, "amount");
    }
}
