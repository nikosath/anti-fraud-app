package antifraud.domain;

import static antifraud.domain.TransactionValidation.ValidationResult.*;

public class TransactionValidation {

    public static ValidationResult validateTransaction(long amount) {
        if (amount <= 0) {
            return INVALID_AMOUNT;
        } else if (amount <= 200) {
            return ALLOWED;
        } else if (amount <= 1500) {
            return MANUAL_PROCESSING;
        } else {
            return PROHIBITED;
        }
    }

    public enum ValidationResult {
        ALLOWED, MANUAL_PROCESSING, PROHIBITED, INVALID_AMOUNT
    }
}
