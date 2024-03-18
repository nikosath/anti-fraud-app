package antifraud.transactionvalidation.datastore;

import antifraud.transactionvalidation.Enum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static antifraud.transactionvalidation.Enum.RegionCode.*;
import static antifraud.transactionvalidation.Enum.TransactionStatus.ALLOWED;
import static org.junit.jupiter.api.Assertions.assertEquals;


@DataJpaTest
class TransactionValidationDatastoreTest {
    static final String CARD_NUMBER = "4000008449433403";
    static final String DIFFERENT_CARD_NUMBER = "30569309025904";
    static final String IP_ADDRESS = "169.254.123.220";
    static final String DIFFERENT_IP_ADDRESS = "192.168.1.1";
    static final Enum.RegionCode REGION = EAP;
    static final Enum.RegionCode DIFFERENT_REGION = ECA;
    static final String DATETIME = "2022-01-01T00:00:00";

    TransactionValidationDatastore datastore;

    private static TransactionValidationEntity transaction(String ipAddress, String creditCardNumber, Enum.RegionCode region,
                                                           LocalDateTime transactionDateTime) {
        return new TransactionValidationEntity(
                10, ipAddress, creditCardNumber, region, transactionDateTime, ALLOWED, "any");
    }

    @BeforeEach
    void beforeEach(@Autowired ITransactionValidationRepo repo) {
        datastore = new TransactionValidationDatastore(repo);
    }

    @ParameterizedTest
    @CsvSource({"0, 169.254.123.220", "1, 192.168.1.1"})
    void countTransactionsWithDifferentIpInLastHour(long expectedCount, String ipAddress) {
        // given
        var transactionDateTime = LocalDateTime.parse(DATETIME);
        var list = List.of(
                transaction("169.254.123.220", CARD_NUMBER, REGION, transactionDateTime.minusMinutes(0)),
                transaction("169.254.123.220", CARD_NUMBER, REGION, transactionDateTime.minusMinutes(1)),
                transaction("169.254.123.220", CARD_NUMBER, REGION, transactionDateTime.minusMinutes(60)),
                transaction("169.254.123.220", CARD_NUMBER, REGION, transactionDateTime.minusMinutes(61))
        );
        datastore.saveAll(list);
        // when
        long count = datastore.countTransactionsWithDifferentIpInLastHour(CARD_NUMBER, transactionDateTime, ipAddress);
        // then
        assertEquals(expectedCount, count);
    }
    @ParameterizedTest
    @CsvSource({"2,  EAP", "3, ECA"})
    void countTransactionsWithDifferentRegionInLastHour(long expectedCount, Enum.RegionCode regionCode) {
        // given
        var transactionDateTime = LocalDateTime.parse(DATETIME);
        var list = List.of(
                transaction(IP_ADDRESS, CARD_NUMBER, EAP, transactionDateTime.minusMinutes(0)),
                transaction(IP_ADDRESS, CARD_NUMBER, LAC, transactionDateTime.minusMinutes(1)),
                transaction(IP_ADDRESS, CARD_NUMBER, HIC, transactionDateTime.minusMinutes(60)),
                transaction(IP_ADDRESS, CARD_NUMBER, ECA, transactionDateTime.minusMinutes(61))
        );
        datastore.saveAll(list);
        // when
        long count = datastore.countTransactionsWithDifferentRegionInLastHour(CARD_NUMBER, transactionDateTime, regionCode);
        // then
        assertEquals(expectedCount, count);
    }



}