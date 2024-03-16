package antifraud.transactionvalidation.service;

import antifraud.TestHelper;
import antifraud.transactionvalidation.service.TransactionValidation.TransactionApprovalVerdict;
import antifraud.transactionvalidation.service.TransactionValidation.TransactionStatusEnum;
import antifraud.transactionvalidation.web.AntifraudController;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Setter
public class FakeTransactionValidationService implements ITransactionValidationService {
    TestHelper.TestBehaviorEnum getTransactionApprovalStatusBehavior;

    @Override
    public @NotNull TransactionApprovalVerdict getTransactionApprovalStatus(AntifraudController.ValidateTransactionRequest request) {
        if (getTransactionApprovalStatusBehavior.equals(TestHelper.TestBehaviorEnum.SUCCEEDS)) {
            return new TransactionApprovalVerdict(TransactionStatusEnum.ALLOWED, "none");
        }
        return new TransactionApprovalVerdict(TransactionStatusEnum.PROHIBITED, "amount");
    }
}
