package antifraud.transactionvalidation.datastore;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ITransactionValidationRepo extends JpaRepository<TransactionValidationEntity, Long> {
    List<TransactionValidationEntity> findByCreditCardNumberAndTransactionDateTimeBetween(
            String creditCardNumber,
            LocalDateTime fromDateTime,
            LocalDateTime untilDateTime
    );
}
