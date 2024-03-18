package antifraud.transactionvalidation.datastore;

import antifraud.transactionvalidation.RegionCodeEnum;

import java.time.LocalDateTime;
import java.util.List;

public interface ITransactionValidationDatastore {
    TransactionValidationEntity save(TransactionValidationEntity transactionValidationEntity);

    List<TransactionValidationEntity> saveAll(Iterable<TransactionValidationEntity> transactionValidationEntity);

    List<TransactionValidationEntity> getTransactionValidationHistory(String creditCardNumber,
                                                                      LocalDateTime fromDateTime,
                                                                      LocalDateTime untilDateTime);

    long countTransactionsWithDifferentIpInLastHour(String creditCardNumber,
                                                    LocalDateTime transactionDateTime, String ipAddress);

    long countTransactionsWithDifferentRegionInLastHour(String creditCardNumber,
                                                        LocalDateTime transactionDateTime, RegionCodeEnum regionCode);
}
