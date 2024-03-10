package antifraud.domain;

import antifraud.domain.datastore.FakeIpAddressEntityDatastore;
import antifraud.domain.service.TransactionValidation.ValidationResultEnum;
import antifraud.domain.web.AntifraudController;
import antifraud.domain.web.AntifraudController.DeleteIpResponse;
import antifraud.domain.web.AntifraudController.IpAddressResponse;
import antifraud.domain.web.AntifraudController.ValidateTransactionRequest;
import antifraud.domain.web.AntifraudController.ValidateTransactionResponse;
import antifraud.security.config.SecurityFilterChainConfig;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static antifraud.TestUtils.*;
import static antifraud.common.Uri.API_ANTIFRAUD_SUSPICIOUS_IP;
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
    @Autowired
    FakeIpAddressEntityDatastore datastore;

    @Test
    @WithMockUser(roles = "MERCHANT")
    void validateTransaction_validAmount_allowed() throws Exception {
        // given
        long amount = 1L;
        var request = createPostRequest(API_ANTIFRAUD_TRANSACTION, new ValidateTransactionRequest(amount), objectMapper);
        // when
        var resultActions = mockMvc.perform(request);
        // then
        resultActions.andExpect(status().isOk());
        assertEquals(ValidationResultEnum.ALLOWED,
                deserializeToType(resultActions, ValidateTransactionResponse.class, objectMapper).result());
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

    @Test
    @WithMockUser(roles = "SUPPORT")
    void createIpAddress_validIp_created() throws Exception {
        // given
        String ip = "169.254.123.229";
        var request = createPostRequest(API_ANTIFRAUD_SUSPICIOUS_IP, new AntifraudController.IpAddressRequest(ip), objectMapper);
        // when
        var resultActions = mockMvc.perform(request);
        // then
        resultActions.andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "SUPPORT")
    void createIpAddress_invalidIp_badRequest() throws Exception {
        // given
        String ip = null;
        var request = createPostRequest(API_ANTIFRAUD_SUSPICIOUS_IP, new AntifraudController.IpAddressRequest(ip), objectMapper);
        // when
        var resultActions = mockMvc.perform(request);
        // then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    @WithAnonymousUser
    void createIpAddress_anonymousUser_unauthorized() throws Exception {
        // given
        String ip = "169.254.123.229";
        var request = createPostRequest(API_ANTIFRAUD_SUSPICIOUS_IP, new AntifraudController.IpAddressRequest(ip), objectMapper);
        // when
        var resultActions = mockMvc.perform(request);
        // then
        resultActions.andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "SUPPORT")
    void getAllIpAddresses_correctAuthUser_ok() throws Exception {
        // given
        String ip1 = "169.254.123.229";
        String ip2 = "169.254.123.230";
        datastore.createIpAddress(ip1);
        datastore.createIpAddress(ip2);
        var request = MockMvcRequestBuilders.get(API_ANTIFRAUD_SUSPICIOUS_IP);
        // when
        var resultActions = mockMvc.perform(request);
        // then
        resultActions.andExpect(status().isOk());
        var type = new TypeReference<List<IpAddressResponse>>() {
        };
        List<IpAddressResponse> list = deserializeToCollectionType(resultActions, objectMapper, type);
        assertEquals(2, list.size());
        assertEquals(ip1, list.get(0).ip());
        assertEquals(ip2, list.get(1).ip());
    }

    @Test
    @WithAnonymousUser
    void getAllIpAddresses_anonymousUser_unauthorized() throws Exception {
        // given
        var request = MockMvcRequestBuilders.get(API_ANTIFRAUD_SUSPICIOUS_IP);
        // when
        var resultActions = mockMvc.perform(request);
        // then
        resultActions.andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "SUPPORT")
    void deleteIpAddress_validIp_succeeds() throws Exception {
        // given
        String ip = "169.254.123.229";
        datastore.createIpAddress(ip);
        // when
        var request = MockMvcRequestBuilders.delete(API_ANTIFRAUD_SUSPICIOUS_IP + "/%s".formatted(ip));
        var resultActions = mockMvc.perform(request);
        // then
        resultActions.andExpect(status().isOk());
        var deleteIpResponse = deserializeToType(resultActions, DeleteIpResponse.class, objectMapper);
        assertEquals("IP %s successfully removed!".formatted(ip), deleteIpResponse.status());
    }

    @Test
    @WithMockUser(roles = "SUPPORT")
    void deleteIpAddress_invalidIp_badRequest() throws Exception {
        // given
        String ip = "169.254.123.01";
        datastore.createIpAddress(ip);
        // when
        var request = MockMvcRequestBuilders.delete(API_ANTIFRAUD_SUSPICIOUS_IP + "/%s".formatted(ip));
        var resultActions = mockMvc.perform(request);
        // then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    @WithAnonymousUser
    void deleteIpAddress_anonymousUser_unauthorized() throws Exception {
        // given
        String ip = "169.254.123.229";
        datastore.createIpAddress(ip);
        // when
        var request = MockMvcRequestBuilders.delete(API_ANTIFRAUD_SUSPICIOUS_IP + "/%s".formatted(ip));
        var resultActions = mockMvc.perform(request);
        // then
        resultActions.andExpect(status().isUnauthorized());
    }

}
