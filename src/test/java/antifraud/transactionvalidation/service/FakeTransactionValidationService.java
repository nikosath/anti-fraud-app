package antifraud.transactionvalidation.service;

import antifraud.TestHelper;
import antifraud.transactionvalidation.Enum;
import antifraud.transactionvalidation.Dto.TransactionApprovalVerdict;
import antifraud.transactionvalidation.web.AntifraudController;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
public class FakeTransactionValidationService implements ITransactionValidationService {
    TestHelper.TestBehaviorEnum getTransactionApprovalStatusBehavior;

    @Override
    public TransactionApprovalVerdict getTransactionApprovalStatus(long amount, String ip, String number, Enum.RegionCode regionCode, LocalDateTime date) {
        if (getTransactionApprovalStatusBehavior.equals(TestHelper.TestBehaviorEnum.SUCCEEDS)) {
            return new TransactionApprovalVerdict(Enum.TransactionStatus.ALLOWED, "none");
        }
        return new TransactionApprovalVerdict(Enum.TransactionStatus.PROHIBITED, "amount");
    }

}
