package antifraud.transactionvalidation.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static antifraud.transactionvalidation.service.TransactionValidation.TransactionStatusEnum.*;
import static antifraud.transactionvalidation.service.TransactionValidation.TransactionValidationResultEnum.*;

public class TransactionValidation {

    public static TransactionApprovalStatus determineTransactionApprovalStatus(long amount, boolean isIpBlacklisted,
                                                                               boolean isCreditCardBlacklisted) {

        var validationResults = checkAllTransactionValidations(amount, isIpBlacklisted, isCreditCardBlacklisted);
        var transactionStatus = validationResults.iterator().next().getTransactionStatus();
        String justificationsConcatenated = validationResults.stream()
                .map(result -> result.getStatusJustification())
                .toList().stream().sorted().collect(Collectors.joining(", "));

        return new TransactionApprovalStatus(transactionStatus, justificationsConcatenated);
    }

    @NotNull
    private static Set<TransactionValidationResultEnum> checkAllTransactionValidations(long amount, boolean isIpBlacklisted,
                                                                                       boolean isCreditCardBlacklisted) {
        var resultsForProhibition = checkValidationsForProhibition(amount, isIpBlacklisted, isCreditCardBlacklisted);
        return resultsForProhibition.isEmpty() ? checkOtherValidations(amount) : resultsForProhibition;
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

    private static Set<TransactionValidationResultEnum> checkOtherValidations(long amount) {
        Set<TransactionValidationResultEnum> validationResults = new HashSet<>();
        if (amount <= 200) {
            validationResults.add(AMOUNT_LESS_EQUAL_200);
        } else if (amount <= 1500) {
            validationResults.add(AMOUNT_LESS_EQUAL_1500);
        }
        return validationResults;
    }

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

    public record TransactionApprovalStatus(TransactionStatusEnum transactionStatus, String statusJustification) {
    }
}
