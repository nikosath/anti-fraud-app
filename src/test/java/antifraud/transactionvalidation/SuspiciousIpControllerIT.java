package antifraud.transactionvalidation;

import antifraud.common.Uri;
import antifraud.security.datastore.SecurityRoleEnum;
import antifraud.security.service.IAuthService;
import antifraud.transactionvalidation.web.SuspiciousIpController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import static antifraud.security.LockOperationEnum.UNLOCK;
import static org.junit.jupiter.api.Assertions.assertEquals;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SuspiciousIpControllerIT {

    public static final String SUPPORT_USERNAME = "user1";
    public static final String SUPPORT_PASSWORD = "pass1";
    @Autowired
    TestRestTemplate template;
    @Autowired
    IAuthService authService;

    @BeforeEach
    void beforeEach() {
        // create first user: ADMINISTRATOR
        authService.createUser("Mr.User0", "user0", "pass0");
        // create next user: MERCHANT
        authService.createUser("Mr.User1", SUPPORT_USERNAME, SUPPORT_PASSWORD);
        // unlock merchant
        authService.updateUserLockStatus("user1", UNLOCK);
        // change MERCHANT to SUPPORT
        authService.updateUserRole(SUPPORT_USERNAME, SecurityRoleEnum.SUPPORT);
    }

    @Test
    void testValidateTransaction() {
        var headers = new HttpHeaders();
        headers.setBasicAuth(SUPPORT_USERNAME, SUPPORT_PASSWORD);
        var request = new HttpEntity<>(headers);
        String invalidIp = "192.168.1.";

        var actual = template.exchange(Uri.API_ANTIFRAUD_SUSPICIOUS_IP + "/" + invalidIp, HttpMethod.DELETE, request, SuspiciousIpController.DeleteIpResponse.class);

        assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());
    }
}
