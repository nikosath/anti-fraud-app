package antifraud.transactionvalidation.web;

import antifraud.common.Regexp;
import antifraud.common.Uri;
import antifraud.common.WebUtils;
import antifraud.error.ErrorEnum;
import antifraud.error.Result;
import antifraud.transactionvalidation.Dto;
import antifraud.transactionvalidation.Enum;
import antifraud.transactionvalidation.Enum.RegionCode;
import antifraud.transactionvalidation.datastore.TransactionValidationEntity;
import antifraud.transactionvalidation.service.ITransactionValidationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.CreditCardNumber;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TransactionValidationController {

    private final ITransactionValidationService service;

    @PostMapping(Uri.API_ANTIFRAUD_TRANSACTION)
    public ResponseEntity<ValidateTransactionResponse> validateTransaction(@Valid @RequestBody ValidateTransactionRequest request) {
        log.debug("validateTransaction for request: " + request);
        var verdict = service.getTransactionApprovalVerdict(request.amount(), request.ip(), request.number(),
                request.region(), request.date());
        return ResponseEntity.ok(new ValidateTransactionResponse(verdict));
    }

    @PutMapping(Uri.API_ANTIFRAUD_TRANSACTION)
    public ResponseEntity<ValidationFeedbackResponse> provideValidationFeedback(@Valid @RequestBody ValidationFeedbackRequest request) {
        log.debug("provideValidationFeedback for request: " + request);
        Result<ErrorEnum, TransactionValidationEntity> result = service.overrideVerdict(
                request.transactionId(), request.feedback());
        if (result.isSuccess()) {
            return ResponseEntity.ok(new ValidationFeedbackResponse(result.getSuccess()));
        }
        return WebUtils.errorToResponseEntity(result.getError());
    }

    @GetMapping(Uri.API_ANTIFRAUD_HISTORY)
    public List<ValidationFeedbackResponse> getAllTransactions() {
        return service.getTransactionValidationHistoryOrderById().stream()
                .map(ValidationFeedbackResponse::new)
                .toList();
    }
    @GetMapping(Uri.API_ANTIFRAUD_HISTORY + Uri.CARD_NUMBER)
    public List<ValidationFeedbackResponse> getTransactionsByCardNumber(@NotBlank @CreditCardNumber @PathVariable String cardNumber) {
        return service.getTransactionValidationHistoryOrderById(cardNumber).stream()
                .map(ValidationFeedbackResponse::new)
                .toList();
    }

    /**
     * @param amount transaction amount
     * @param ip     ip address of the transaction initiator
     * @param number credit card used for the transaction
     * @param region geographical region of the transaction initiator
     * @param date   transaction date-time
     */
    public record ValidateTransactionRequest(
            @Min(1) long amount,
            @NotBlank @Pattern(regexp = Regexp.IP_ADDRESS) String ip,
            @NotBlank @CreditCardNumber String number,
            RegionCode region,
            LocalDateTime date) {
    }

    public record ValidateTransactionResponse(Enum.TransactionStatus result, String info) {
        ValidateTransactionResponse(Dto.TransactionApprovalVerdict verdict) {
            this(verdict.transactionStatus(), verdict.statusJustification());
        }
    }

    private record ValidationFeedbackRequest(@Min(1) Long transactionId, Enum.TransactionStatus feedback) {
    }

    /**
     * @param transactionId
     * @param amount        transaction amount
     * @param ip            ip address of the transaction initiator
     * @param number        card number
     * @param region        region of the transaction initiator
     * @param date          transaction date
     * @param result
     * @param feedback
     */
    private record ValidationFeedbackResponse(Long transactionId, Long amount, String ip, String number, RegionCode region,
                                              LocalDateTime date, Enum.TransactionStatus result,
                                              Enum.TransactionStatus feedback) {
        public ValidationFeedbackResponse(TransactionValidationEntity entity) {
            this(entity.getId(), entity.getAmount(), entity.getIpAddress(), entity.getCreditCardNumber(), entity.getRegionCode(),
                    entity.getTransactionDateTime(), entity.getTransactionStatus(), entity.getFeedback());
        }
    }
}
