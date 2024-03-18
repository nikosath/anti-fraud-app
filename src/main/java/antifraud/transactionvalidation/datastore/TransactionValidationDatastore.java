package antifraud.transactionvalidation.datastore;

import antifraud.transactionvalidation.Enum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class TransactionValidationDatastore implements ITransactionValidationDatastore {
    private final ITransactionValidationRepo repo;

    @Override
    public TransactionValidationEntity save(TransactionValidationEntity transactionValidationEntity) {
        return repo.save(transactionValidationEntity);
    }

    @Override
    public List<TransactionValidationEntity> saveAll(Iterable<TransactionValidationEntity> transactionValidationEntity) {
        return repo.saveAll(transactionValidationEntity);
    }

    @Override
    public List<TransactionValidationEntity> getTransactionValidationHistory(String creditCardNumber,
                                                                             LocalDateTime fromDateTime,
                                                                             LocalDateTime untilDateTime) {
        return repo.findByCreditCardNumberAndTransactionDateTimeBetween(creditCardNumber, fromDateTime, untilDateTime);
    }

    @Override
    public long countTransactionsWithDifferentIpInLastHour(String creditCardNumber,
                                                           LocalDateTime transactionDateTime, String ipAddress) {
        LocalDateTime fromDateTime = transactionDateTime.minusHours(1);
        return repo.countTransactionsWithDifferentIpInLastHour(creditCardNumber, fromDateTime,
                transactionDateTime, ipAddress);
    }

    @Override
    public long countTransactionsWithDifferentRegionInLastHour(String creditCardNumber,
                                                               LocalDateTime transactionDateTime, Enum.RegionCode regionCode) {
        LocalDateTime fromDateTime = transactionDateTime.minusHours(1);
        return repo.countTransactionsWithDifferentRegionInLastHour(creditCardNumber, fromDateTime,
                transactionDateTime, regionCode);
    }

}
