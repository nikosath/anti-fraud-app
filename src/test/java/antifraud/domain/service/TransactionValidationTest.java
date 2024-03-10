package antifraud.domain.service;

import antifraud.domain.service.TransactionValidation.ValidationResultEnum;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TransactionValidationTest {

    @ParameterizedTest
    @CsvSource({"150, ALLOWED", "1500, MANUAL_PROCESSING", "15000, PROHIBITED", "-1, INVALID_AMOUNT", "0, INVALID_AMOUNT"})
    void validateTransaction_expectCorrectResult(long amount, ValidationResultEnum validationResult) {
        assertEquals(validationResult, TransactionValidation.validateTransaction(amount));
    }

}