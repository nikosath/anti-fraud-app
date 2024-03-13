package antifraud.domain.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static antifraud.domain.service.TransactionValidation.TransactionStatusEnum.*;
import static antifraud.domain.service.TransactionValidation.TransactionValidationResultEnum.*;

public class TransactionValidation {

    public static TransactionDesignatedStatus determineTransactionStatusByValidating(long amount, boolean isIpBlacklisted, boolean isCreditCardBlacklisted) {

        var validationResults = getValidationResults(amount, isIpBlacklisted, isCreditCardBlacklisted);
        var transactionStatus = determineTransactionStatusWithHighestPrecedence(validationResults);
        String justificationsConcatenated = validationResults.stream()
                .map(result -> result.getStatusJustification())
                .toList().stream().sorted().collect(Collectors.joining(", "));
        return new TransactionDesignatedStatus(transactionStatus, justificationsConcatenated);
    }

    private static TransactionStatusEnum determineTransactionStatusWithHighestPrecedence(Set<TransactionValidationResultEnum> results) {
        return results.stream().sorted().toList().get(0).getTransactionStatus();
    }

    @NotNull
    private static Set<TransactionValidationResultEnum> getValidationResults(long amount, boolean isIpBlacklisted,
                                                                             boolean isCreditCardBlacklisted) {
        var validationResults = checkValidationsForProhibition(amount, isIpBlacklisted, isCreditCardBlacklisted);
        boolean isNotProhibited = validationResults.isEmpty();
        if (isNotProhibited) { // check rest validations
            if (amount <= 200) {
                validationResults.add(AMOUNT_LESS_EQUAL_200);
            } else if (amount <= 1500) {
                validationResults.add(AMOUNT_LESS_EQUAL_1500);
            }
        }
        return validationResults;
    }

    private static Set<TransactionValidationResultEnum> checkValidationsForProhibition(long amount, boolean isIpBlacklisted,
                                                                                       boolean isCreditCardBlacklisted) {
        Set<TransactionValidationResultEnum> results = new HashSet<>();
        if (isIpBlacklisted) {
            results.add(IP_BLACKLISTED);
        }
        if (isCreditCardBlacklisted) {
            results.add(CREDIT_CARD_BLACKLISTED);
        }
        if (amount > 1500) {
            results.add(AMOUNT_OVER_1500);
        }
        return results;
    }


    /**
     * The declaration order of the enum values signifies their precedence during sorting.
     * E.g. sorting a Set holding IP_BLACKLISTED and AMOUNT_LESS_EQUAL_200 in ascending order, will put the former first.
     * Sorting is used in TransactionValidation#validateTransaction.
     * See also java.lang.Enum#compareTo
     */
    @Getter
    @RequiredArgsConstructor
    public enum TransactionValidationResultEnum {
        IP_BLACKLISTED(PROHIBITED, "ip"),
        CREDIT_CARD_BLACKLISTED(PROHIBITED, "card-number"),
        AMOUNT_OVER_1500(PROHIBITED, "amount"),
        AMOUNT_LESS_EQUAL_1500(MANUAL_PROCESSING, "amount"),
        AMOUNT_LESS_EQUAL_200(ALLOWED, "none");

        private final TransactionStatusEnum transactionStatus;
        private final String statusJustification;
    }

    public enum TransactionStatusEnum {
        PROHIBITED, MANUAL_PROCESSING, ALLOWED
    }

    public record TransactionDesignatedStatus(TransactionStatusEnum transactionStatus, String statusJustification) {
    }
}
