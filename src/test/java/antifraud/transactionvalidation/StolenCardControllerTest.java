package antifraud.transactionvalidation;

import antifraud.TestHelper;
import antifraud.security.config.SecurityFilterChainConfig;
import antifraud.transactionvalidation.datastore.FakeStolenCardEntityDatastore;
import antifraud.transactionvalidation.web.StolenCardController;
import antifraud.transactionvalidation.web.StolenCardController.DeleteStolenCardResponse;
import antifraud.transactionvalidation.web.StolenCardController.StolenCardResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static antifraud.common.Uri.API_ANTIFRAUD_STOLENCARD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import({SecurityFilterChainConfig.class, FakeStolenCardEntityDatastore.class})
@WebMvcTest(StolenCardController.class)
class StolenCardControllerTest {

    public static final String INVALID_CARD_NUMBER = "400000844-9433403";
    public static final String VALID_CARD_NUMBER = "4000008449433403";
    @Autowired
    MockMvc mockMvc;
    @Autowired
    FakeStolenCardEntityDatastore datastore;
    static TestHelper testHelper;

    @BeforeAll
    static void beforeAll(@Autowired ObjectMapper objectMapper) {
        testHelper = new TestHelper(objectMapper);
    }

    @Test
    @WithMockUser(roles = "SUPPORT")
    void createStolenCard_validCardNumber_created() throws Exception {
        // given
        String cardNumber = "4000008449433403";
        var request = testHelper.createPostRequest(API_ANTIFRAUD_STOLENCARD, new StolenCardController.StolenCardRequest(cardNumber));
        // when
        var resultActions = mockMvc.perform(request);
        // then
        resultActions.andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "SUPPORT")
    void createStolenCard_invalidCardNumber_badRequest() throws Exception {
        // given
        String cardNumber = null;
        var request = testHelper.createPostRequest(API_ANTIFRAUD_STOLENCARD, new StolenCardController.StolenCardRequest(cardNumber));
        // when
        var resultActions = mockMvc.perform(request);
        // then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    @WithAnonymousUser
    void createStolenCard_anonymousUser_unauthorized() throws Exception {
        // given
        String cardNumber = "4000008449433403";
        var request = testHelper.createPostRequest(API_ANTIFRAUD_STOLENCARD, new StolenCardController.StolenCardRequest(cardNumber));
        // when
        var resultActions = mockMvc.perform(request);
        // then
        resultActions.andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "SUPPORT")
    void getAllStolenCardes_correctAuthUser_ok() throws Exception {
        // given
        String cardNumber1 = "4000008449433403";
        String cardNumber2 = "169.254.123.230";
        datastore.createStolenCard(cardNumber1);
        datastore.createStolenCard(cardNumber2);
        var request = MockMvcRequestBuilders.get(API_ANTIFRAUD_STOLENCARD);
        // when
        var resultActions = mockMvc.perform(request);
        // then
        resultActions.andExpect(status().isOk());
        var type = new TypeReference<List<StolenCardResponse>>() {
        };
        List<StolenCardResponse> list = testHelper.deserializeToCollectionType(resultActions, type);
        assertEquals(2, list.size());
        assertEquals(cardNumber1, list.get(0).number());
        assertEquals(cardNumber2, list.get(1).number());
    }

    @Test
    @WithAnonymousUser
    void getAllStolenCardes_anonymousUser_unauthorized() throws Exception {
        // given
        var request = MockMvcRequestBuilders.get(API_ANTIFRAUD_STOLENCARD);
        // when
        var resultActions = mockMvc.perform(request);
        // then
        resultActions.andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "SUPPORT")
    void deleteStolenCard_validCardNumber_succeeds() throws Exception {
        // given
        String cardNumber = VALID_CARD_NUMBER;
        datastore.createStolenCard(cardNumber);
        // when
        var request = MockMvcRequestBuilders.delete(API_ANTIFRAUD_STOLENCARD + "/%s".formatted(cardNumber));
        var resultActions = mockMvc.perform(request);
        // then
        resultActions.andExpect(status().isOk());
        var deleteStolenCardResponse = testHelper.deserializeToType(resultActions, DeleteStolenCardResponse.class);
        assertEquals("Card %s successfully removed!".formatted(cardNumber), deleteStolenCardResponse.status());
    }

    @Test
    @WithMockUser(roles = "SUPPORT")
    void deleteStolenCard_invalidCardNumber_badRequest() throws Exception {
        // given
        String cardNumber = INVALID_CARD_NUMBER;
        // when
        var request = MockMvcRequestBuilders.delete(API_ANTIFRAUD_STOLENCARD + "/%s".formatted(cardNumber));
        var resultActions = mockMvc.perform(request);
        // then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    @WithAnonymousUser
    void deleteStolenCard_anonymousUser_unauthorized() throws Exception {
        // given
        String cardNumber = "4000008449433403";
        datastore.createStolenCard(cardNumber);
        // when
        var request = MockMvcRequestBuilders.delete(API_ANTIFRAUD_STOLENCARD + "/%s".formatted(cardNumber));
        var resultActions = mockMvc.perform(request);
        // then
        resultActions.andExpect(status().isUnauthorized());
    }

}
