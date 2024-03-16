package antifraud.transactionvalidation.service;

import antifraud.transactionvalidation.datastore.TransactionValidationEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static antifraud.transactionvalidation.service.TransactionValidation.TransactionStatusEnum.*;
import static antifraud.transactionvalidation.service.TransactionValidation.TransactionValidationResultEnum.*;

@Slf4j
public class TransactionValidation {

    public static TransactionApprovalVerdict determineTransactionApprovalVerdict(long amount, boolean isIpBlacklisted,
                                                                                 boolean isCreditCardBlacklisted,
                                                                                 List<TransactionValidationEntity> transactionValidationHistory) {

        log.debug("amount = " + amount + ", isIpBlacklisted = " + isIpBlacklisted +
                ", isCreditCardBlacklisted = " + isCreditCardBlacklisted +
                ", transactionValidationHistory = " + transactionValidationHistory);

        var validationResults = checkAllTransactionValidations(amount, isIpBlacklisted, isCreditCardBlacklisted);
        var transactionStatus = validationResults.iterator().next().getTransactionStatus();
        String justificationsConcatenated = validationResults.stream()
                .map(result -> result.getStatusJustification())
                .toList().stream().sorted().collect(Collectors.joining(", "));

        return new TransactionApprovalVerdict(transactionStatus, justificationsConcatenated);
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

    public record TransactionApprovalVerdict(TransactionStatusEnum transactionStatus, String statusJustification) {
    }
}
