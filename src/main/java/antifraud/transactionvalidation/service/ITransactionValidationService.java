package antifraud.transactionvalidation.service;

import antifraud.transactionvalidation.Dto;
import antifraud.transactionvalidation.Enum;
import antifraud.transactionvalidation.web.AntifraudController;

import java.time.LocalDateTime;

public interface ITransactionValidationService {
    Dto.TransactionApprovalVerdict getTransactionApprovalStatus(long amount, String ip, String number,
                                                                Enum.RegionCode regionCode, LocalDateTime date);
}
