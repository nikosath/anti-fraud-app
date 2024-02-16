package antifraud;

import antifraud.domain.TransactionValidation.ValidationResult;
import antifraud.web.TransactionValidationController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class TransactionValidationControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @ParameterizedTest
    @CsvSource({"150, ALLOWED", "1500, MANUAL_PROCESSING", "15000, PROHIBITED"})
    void validateTransaction_validAmount_properValidationResult(Long amount, ValidationResult validationResult) throws Exception {
        // given
        var request = createRequest(amount);

        // when
        var resultActions = mockMvc.perform(request);

        // then
        resultActions.andExpect(status().isOk());
        var expected = new TransactionValidationController.ValidationResponse(validationResult);
        assertEquals(expected, getValidationResponse(resultActions));
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L})
    void validateTransaction_invalidAmount_isBadRequest(Long amount) throws Exception {
        // given
        var request = createRequest(amount);

        // when
        var resultActions = mockMvc.perform(request);

        // then
        resultActions.andExpect(status().isBadRequest());
    }

    private MockHttpServletRequestBuilder createRequest(Long amount) throws JsonProcessingException {
        var validationRequest = new TransactionValidationController.ValidationRequest(amount);
        String requestAsJson = objectMapper.writeValueAsString(validationRequest);
        return post("/api/antifraud/transaction")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestAsJson);
    }

    private TransactionValidationController.ValidationResponse getValidationResponse(ResultActions result) throws Exception {
        String responseAsString = result.andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(responseAsString, TransactionValidationController.ValidationResponse.class);
    }
}
