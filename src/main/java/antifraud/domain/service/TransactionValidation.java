package antifraud.domain.service;

import static antifraud.domain.service.TransactionValidation.ValidationResultEnum.*;

public class TransactionValidation {

    // TODO: move to AntifraudService
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
