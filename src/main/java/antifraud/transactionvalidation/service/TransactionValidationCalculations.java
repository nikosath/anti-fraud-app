package antifraud.transactionvalidation.service;

import antifraud.error.CustomExceptions;
import antifraud.transactionvalidation.Dto;
import antifraud.transactionvalidation.Enum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

import static antifraud.transactionvalidation.Enum.TransactionStatus.*;
import static antifraud.transactionvalidation.service.TransactionValidationCalculations.TransactionValidationResultEnum.*;

@Slf4j
public class TransactionValidationCalculations {

    public static Dto.TransactionApprovalVerdict getTransactionApprovalVerdict(long amount, boolean isIpBlacklisted,
                                                                               boolean isCreditCardBlacklisted,
                                                                               long countTransactionsWithDifferentIp,
                                                                               long countTransactionsWithDifferentRegion,
                                                                               long amountLimitForAllowed,
                                                                               long amountLimitForManualProcessing) {

        log.debug("amount = " + amount + ", isIpBlacklisted = " + isIpBlacklisted + ", isCreditCardBlacklisted = " +
                isCreditCardBlacklisted + ", countTransactionsWithDifferentIp = " + countTransactionsWithDifferentIp +
                ", countTransactionsWithDifferentRegion = " + countTransactionsWithDifferentRegion);

        Set<TransactionValidationResultEnum> results = getTransactionValidationResults(amount, isIpBlacklisted, isCreditCardBlacklisted, countTransactionsWithDifferentIp, countTransactionsWithDifferentRegion, amountLimitForAllowed, amountLimitForManualProcessing);
        log.debug("results = " + results);
        Dto.TransactionApprovalVerdict transactionApprovalVerdict = determineTransactionApprovalVerdict(results);
        log.debug("transactionApprovalVerdict = " + transactionApprovalVerdict);
        return transactionApprovalVerdict;
    }

    private static Set<TransactionValidationResultEnum> getTransactionValidationResults(long amount, boolean isIpBlacklisted, boolean isCreditCardBlacklisted, long countTransactionsWithDifferentIp, long countTransactionsWithDifferentRegion, long amountLimitForAllowed, long amountLimitForManualProcessing) {
        Set<TransactionValidationResultEnum> results = new HashSet<>();
        results.addAll(
                checkBlacklistViolations(isIpBlacklisted, isCreditCardBlacklisted));
        results.add(checkAmountViolations(amount, amountLimitForAllowed, amountLimitForManualProcessing));
        results.addAll(
                checkTransactionHistoryValidations(countTransactionsWithDifferentIp, countTransactionsWithDifferentRegion));
        return results;
    }

    private static Dto.TransactionApprovalVerdict determineTransactionApprovalVerdict(Set<TransactionValidationResultEnum> results) {
        Map<Enum.TransactionStatus, List<TransactionValidationResultEnum>> statusToResults =
                results.stream().collect(
                        Collectors.groupingBy(result -> result.getTransactionStatus()));

        if (statusToResults.get(PROHIBITED) != null) {
            String justificationsConcatenated = toJustificationsConcatenated(statusToResults.get(PROHIBITED));
            return new Dto.TransactionApprovalVerdict(PROHIBITED, justificationsConcatenated);
        } else if (statusToResults.get(MANUAL_PROCESSING) != null) {
            String justificationsConcatenated = toJustificationsConcatenated(statusToResults.get(MANUAL_PROCESSING));
            return new Dto.TransactionApprovalVerdict(MANUAL_PROCESSING, justificationsConcatenated);
        } else {
            if (statusToResults.get(ALLOWED) == null) {
                throw new CustomExceptions.FailedPostconditionException("Missing %s result".formatted(ALLOWED));
            }
            String justificationsConcatenated = toJustificationsConcatenated(statusToResults.get(ALLOWED));
            return new Dto.TransactionApprovalVerdict(ALLOWED, justificationsConcatenated);
        }
    }

    private static String toJustificationsConcatenated(Collection<TransactionValidationResultEnum> validationResults) {
        return validationResults.stream().map(result -> result.getStatusJustification()).distinct().sorted().collect(Collectors.joining(", "));
    }

    private static Set<TransactionValidationResultEnum> checkBlacklistViolations(boolean isIpBlacklisted,
                                                                                 boolean isCreditCardBlacklisted) {
        Set<TransactionValidationResultEnum> results = new HashSet<>();
        if (isIpBlacklisted) {
            results.add(IP_BLACKLISTED);
        }
        if (isCreditCardBlacklisted) {
            results.add(CREDIT_CARD_BLACKLISTED);
        }
        return results;
    }

    private static TransactionValidationResultEnum checkAmountViolations(long amount, long amountLimitForAllowed,
                                                                         long amountLimitForManualProcessing) {
        if (amount <= amountLimitForAllowed) {
            return AMOUNT_LESS_EQUAL_200;
        } else if (amount <= amountLimitForManualProcessing) {
            return AMOUNT_LESS_EQUAL_1500;
        } else {
            return AMOUNT_OVER_1500;
        }
    }

    private static Set<TransactionValidationResultEnum> checkTransactionHistoryValidations(long countTransactionsWithDifferentIp,
                                                                                           long countTransactionsWithDifferentRegion) {
        Set<TransactionValidationResultEnum> results = new HashSet<>();
        if (countTransactionsWithDifferentIp > 2) {
            results.add(MORE_THAN_TWO_OTHER_IPADDRESSES);
        } else if (countTransactionsWithDifferentIp == 2) {
            results.add(TWO_OTHER_IPADDRESSES);
        }
        if (countTransactionsWithDifferentRegion > 2) {
            results.add(MORE_THAN_TWO_OTHER_REGIONS);
        } else if (countTransactionsWithDifferentRegion == 2) {
            results.add(TWO_OTHER_REGIONS);
        }
        return results;
    }

    /**
     * @return Optional.empty() if no new amount limits are calculated
     */
    public static Optional<TransactionValidationConfig> calculateNewAmountLimits(long transactionAmount, Enum.TransactionStatus transactionStatus, Enum.TransactionStatus feedback,
                                                                                 TransactionValidationConfig currentAmountLimits) {
        long amountLimitForAllowed = currentAmountLimits.amountLimitForAllowed();
        long amountLimitForManualProcessing = currentAmountLimits.amountLimitForManualProcessing();

        if (transactionStatus == ALLOWED && feedback == MANUAL_PROCESSING) {
            amountLimitForAllowed = decreaseAmountLimit(transactionAmount, amountLimitForAllowed);
        } else  if (transactionStatus == ALLOWED && feedback == PROHIBITED) {
            amountLimitForAllowed = decreaseAmountLimit(transactionAmount, amountLimitForAllowed);
            amountLimitForManualProcessing = decreaseAmountLimit(transactionAmount, amountLimitForManualProcessing);
        } else if (transactionStatus == MANUAL_PROCESSING && feedback == ALLOWED) {
            amountLimitForAllowed = increaseAmountLimit(transactionAmount, amountLimitForAllowed);
        } else if (transactionStatus == MANUAL_PROCESSING && feedback == PROHIBITED) {
            amountLimitForManualProcessing = decreaseAmountLimit(transactionAmount, amountLimitForManualProcessing);
        } else if (transactionStatus == PROHIBITED && feedback == ALLOWED) {
            amountLimitForAllowed = increaseAmountLimit(transactionAmount, amountLimitForAllowed);
            amountLimitForManualProcessing = increaseAmountLimit(transactionAmount, amountLimitForManualProcessing);
        } else if (transactionStatus == PROHIBITED && feedback == MANUAL_PROCESSING) {
            amountLimitForManualProcessing = increaseAmountLimit(transactionAmount, amountLimitForManualProcessing);
        } else {
            return Optional.empty();
        }
        return Optional.of(new TransactionValidationConfig(amountLimitForAllowed, amountLimitForManualProcessing));
    }

    private static long decreaseAmountLimit(long transactionAmount, long amountLimitForAllowed) {
        return Math.round(0.8 * amountLimitForAllowed - 0.2 * transactionAmount);
    }

    private static long increaseAmountLimit(long transactionAmount, long amountLimitForAllowed) {
        return Math.round(0.8 * amountLimitForAllowed + 0.2 * transactionAmount);
    }

    @Getter
    @RequiredArgsConstructor
    enum TransactionValidationResultEnum {
        IP_BLACKLISTED(PROHIBITED, "ip"),
        CREDIT_CARD_BLACKLISTED(PROHIBITED, "card-number"),
        MORE_THAN_TWO_OTHER_IPADDRESSES(PROHIBITED, "ip-correlation"),
        MORE_THAN_TWO_OTHER_REGIONS(PROHIBITED, "region-correlation"),
        AMOUNT_OVER_1500(PROHIBITED, "amount"),
        TWO_OTHER_IPADDRESSES(MANUAL_PROCESSING, "ip-correlation"),
        TWO_OTHER_REGIONS(MANUAL_PROCESSING, "region-correlation"),
        AMOUNT_LESS_EQUAL_1500(MANUAL_PROCESSING, "amount"),
        AMOUNT_LESS_EQUAL_200(ALLOWED, "none");

        private final Enum.TransactionStatus transactionStatus;
        private final String statusJustification;
    }

}
