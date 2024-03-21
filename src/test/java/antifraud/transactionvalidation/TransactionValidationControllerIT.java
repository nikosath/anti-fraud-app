package antifraud.transactionvalidation;

import antifraud.common.Uri;
import antifraud.security.service.AuthService;
import antifraud.transactionvalidation.web.TransactionValidationController.ValidateTransactionRequest;
import antifraud.transactionvalidation.web.TransactionValidationController.ValidateTransactionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

import static antifraud.security.Enum.LockOperation.UNLOCK;
import static antifraud.transactionvalidation.Enum.TransactionStatus.ALLOWED;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TransactionValidationControllerIT {

    public static final String MERCHANT_USERNAME = "user1";
    public static final String MERCHANT_PASSWORD = "pass1";
    @Autowired
    TestRestTemplate template;
    @Autowired
    AuthService authService;

    @BeforeEach
    void beforeEach() {
        // create first user: ADMINISTRATOR
        authService.createUser("Mr.User0", "user0", "pass0");
        // create next user: MERCHANT
        authService.createUser("Mr.User1", MERCHANT_USERNAME, MERCHANT_PASSWORD);
        // unlock merchant
        authService.updateUserLockStatus("user1", UNLOCK);
    }

    @Test
    void testValidateTransaction() {
        var headers = new HttpHeaders();
        headers.setBasicAuth(MERCHANT_USERNAME, MERCHANT_PASSWORD);
        long amount = 150L;
        String ip = "169.254.123.229";
        String cardNumber = "4000008449433403";
        ValidateTransactionRequest body = new ValidateTransactionRequest(amount, ip, cardNumber, Enum.RegionCode.EAP,
                LocalDateTime.of(2023, 1, 1, 0, 0));
        var request = new HttpEntity<>(body, headers);

        var actual = template.postForEntity(Uri.API_ANTIFRAUD_TRANSACTION, request, ValidateTransactionResponse.class);

        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertEquals(ALLOWED, actual.getBody().result());
    }
}
