package antifraud.domain;

import antifraud.TestHelper;
import antifraud.domain.datastore.FakeIpAddressEntityDatastore;
import antifraud.domain.web.SuspiciousIpController;
import antifraud.domain.web.SuspiciousIpController.DeleteIpResponse;
import antifraud.domain.web.SuspiciousIpController.IpAddressResponse;
import antifraud.security.config.SecurityFilterChainConfig;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static antifraud.common.Uri.API_ANTIFRAUD_SUSPICIOUS_IP;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import({SecurityFilterChainConfig.class, FakeIpAddressEntityDatastore.class})
@WebMvcTest(SuspiciousIpController.class)
class SuspiciousIpControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    FakeIpAddressEntityDatastore datastore;
    TestHelper testHelper;

    @BeforeEach
    void beforeEach() {
        this.testHelper = new TestHelper(objectMapper);
    }

    @Test
    @WithMockUser(roles = "SUPPORT")
    void createIpAddress_validIp_created() throws Exception {
        // given
        String ip = "169.254.123.229";
        var request = testHelper.createPostRequest(API_ANTIFRAUD_SUSPICIOUS_IP, new SuspiciousIpController.IpAddressRequest(ip));
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
        var request = testHelper.createPostRequest(API_ANTIFRAUD_SUSPICIOUS_IP, new SuspiciousIpController.IpAddressRequest(ip));
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
        var request = testHelper.createPostRequest(API_ANTIFRAUD_SUSPICIOUS_IP, new SuspiciousIpController.IpAddressRequest(ip));
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
        List<IpAddressResponse> list = testHelper.deserializeToCollectionType(resultActions, type);
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
        var deleteIpResponse = testHelper.deserializeToType(resultActions, DeleteIpResponse.class);
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
