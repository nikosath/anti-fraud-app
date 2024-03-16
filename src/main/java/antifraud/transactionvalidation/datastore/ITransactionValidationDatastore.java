package antifraud.transactionvalidation.datastore;

import java.time.LocalDateTime;
import java.util.List;

public interface ITransactionValidationDatastore {
    TransactionValidationEntity save(TransactionValidationEntity transactionValidationEntity);

    List<TransactionValidationEntity> getTransactionValidationHistory(String creditCardNumber,
                                                                      LocalDateTime fromDateTime,
                                                                      LocalDateTime untilDateTime);
}
