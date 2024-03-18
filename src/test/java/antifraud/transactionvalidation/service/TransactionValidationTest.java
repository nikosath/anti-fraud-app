package antifraud.transactionvalidation.service;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TransactionValidationTest {

    @ParameterizedTest
    @CsvSource({"150, ALLOWED, none", "1500, MANUAL_PROCESSING, amount", "15000, PROHIBITED, amount"})
    void validateTransaction_noBlacklisted_correctTransactionStatus(long amount,
                                                                    TransactionValidationCalculations.TransactionStatusEnum expectedStatus,
                                                                    String justification) {
        boolean ipBlacklisted = false;
        boolean creditCardBlacklisted = false;
        long countTransactionsWithDifferentIp = 0;
        long countTransactionsWithDifferentRegion = 0;
        var approvalVerdict = TransactionValidationCalculations.getTransactionApprovalVerdict(amount, ipBlacklisted,
                creditCardBlacklisted, countTransactionsWithDifferentIp, countTransactionsWithDifferentRegion);
        assertEquals(expectedStatus, approvalVerdict.transactionStatus());
        assertEquals(justification, approvalVerdict.statusJustification());
    }

    @ParameterizedTest
    @CsvSource({"150, PROHIBITED, ip", "1500, PROHIBITED, 'ip'", "15000, PROHIBITED, 'amount, ip'"})
    void validateTransaction_ipBlacklisted_correctTransactionStatus(long amount,
                                                                    TransactionValidationCalculations.TransactionStatusEnum expectedStatus,
                                                                    String justification) {
        boolean ipBlacklisted = true;
        boolean creditCardBlacklisted = false;
        long countTransactionsWithDifferentIp = 0;
        long countTransactionsWithDifferentRegion = 0;
        var approvalVerdict = TransactionValidationCalculations.getTransactionApprovalVerdict(amount, ipBlacklisted,
                creditCardBlacklisted, countTransactionsWithDifferentIp, countTransactionsWithDifferentRegion);
        assertEquals(expectedStatus, approvalVerdict.transactionStatus());
        assertEquals(justification, approvalVerdict.statusJustification());
    }

    @ParameterizedTest
    @CsvSource({"150, PROHIBITED, card-number", "1500, PROHIBITED, 'card-number'", "15000, PROHIBITED, 'amount, card-number'"})
    void validateTransaction_creditCardBlacklisted_correctTransactionStatus(long amount,
                                                                            TransactionValidationCalculations.TransactionStatusEnum expectedStatus,
                                                                            String justification) {
        boolean ipBlacklisted = false;
        boolean creditCardBlacklisted = true;
        long countTransactionsWithDifferentIp = 0;
        long countTransactionsWithDifferentRegion = 0;
        var approvalVerdict = TransactionValidationCalculations.getTransactionApprovalVerdict(amount, ipBlacklisted,
                creditCardBlacklisted, countTransactionsWithDifferentIp, countTransactionsWithDifferentRegion);
        assertEquals(expectedStatus, approvalVerdict.transactionStatus());
        assertEquals(justification, approvalVerdict.statusJustification());
    }

    @ParameterizedTest
    @CsvSource(
            {"150, PROHIBITED, 'card-number, ip'", "1500, PROHIBITED, 'card-number, ip'"
                    , "15000, PROHIBITED, 'amount, card-number, ip'"})
    void validateTransaction_ipAndCreditCardBlacklisted_correctTransactionStatus(long amount,
                                                                                 TransactionValidationCalculations.TransactionStatusEnum expectedStatus,
                                                                                 String justification) {
        boolean ipBlacklisted = true;
        boolean creditCardBlacklisted = true;
        long countTransactionsWithDifferentIp = 0;
        long countTransactionsWithDifferentRegion = 0;
        var approvalVerdict = TransactionValidationCalculations.getTransactionApprovalVerdict(amount, ipBlacklisted,
                creditCardBlacklisted, countTransactionsWithDifferentIp, countTransactionsWithDifferentRegion);
        assertEquals(expectedStatus, approvalVerdict.transactionStatus());
        assertEquals(justification, approvalVerdict.statusJustification());
    }

}