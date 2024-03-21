package antifraud.transactionvalidation.service;

import antifraud.common.datastore.ConfigEntity;
import antifraud.common.datastore.IConfigRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static antifraud.common.Enum.ConfigCategory.TRANSACTION_VALIDATION;
import static antifraud.transactionvalidation.service.TransactionValidationConfig.PropertyNames.AMOUNT_LIMIT_FOR_ALLOWED;
import static antifraud.transactionvalidation.service.TransactionValidationConfig.PropertyNames.AMOUNT_LIMIT_FOR_MANUAL_PROCESSING;
import static org.junit.jupiter.api.Assertions.*;

@Import(ConfigService.class)
@DataJpaTest
class ConfigServiceTest {

    @Autowired
    ConfigService service;


    @Test
    void getTransactionValidationConfig_expectDefaultConfigState() {
        var config = service.getTransactionValidationConfig();

        assertEquals(200L, config.amountLimitForAllowed());
        assertEquals(1500L, config.amountLimitForManualProcessing());
    }

}