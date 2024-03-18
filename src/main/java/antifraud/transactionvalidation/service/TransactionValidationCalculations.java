package antifraud.transactionvalidation.service;

import antifraud.error.CustomExceptions;
import antifraud.transactionvalidation.RegionCodeEnum;
import antifraud.transactionvalidation.datastore.TransactionValidationEntity;
import antifraud.transactionvalidation.web.AntifraudController.ValidateTransactionRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

import static antifraud.transactionvalidation.service.TransactionValidationCalculations.TransactionStatusEnum.*;
import static antifraud.transactionvalidation.service.TransactionValidationCalculations.TransactionValidationResultEnum.*;

@Slf4j
public class TransactionValidationCalculations {

    public static TransactionApprovalVerdict getTransactionApprovalVerdict(long amount, boolean isIpBlacklisted,
                                                                           boolean isCreditCardBlacklisted,
                                                                           long countTransactionsWithDifferentIp,
                                                                           long countTransactionsWithDifferentRegion) {
        log.debug("amount = " + amount + ", isIpBlacklisted = " + isIpBlacklisted + ", isCreditCardBlacklisted = " +
                isCreditCardBlacklisted + ", countTransactionsWithDifferentIp = " + countTransactionsWithDifferentIp +
                ", countTransactionsWithDifferentRegion = " + countTransactionsWithDifferentRegion);

        Set<TransactionValidationResultEnum> results = getTransactionValidationResultEnums(amount, isIpBlacklisted,
                isCreditCardBlacklisted, countTransactionsWithDifferentIp, countTransactionsWithDifferentRegion);

        TransactionApprovalVerdict transactionApprovalVerdict = determineTransactionApprovalVerdict(results);

        log.debug("transactionApprovalVerdict = " + transactionApprovalVerdict);
        return transactionApprovalVerdict;
    }

    public static Set<TransactionValidationResultEnum> getTransactionValidationResultEnums(
            long amount, boolean isIpBlacklisted, boolean isCreditCardBlacklisted,
            long countTransactionsWithDifferentIp, long countTransactionsWithDifferentRegion) {

        Set<TransactionValidationResultEnum> results = new HashSet<>();
        results.addAll(
                checkBlacklistViolations(isIpBlacklisted, isCreditCardBlacklisted));
        results.add(checkAmountViolations(amount));
        results.addAll(
                checkTransactionHistoryValidations(countTransactionsWithDifferentIp, countTransactionsWithDifferentRegion));
        log.debug("results = " + results);
        return results;
    }

    public static TransactionApprovalVerdict determineTransactionApprovalVerdict(Set<TransactionValidationResultEnum> results) {
        Map<TransactionStatusEnum, List<TransactionValidationResultEnum>> statusToResults =
                results.stream().collect(
                        Collectors.groupingBy(result -> result.getTransactionStatus()));

        if (statusToResults.get(PROHIBITED) != null) {
            String justificationsConcatenated = toJustificationsConcatenated(statusToResults.get(PROHIBITED));
            return new TransactionApprovalVerdict(PROHIBITED, justificationsConcatenated);
        } else if (statusToResults.get(MANUAL_PROCESSING) != null) {
            String justificationsConcatenated = toJustificationsConcatenated(statusToResults.get(MANUAL_PROCESSING));
            return new TransactionApprovalVerdict(MANUAL_PROCESSING, justificationsConcatenated);
        } else {
            if (statusToResults.get(ALLOWED) == null) {
                throw new CustomExceptions.FailedPostconditionException("Expected at least one %s result".formatted(ALLOWED));
            }
            String justificationsConcatenated = toJustificationsConcatenated(statusToResults.get(ALLOWED));
            return new TransactionApprovalVerdict(ALLOWED, justificationsConcatenated);
        }
    }

    public static TransactionValidationEntity toEntity(ValidateTransactionRequest request,
                                                       TransactionApprovalVerdict approvalVerdict) {
        return new TransactionValidationEntity(request.amount(), request.ip(), request.number(), request.region(),
                request.date(), approvalVerdict.transactionStatus(), approvalVerdict.statusJustification());
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

    private static TransactionValidationResultEnum checkAmountViolations(long amount) {
        if (amount <= 200) {
            return AMOUNT_LESS_EQUAL_200;
        } else if (amount <= 1500) {
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


    private static int countTransactionsWithDifferentIp(String ipAddress, List<TransactionValidationEntity> transactions) {
        return transactions.stream().map(transaction -> transaction.getIpAddress()).filter(ip -> !ip.equals(ipAddress)).collect(Collectors.toSet()).size();
    }

    private static int countTransactionsWithDifferentRegion(RegionCodeEnum region,
                                                            List<TransactionValidationEntity> transactions) {
        return transactions.stream().map(transaction -> transaction.getRegionCode()).filter(reg -> !reg.equals(region)).collect(Collectors.toSet()).size();
    }

    private static TransactionApprovalVerdict toTransactionApprovalVerdict(Set<TransactionValidationResultEnum> validationResults) {
        var transactionStatus = validationResults.iterator().next().getTransactionStatus();
        String justificationsConcatenated = toJustificationsConcatenated(validationResults);

        return new TransactionApprovalVerdict(transactionStatus, justificationsConcatenated);
    }

    @Getter
    @RequiredArgsConstructor
    public enum TransactionValidationResultEnum {
        IP_BLACKLISTED(PROHIBITED, "ip"),
        CREDIT_CARD_BLACKLISTED(PROHIBITED, "card-number"),
        MORE_THAN_TWO_OTHER_IPADDRESSES(PROHIBITED, "ip-correlation"),
        MORE_THAN_TWO_OTHER_REGIONS(PROHIBITED, "region-correlation"),
        AMOUNT_OVER_1500(PROHIBITED, "amount"),
        TWO_OTHER_IPADDRESSES(MANUAL_PROCESSING, "ip-correlation"),
        TWO_OTHER_REGIONS(MANUAL_PROCESSING, "region-correlation"),
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
