package antifraud.domain;

import static antifraud.domain.TransactionValidation.ValidationResultEnum.*;

public class TransactionValidation {

    public static ValidationResultEnum validateTransaction(long amount) {
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

    public enum ValidationResultEnum {
        ALLOWED, MANUAL_PROCESSING, PROHIBITED, INVALID_AMOUNT
    }
}
