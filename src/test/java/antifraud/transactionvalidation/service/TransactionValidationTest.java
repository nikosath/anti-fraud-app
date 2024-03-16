package antifraud.transactionvalidation.service;

import antifraud.transactionvalidation.service.TransactionValidation.TransactionStatusEnum;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TransactionValidationTest {

    @ParameterizedTest
    @CsvSource({"150, ALLOWED, none", "1500, MANUAL_PROCESSING, amount", "15000, PROHIBITED, amount"})
    void validateTransaction_noBlacklisted_correctTransactionStatus(long amount, TransactionStatusEnum expectedStatus,
                                                                    String justification) {
        boolean ipBlacklisted = false;
        boolean creditCardBlacklisted = false;
        var approvalVerdict = TransactionValidation.determineTransactionApprovalVerdict(amount, ipBlacklisted, creditCardBlacklisted, List.of());
        assertEquals(expectedStatus, approvalVerdict.transactionStatus());
        assertEquals(justification, approvalVerdict.statusJustification());
    }

    @ParameterizedTest
    @CsvSource({"150, PROHIBITED, ip", "1500, PROHIBITED, 'ip'", "15000, PROHIBITED, 'amount, ip'"})
    void validateTransaction_ipBlacklisted_correctTransactionStatus(long amount, TransactionStatusEnum expectedStatus,
                                                                    String justification) {
        boolean ipBlacklisted = true;
        boolean creditCardBlacklisted = false;
        var approvalVerdict = TransactionValidation.determineTransactionApprovalVerdict(amount, ipBlacklisted, creditCardBlacklisted, List.of());
        assertEquals(expectedStatus, approvalVerdict.transactionStatus());
        assertEquals(justification, approvalVerdict.statusJustification());
    }

    @ParameterizedTest
    @CsvSource({"150, PROHIBITED, card-number", "1500, PROHIBITED, 'card-number'", "15000, PROHIBITED, 'amount, card-number'"})
    void validateTransaction_creditCardBlacklisted_correctTransactionStatus(long amount,
                                                                            TransactionStatusEnum expectedStatus,
                                                                            String justification) {
        boolean ipBlacklisted = false;
        boolean creditCardBlacklisted = true;
        var approvalVerdict = TransactionValidation.determineTransactionApprovalVerdict(amount, ipBlacklisted, creditCardBlacklisted, List.of());
        assertEquals(expectedStatus, approvalVerdict.transactionStatus());
        assertEquals(justification, approvalVerdict.statusJustification());
    }

    @ParameterizedTest
    @CsvSource(
            {"150, PROHIBITED, 'card-number, ip'", "1500, PROHIBITED, 'card-number, ip'"
                    , "15000, PROHIBITED, 'amount, card-number, ip'"})
    void validateTransaction_ipAndCreditCardBlacklisted_correctTransactionStatus(long amount,
                                                                                 TransactionStatusEnum expectedStatus,
                                                                                 String justification) {
        boolean ipBlacklisted = true;
        boolean creditCardBlacklisted = true;
        var approvalVerdict = TransactionValidation.determineTransactionApprovalVerdict(amount, ipBlacklisted, creditCardBlacklisted, List.of());
        assertEquals(expectedStatus, approvalVerdict.transactionStatus());
        assertEquals(justification, approvalVerdict.statusJustification());
    }

}