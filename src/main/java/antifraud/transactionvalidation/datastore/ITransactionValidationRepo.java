package antifraud.transactionvalidation.datastore;

import antifraud.transactionvalidation.Enum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface ITransactionValidationRepo extends JpaRepository<TransactionValidationEntity, Long> {


    List<TransactionValidationEntity> findAllByOrderById();

    List<TransactionValidationEntity> findByCreditCardNumberOrderById(String creditCardNumber);

    @Query("SELECT COUNT(DISTINCT t.ipAddress) FROM TransactionValidationEntity t WHERE t.creditCardNumber = :creditCardNumber " +
            "AND t.transactionDateTime BETWEEN :fromDateTime AND :untilDateTime " +
            "AND t.ipAddress != :ipAddress")
    long countTransactionsWithDifferentIpInLastHour(String creditCardNumber, LocalDateTime fromDateTime,
                                                    LocalDateTime untilDateTime, String ipAddress);


    @Query("SELECT COUNT(DISTINCT t.regionCode) FROM TransactionValidationEntity t WHERE t.creditCardNumber = :creditCardNumber" +
            " AND t.transactionDateTime BETWEEN :fromDateTime AND :untilDateTime" +
            " AND t.regionCode != :regionCode")
    long countTransactionsWithDifferentRegionInLastHour(String creditCardNumber, LocalDateTime fromDateTime,
                                                        LocalDateTime untilDateTime,
                                                        Enum.RegionCode regionCode);
}
