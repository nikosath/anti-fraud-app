package antifraud.domain;

import antifraud.domain.service.TransactionValidation.ValidationResultEnum;
import antifraud.domain.web.AntifraudController;
import antifraud.domain.web.AntifraudController.ValidateTransactionRequest;
import antifraud.domain.web.AntifraudController.ValidateTransactionResponse;
import antifraud.security.config.SecurityFilterChainConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static antifraud.TestUtils.createPostRequest;
import static antifraud.common.Uri.API_ANTIFRAUD_TRANSACTION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser(roles = "MERCHANT")
@Import({SecurityFilterChainConfig.class})
@WebMvcTest(AntifraudController.class)
public class AntifraudControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @ParameterizedTest
    @CsvSource({"150, ALLOWED", "1500, MANUAL_PROCESSING", "15000, PROHIBITED"})
    void validateTransaction_validAmount_properValidationResult(Long amount, ValidationResultEnum validationResult) throws Exception {
        // given
        var request = createPostRequest(API_ANTIFRAUD_TRANSACTION, new ValidateTransactionRequest(amount), objectMapper);

        // when
        var resultActions = mockMvc.perform(request);

        // then
        resultActions.andExpect(status().isOk());
        var expected = new ValidateTransactionResponse(validationResult);
        assertEquals(expected, getValidationResponse(resultActions));
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L})
    void validateTransaction_invalidAmount_isBadRequest(Long amount) throws Exception {
        // given
        var request = createPostRequest(API_ANTIFRAUD_TRANSACTION, new ValidateTransactionRequest(amount), objectMapper);

        // when
        var resultActions = mockMvc.perform(request);

        // then
        resultActions.andExpect(status().isBadRequest());
    }

    private ValidateTransactionResponse getValidationResponse(ResultActions result) throws Exception {
        String responseAsString = result.andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(responseAsString, ValidateTransactionResponse.class);
    }
}
