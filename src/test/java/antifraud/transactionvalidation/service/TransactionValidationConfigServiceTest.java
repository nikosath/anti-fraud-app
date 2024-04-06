package antifraud.transactionvalidation.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.*;

@Import(TransactionValidationConfigService.class)
@DataJpaTest
class TransactionValidationConfigServiceTest {

    @Autowired
    TransactionValidationConfigService service;


    @Test
    void getTransactionValidationConfig_expectDefaultConfigState() {
        var config = service.getTransactionValidationConfig();

        assertEquals(200L, config.amountLimitForAllowed());
        assertEquals(1500L, config.amountLimitForManualProcessing());
    }

}