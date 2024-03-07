package antifraud.domain;

import antifraud.common.Uri;
import antifraud.domain.TransactionValidation.ValidationResultEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static antifraud.domain.TransactionValidation.ValidationResultEnum.INVALID_AMOUNT;

@Slf4j
@RestController
public class AntifraudController {

    @PostMapping(Uri.API_ANTIFRAUD_TRANSACTION)
    public ResponseEntity<ValidateTransactionResponse> validateTransaction(@RequestBody ValidateTransactionRequest request) {
        log.info("validateTransaction for request: " + request);
        var validationResult = TransactionValidation.validateTransaction(request.amount());
        if (INVALID_AMOUNT.equals(validationResult)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(new ValidateTransactionResponse(validationResult));
    }

    public record ValidateTransactionRequest(long amount) {
    }

    public record ValidateTransactionResponse(ValidationResultEnum result) {
    }
}
