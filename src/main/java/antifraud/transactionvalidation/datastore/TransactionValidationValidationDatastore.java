package antifraud.transactionvalidation.datastore;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class TransactionValidationValidationDatastore implements ITransactionValidationDatastore {
    private final ITransactionValidationRepo repo;

    @Override
    public TransactionValidationEntity save(TransactionValidationEntity transactionValidationEntity) {
        return repo.save(transactionValidationEntity);
    }

    @Override
    public List<TransactionValidationEntity> getTransactionValidationHistory(String creditCardNumber,
                                                                             LocalDateTime fromDateTime,
                                                                             LocalDateTime untilDateTime) {
        return repo.findByCreditCardNumberAndTransactionDateTimeBetween(creditCardNumber, fromDateTime, untilDateTime);
    }
}
