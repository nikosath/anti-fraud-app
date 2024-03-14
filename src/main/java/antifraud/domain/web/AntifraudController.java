package antifraud.domain.web;

import antifraud.common.Regexp;
import antifraud.common.Uri;
import antifraud.domain.datastore.IIpAddressEntityDatastore;
import antifraud.domain.datastore.IStolenCardEntityDatastore;
import antifraud.domain.service.TransactionValidation;
import antifraud.domain.service.TransactionValidation.TransactionStatusEnum;
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

@Slf4j
@RestController
@RequiredArgsConstructor
public class AntifraudController {

    private final IIpAddressEntityDatastore ipDatastore;
    private final IStolenCardEntityDatastore cardDatastore;

    @PostMapping(Uri.API_ANTIFRAUD_TRANSACTION)
    public ResponseEntity<ValidateTransactionResponse> validateTransaction(@Valid @RequestBody ValidateTransactionRequest request) {
        log.debug("validateTransaction for request: " + request);
        var transactionStatus = TransactionValidation.determineTransactionApprovalStatus(
                request.amount(), isIpBlacklisted(request.ip()), isCreditCardBlacklisted(request.number()));

        return ResponseEntity.ok(new ValidateTransactionResponse(transactionStatus));
    }

    private boolean isCreditCardBlacklisted(String number) {
        return cardDatastore.existsByCardNumber(number);
    }

    private boolean isIpBlacklisted(String ip) {
        return ipDatastore.existsByIp(ip);
    }

    /**
     * @param amount transaction amount
     * @param ip     ip address of the transaction initiator
     * @param number credit card of the transaction initiator
     */
    public record ValidateTransactionRequest(
            @Min(1) long amount,
            @NotBlank @Pattern(regexp = Regexp.IP_ADDRESS) String ip,
            @NotBlank @CreditCardNumber String number) {
    }

    public record ValidateTransactionResponse(TransactionStatusEnum result, String info) {
        ValidateTransactionResponse(TransactionValidation.TransactionApprovalStatus status) {
           this(status.transactionStatus(), status.statusJustification());
        }
    }

}
