package antifraud.security;

import antifraud.security.config.SecurityFilterChainConfig;
import antifraud.security.service.FakeAuthService;
import antifraud.security.service.FakeAuthService.BehaviorEnum;
import antifraud.security.web.AuthController;
import antifraud.security.web.AuthController.UserRequest;
import antifraud.security.web.AuthController.UserResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static antifraud.TestUtils.createPostRequest;
import static antifraud.common.Uri.API_AUTH_LIST;
import static antifraud.common.Uri.API_AUTH_USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Import({SecurityFilterChainConfig.class, FakeAuthService.class})
@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    FakeAuthService fakeAuthService;

    @Test
    void createUser_succeeds() throws Exception {
        // given
        fakeAuthService.setCreateUserBehavior(BehaviorEnum.SUCCEEDS);
        var userRequest = new UserRequest("Name2", "user2", "pass2");
        var request = createPostRequest(API_AUTH_USER, userRequest, objectMapper);

        // when
        mockMvc.perform(request)
                .andExpect(status().isCreated()); // then
    }

    @Test
    void listUsers_withoutAuthorizedUser_failsAsUnauthorized() throws Exception {
        mockMvc.perform(get(API_AUTH_LIST))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "SUPPORT")
    void listUsers_returnsExpectedEntities() throws Exception {
        fakeAuthService.setListUsersBehavior(BehaviorEnum.RETURNS_2_ENTITIES);

        var resultActions = mockMvc.perform(get(API_AUTH_LIST));

        resultActions.andExpect(status().isOk());
        assertEquals(2, getList(resultActions).size());
    }

    private List<UserResponse> getList(ResultActions result) throws Exception {
        String responseAsString = result.andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(responseAsString, new TypeReference<>() {
        });
    }

    @Test
    void deleteUser_withoutAuthorizedUser_failsAsUnauthorized() throws Exception {
        mockMvc.perform(delete(API_AUTH_USER + "/user1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATOR")
    void deleteUser_withAuthorizedUser_succeeds() throws Exception {
        fakeAuthService.setDeleteUserBehavior(BehaviorEnum.SUCCEEDS);

        mockMvc.perform(delete(API_AUTH_USER + "/user1"))
                .andExpect(status().isOk());
    }
}