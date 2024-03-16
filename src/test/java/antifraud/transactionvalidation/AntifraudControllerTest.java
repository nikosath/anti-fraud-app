package antifraud.transactionvalidation;

import antifraud.TestHelper;
import antifraud.security.config.SecurityFilterChainConfig;
import antifraud.transactionvalidation.datastore.FakeIpAddressEntityDatastore;
import antifraud.transactionvalidation.datastore.FakeStolenCardEntityDatastore;
import antifraud.transactionvalidation.service.FakeTransactionValidationService;
import antifraud.transactionvalidation.web.AntifraudController;
import antifraud.transactionvalidation.web.AntifraudController.ValidateTransactionRequest;
import antifraud.transactionvalidation.web.AntifraudController.ValidateTransactionResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static antifraud.common.Uri.API_ANTIFRAUD_TRANSACTION;
import static antifraud.transactionvalidation.service.TransactionValidation.TransactionStatusEnum.ALLOWED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import({SecurityFilterChainConfig.class, FakeTransactionValidationService.class})
@WebMvcTest(AntifraudController.class)
class AntifraudControllerTest {

    static TestHelper testHelper;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    FakeTransactionValidationService service;

    @BeforeAll
    static void beforeAll(@Autowired ObjectMapper objectMapper) {
        testHelper = new TestHelper(objectMapper);
    }

    @BeforeEach
    void beforeEach() {
        service.setGetTransactionApprovalStatusBehavior(TestHelper.TestBehaviorEnum.SUCCEEDS);
    }

    @Test
    @WithMockUser(roles = "MERCHANT")
    void validateTransaction_validAmount_allowed() throws Exception {
        // given
        long amount = 1L;
        String ip = "169.254.123.229";
        String cardNumber = "4000008449433403";
        var request = testHelper.createPostRequest(
                API_ANTIFRAUD_TRANSACTION, new ValidateTransactionRequest(amount, ip, cardNumber, RegionCodeEnum.EAP, LocalDateTime.of(2023, 1, 1, 0, 0)));
        // when
        var resultActions = mockMvc.perform(request);
        // then
        resultActions.andExpect(status().isOk());
        var result = testHelper.deserializeToType(resultActions, ValidateTransactionResponse.class).result();
        assertEquals(ALLOWED, result);
    }

    @Test
    @WithMockUser(roles = "MERCHANT")
    void validateTransaction_invalidAmount_badRequest() throws Exception {
        // given
        long amount = 0;
        String ip = "169.254.123.229";
        String cardNumber = "4000008449433403";
        var request = testHelper.createPostRequest(
                API_ANTIFRAUD_TRANSACTION, new ValidateTransactionRequest(amount, ip, cardNumber, RegionCodeEnum.EAP, LocalDateTime.of(2023, 1, 1, 0, 0)));
        // when
        var resultActions = mockMvc.perform(request);
        // then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    @WithAnonymousUser
    void validateTransaction_anonymousUser_unauthorized() throws Exception {
        // given
        long amount = 1L;
        String ip = "169.254.123.229";
        String cardNumber = "4000008449433403";
        var request = testHelper.createPostRequest(
                API_ANTIFRAUD_TRANSACTION, new ValidateTransactionRequest(amount, ip, cardNumber, RegionCodeEnum.EAP, LocalDateTime.of(2023, 1, 1, 0, 0)));
        // when
        var resultActions = mockMvc.perform(request);
        // then
        resultActions.andExpect(status().isUnauthorized());
    }

}
