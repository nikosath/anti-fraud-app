package antifraud.web;

import antifraud.domain.TransactionValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static antifraud.domain.TransactionValidation.ValidationResult.INVALID_AMOUNT;

@RestController
public class TransactionValidationController {

    Logger log = LoggerFactory.getLogger(TransactionValidationController.class);

    @PostMapping("/api/antifraud/transaction")
    public ResponseEntity<ValidationResponse> validateTransaction(@RequestBody ValidationRequest request) {
        log.info("validateTransaction for request: " + request);
        var validationResult = TransactionValidation.validateTransaction(request.amount());
        if (INVALID_AMOUNT == validationResult) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(new ValidationResponse(validationResult));
    }

    public static record ValidationRequest(long amount) {
    }

    public static record ValidationResponse(TransactionValidation.ValidationResult result) {
    }
}
