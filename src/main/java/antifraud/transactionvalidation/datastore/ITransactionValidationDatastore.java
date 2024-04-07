package antifraud.transactionvalidation.datastore;

import antifraud.transactionvalidation.Enum;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ITransactionValidationDatastore {
    TransactionValidationEntity save(TransactionValidationEntity transactionValidationEntity);

    Optional<TransactionValidationEntity> findById(Long id);

    List<TransactionValidationEntity> saveAll(Iterable<TransactionValidationEntity> transactionValidationEntity);

    /**
     * @return list of all transactions, ordered by transaction id asc
     */
    List<TransactionValidationEntity> getTransactionValidationHistory();

    /**
     * @return list of transactions for the given credit card number, ordered by transaction id asc
     */
    List<TransactionValidationEntity> getTransactionValidationHistory(String creditCardNumber);

    long countTransactionsWithDifferentIpInLastHour(String creditCardNumber,
                                                    LocalDateTime transactionDateTime, String ipAddress);

    long countTransactionsWithDifferentRegionInLastHour(String creditCardNumber,
                                                        LocalDateTime transactionDateTime, Enum.RegionCode regionCode);
}
