package antifraud.transactionvalidation.service;

import antifraud.TestHelper;
import antifraud.error.ErrorEnum;
import antifraud.error.Result;
import antifraud.transactionvalidation.Enum;
import antifraud.transactionvalidation.Dto.TransactionApprovalVerdict;
import antifraud.transactionvalidation.datastore.TransactionValidationEntity;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
public class FakeTransactionValidationService implements ITransactionValidationService {
    TestHelper.TestBehaviorEnum getTransactionApprovalStatusBehavior;

    @Override
    public TransactionApprovalVerdict getTransactionApprovalVerdict(long amount, String ip, String number, Enum.RegionCode regionCode, LocalDateTime date) {
        if (getTransactionApprovalStatusBehavior.equals(TestHelper.TestBehaviorEnum.SUCCEEDS)) {
            return new TransactionApprovalVerdict(Enum.TransactionStatus.ALLOWED, "none");
        }
        return new TransactionApprovalVerdict(Enum.TransactionStatus.PROHIBITED, "amount");
    }


    @Override
    public Result<ErrorEnum, TransactionValidationEntity> overrideVerdict(Long transactionId, Enum.TransactionStatus feedback) {
        return Result.success(new TransactionValidationEntity());
    }

    @Override
    public List<TransactionValidationEntity> getTransactionValidationHistoryOrderById() {
        return List.of(new TransactionValidationEntity());
    }

    @Override
    public List<TransactionValidationEntity> getTransactionValidationHistoryOrderById(String creditCardNumber) {
        return List.of(new TransactionValidationEntity());
    }

}
