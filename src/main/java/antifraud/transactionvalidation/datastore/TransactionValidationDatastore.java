package antifraud.transactionvalidation.datastore;

import antifraud.transactionvalidation.Enum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TransactionValidationDatastore implements ITransactionValidationDatastore {
    private final ITransactionValidationRepo repo;

    @Override
    public TransactionValidationEntity save(TransactionValidationEntity transactionValidationEntity) {
        return repo.save(transactionValidationEntity);
    }

    @Override
    public Optional<TransactionValidationEntity> findById(Long id) {
        return repo.findById(id);
    }

    @Override
    public List<TransactionValidationEntity> saveAll(Iterable<TransactionValidationEntity> transactionValidationEntity) {
        return repo.saveAll(transactionValidationEntity);
    }

    @Override
    public List<TransactionValidationEntity> getTransactionValidationHistoryOrderById() {
        return repo.findAllByOrderById();
    }
    @Override
    public List<TransactionValidationEntity> getTransactionValidationHistoryOrderById(String creditCardNumber) {
        return repo.findByCreditCardNumberOrderById(creditCardNumber);
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
