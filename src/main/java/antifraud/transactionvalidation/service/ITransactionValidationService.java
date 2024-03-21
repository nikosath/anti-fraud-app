package antifraud.transactionvalidation.service;

import antifraud.error.ErrorEnum;
import antifraud.error.Result;
import antifraud.transactionvalidation.Dto;
import antifraud.transactionvalidation.Enum;
import antifraud.transactionvalidation.datastore.TransactionValidationEntity;

import java.time.LocalDateTime;
import java.util.List;

public interface ITransactionValidationService {
    Dto.TransactionApprovalVerdict getTransactionApprovalVerdict(long amount, String ip, String number,
                                                                 Enum.RegionCode regionCode, LocalDateTime date);


    Result<ErrorEnum, TransactionValidationEntity> updateVerdict(Long transactionId, Enum.TransactionStatus feedback);

    List<TransactionValidationEntity> getTransactionValidationHistoryOrderById();

    List<TransactionValidationEntity> getTransactionValidationHistoryOrderById(String creditCardNumber);
}
