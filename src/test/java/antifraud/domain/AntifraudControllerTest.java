package antifraud.domain;

import antifraud.domain.datastore.FakeIpAddressEntityDatastore;
import antifraud.domain.service.TransactionValidation.ValidationResultEnum;
import antifraud.domain.web.AntifraudController;
import antifraud.domain.web.AntifraudController.ValidateTransactionRequest;
import antifraud.domain.web.AntifraudController.ValidateTransactionResponse;
import antifraud.security.config.SecurityFilterChainConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static antifraud.TestUtils.createPostRequest;
import static antifraud.common.Uri.API_ANTIFRAUD_TRANSACTION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import({SecurityFilterChainConfig.class, FakeIpAddressEntityDatastore.class})
@WebMvcTest(AntifraudController.class)
class AntifraudControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "MERCHANT")
    void validateTransaction_validAmount_okAndAllowed() throws Exception {
        // given
        long amount = 1L;
        var request = createPostRequest(API_ANTIFRAUD_TRANSACTION, new ValidateTransactionRequest(amount), objectMapper);
        // when
        var resultActions = mockMvc.perform(request);
        // then
        resultActions.andExpect(status().isOk());
        assertEquals(ValidationResultEnum.ALLOWED, getValidationResponse(resultActions).result());
    }

    @Test
    @WithMockUser(roles = "MERCHANT")
    void validateTransaction_invalidAmount_badRequest() throws Exception {
        // given
        long amount = 0;
        var request = createPostRequest(API_ANTIFRAUD_TRANSACTION, new ValidateTransactionRequest(amount), objectMapper);
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
        var request = createPostRequest(API_ANTIFRAUD_TRANSACTION, new ValidateTransactionRequest(amount), objectMapper);
        // when
        var resultActions = mockMvc.perform(request);
        // then
        resultActions.andExpect(status().isUnauthorized());
    }

    private ValidateTransactionResponse getValidationResponse(ResultActions result) throws Exception {
        String responseAsString = result.andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(responseAsString, ValidateTransactionResponse.class);
    }
}
