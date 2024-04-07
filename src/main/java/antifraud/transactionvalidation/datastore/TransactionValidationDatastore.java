package antifraud.transactionvalidation.datastore;

import antifraud.transactionvalidation.Enum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class TransactionValidationDatastore implements ITransactionValidationDatastore {
    private final ITransactionValidationRepo repo;

    @Override
    public TransactionValidationEntity save(TransactionValidationEntity transactionValidationEntity) {
        return repo.save(transactionValidationEntity);
    }

    @Override
    public Optional<TransactionValidationEntity> findById(Long id) {
        Optional<TransactionValidationEntity> entity = repo.findById(id);
        log.debug("findById TransactionValidationEntity: {}", entity);
        return entity;
    }

    @Override
    public List<TransactionValidationEntity> saveAll(Iterable<TransactionValidationEntity> transactionValidationEntity) {
        return repo.saveAll(transactionValidationEntity);
    }

    @Override
    public List<TransactionValidationEntity> getTransactionValidationHistory() {
        return repo.findAllByOrderById();
    }
    @Override
    public List<TransactionValidationEntity> getTransactionValidationHistory(String creditCardNumber) {
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
