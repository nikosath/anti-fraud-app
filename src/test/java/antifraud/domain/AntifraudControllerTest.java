package antifraud.domain;

import antifraud.TestHelper;
import antifraud.domain.datastore.FakeIpAddressEntityDatastore;
import antifraud.domain.datastore.FakeStolenCardEntityDatastore;
import antifraud.domain.web.AntifraudController;
import antifraud.domain.web.AntifraudController.ValidateTransactionRequest;
import antifraud.domain.web.AntifraudController.ValidateTransactionResponse;
import antifraud.security.config.SecurityFilterChainConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static antifraud.common.Uri.API_ANTIFRAUD_TRANSACTION;
import static antifraud.domain.service.TransactionValidation.TransactionStatusEnum.ALLOWED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import({SecurityFilterChainConfig.class, FakeIpAddressEntityDatastore.class, FakeStolenCardEntityDatastore.class})
@WebMvcTest(AntifraudController.class)
class AntifraudControllerTest {

    static TestHelper testHelper;
    @Autowired
    MockMvc mockMvc;
//    @Autowired
//    IIpAddressEntityDatastore ipDatastore;
//    @Autowired
//    IStolenCardEntityDatastore cardDatastore;

    @BeforeAll
    static void beforeAll(@Autowired ObjectMapper objectMapper) {
        testHelper = new TestHelper(objectMapper);
    }

    @Test
    @WithMockUser(roles = "MERCHANT")
    void validateTransaction_validAmount_allowed() throws Exception {
        // given
        long amount = 1L;
        String ip = "169.254.123.229";
        String cardNumber = "4000008449433403";
        var request = testHelper.createPostRequest(
                API_ANTIFRAUD_TRANSACTION, new ValidateTransactionRequest(amount, ip, cardNumber));
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
                API_ANTIFRAUD_TRANSACTION, new ValidateTransactionRequest(amount, ip, cardNumber));
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
                API_ANTIFRAUD_TRANSACTION, new ValidateTransactionRequest(amount, ip, cardNumber));
        // when
        var resultActions = mockMvc.perform(request);
        // then
        resultActions.andExpect(status().isUnauthorized());
    }

}
