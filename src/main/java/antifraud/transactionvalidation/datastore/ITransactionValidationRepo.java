package antifraud.transactionvalidation.datastore;

import antifraud.transactionvalidation.RegionCodeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface ITransactionValidationRepo extends JpaRepository<TransactionValidationEntity, Long> {
    /**
     * @param creditCardNumber
     * @param fromDateTime inclusive
     * @param untilDateTime inclusive
     * @return
     */
    List<TransactionValidationEntity> findByCreditCardNumberAndTransactionDateTimeBetween(
            String creditCardNumber,
            LocalDateTime fromDateTime,
            LocalDateTime untilDateTime
    );
    @Query("SELECT COUNT(DISTINCT t.ipAddress) FROM TransactionValidationEntity t WHERE t.creditCardNumber = :creditCardNumber " +
            "AND t.transactionDateTime BETWEEN :fromDateTime AND :untilDateTime " +
            "AND t.ipAddress != :ipAddress")
    long countTransactionsWithDifferentIpInLastHour(String creditCardNumber, LocalDateTime fromDateTime,
                                                    LocalDateTime untilDateTime, String ipAddress);

//    long countByCreditCardNumberAndTransactionDateTimeBetweenAndIpAddressNot(String creditCardNumber, LocalDateTime fromDateTime,
//                                                                           LocalDateTime untilDateTime, String ipAddress);

    @Query("SELECT COUNT(DISTINCT t.regionCode) FROM TransactionValidationEntity t WHERE t.creditCardNumber = :creditCardNumber " +
            "AND t.transactionDateTime BETWEEN :fromDateTime AND :untilDateTime " +
            "AND t.regionCode != :regionCode")
    long countTransactionsWithDifferentRegionInLastHour(String creditCardNumber, LocalDateTime fromDateTime,
                                                        LocalDateTime untilDateTime,
                                                        RegionCodeEnum regionCode);
//    long countByCreditCardNumberAndTransactionDateTimeBetweenAndRegionCodeNot(String creditCardNumber, LocalDateTime fromDateTime,
//                                                                              LocalDateTime untilDateTime,
//                                                                              RegionCodeEnum regionCode);

}
