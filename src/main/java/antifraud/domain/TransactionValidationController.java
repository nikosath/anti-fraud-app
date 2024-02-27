package antifraud.domain;

import antifraud.domain.TransactionValidation.ValidationResultEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static antifraud.domain.TransactionValidation.ValidationResultEnum.INVALID_AMOUNT;

@Slf4j
@RestController
public class TransactionValidationController {

    @PostMapping("/api/antifraud/transaction")
    public ResponseEntity<ValidationResponse> validateTransaction(@RequestBody ValidationRequest request) {
        log.info("validateTransaction for request: " + request);
        var validationResult = TransactionValidation.validateTransaction(request.amount());
        if (INVALID_AMOUNT.equals(validationResult)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(new ValidationResponse(validationResult));
    }

    public record ValidationRequest(long amount) {
    }

    public record ValidationResponse(ValidationResultEnum result) {
    }
}
