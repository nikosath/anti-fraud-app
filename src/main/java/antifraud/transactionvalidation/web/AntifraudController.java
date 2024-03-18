package antifraud.transactionvalidation.web;

import antifraud.common.Regexp;
import antifraud.common.Uri;
import antifraud.transactionvalidation.RegionCodeEnum;
import antifraud.transactionvalidation.service.ITransactionValidationService;
import antifraud.transactionvalidation.service.TransactionValidationCalculations;
import antifraud.transactionvalidation.service.TransactionValidationCalculations.TransactionStatusEnum;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.CreditCardNumber;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AntifraudController {

    private final ITransactionValidationService service;

    @PostMapping(Uri.API_ANTIFRAUD_TRANSACTION)
    public ResponseEntity<ValidateTransactionResponse> validateTransaction(@Valid @RequestBody ValidateTransactionRequest request) {
        log.debug("validateTransaction for request: " + request);
        var transactionStatus = service.getTransactionApprovalStatus(request);
        return ResponseEntity.ok(new ValidateTransactionResponse(transactionStatus));
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
            RegionCodeEnum region,
            LocalDateTime date) {
    }

    public record ValidateTransactionResponse(TransactionStatusEnum result, String info) {
        ValidateTransactionResponse(TransactionValidationCalculations.TransactionApprovalVerdict verdict) {
            this(verdict.transactionStatus(), verdict.statusJustification());
        }
    }

}
