package antifraud.domain;

import antifraud.domain.TransactionValidation.ValidationResult;
import antifraud.domain.TransactionValidationController.ValidationRequest;
import antifraud.domain.TransactionValidationController.ValidationResponse;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser
@Import({SecurityFilterChainConfig.class})
@WebMvcTest(TransactionValidationController.class)
public class TransactionValidationControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @ParameterizedTest
    @CsvSource({"150, ALLOWED", "1500, MANUAL_PROCESSING", "15000, PROHIBITED"})
    void validateTransaction_validAmount_properValidationResult(Long amount, ValidationResult validationResult) throws Exception {
        // given
        var request = createPostRequest("/api/antifraud/transaction", new ValidationRequest(amount), objectMapper);

        // when
        var resultActions = mockMvc.perform(request);

        // then
        resultActions.andExpect(status().isOk());
        var expected = new ValidationResponse(validationResult);
        assertEquals(expected, getValidationResponse(resultActions));
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L})
    void validateTransaction_invalidAmount_isBadRequest(Long amount) throws Exception {
        // given
        var request = createPostRequest("/api/antifraud/transaction", new ValidationRequest(amount), objectMapper);

        // when
        var resultActions = mockMvc.perform(request);

        // then
        resultActions.andExpect(status().isBadRequest());
    }

    private ValidationResponse getValidationResponse(ResultActions result) throws Exception {
        String responseAsString = result.andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(responseAsString, ValidationResponse.class);
    }
}
