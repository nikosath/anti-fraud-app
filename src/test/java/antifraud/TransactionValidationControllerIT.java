package antifraud;

import antifraud.web.TransactionValidationController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import static antifraud.domain.TransactionValidation.ValidationResult.ALLOWED;
import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TransactionValidationControllerIT {

    @Autowired
    TestRestTemplate template;

    @Test
    void testValidateTransaction() {
        String url = "/api/antifraud/transaction";
        TransactionValidationController.ValidationRequest request = new TransactionValidationController.ValidationRequest(150L);
        var actual = template.postForEntity(url, request, TransactionValidationController.ValidationResponse.class);
        assertEquals(ALLOWED, actual.getBody().result());
    }
}
