package antifraud.domain;

import antifraud.domain.TransactionValidationController.ValidationRequest;
import antifraud.domain.TransactionValidationController.ValidationResponse;
import antifraud.security.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import static antifraud.domain.TransactionValidation.ValidationResultEnum.ALLOWED;
import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TransactionValidationControllerIT {

    @Autowired
    TestRestTemplate template;
    @Autowired
    AuthService authService;

    @Test
    void testValidateTransaction() {
        authService.createUser("Name1", "user1", "pass1");
        var headers = new HttpHeaders();
        headers.setBasicAuth("user1", "pass1");
        var request = new HttpEntity<>(new ValidationRequest(150L), headers) ;

        var actual = template.postForEntity("/api/antifraud/transaction", request, ValidationResponse.class);

        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertEquals(ALLOWED, actual.getBody().result());
    }
}
