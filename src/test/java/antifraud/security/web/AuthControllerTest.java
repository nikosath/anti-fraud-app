package antifraud.security.web;

import antifraud.TestHelper;
import antifraud.security.Enum;
import antifraud.security.config.SecurityFilterChainConfig;
import antifraud.security.service.FakeAuthService;
import antifraud.security.web.AuthController.LockStatusRequest;
import antifraud.security.web.AuthController.UserRequest;
import antifraud.security.web.AuthController.UserResponse;
import antifraud.security.web.AuthController.UserRoleRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static antifraud.common.Uri.*;
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
    FakeAuthService fakeAuthService;
    static TestHelper testHelper;

    @BeforeAll
    static void beforeAll(@Autowired ObjectMapper objectMapper) {
        testHelper = new TestHelper(objectMapper);
    }

    @Test
    void createUser_succeeds() throws Exception {
        // given
        fakeAuthService.setCreateUserBehavior(TestHelper.TestBehaviorEnum.SUCCEEDS);
        var payload = new UserRequest("Name2", "user2", "pass2");
        var request = testHelper.createPostRequest(API_AUTH_USER, payload);

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
        fakeAuthService.setListUsersBehavior(TestHelper.TestBehaviorEnum.RETURNS_2_ENTITIES);

        var resultActions = mockMvc.perform(get(API_AUTH_LIST));

        resultActions.andExpect(status().isOk());
        var type = new TypeReference<List<UserResponse>>() {
        };
        List<UserResponse> list = testHelper.deserializeToCollectionType(resultActions, type);
        assertEquals(2, list.size());
    }

    @Test
    void deleteUser_withoutAuthorizedUser_failsAsUnauthorized() throws Exception {
        mockMvc.perform(delete(API_AUTH_USER + "/user1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATOR")
    void deleteUser_withAuthorizedUser_succeeds() throws Exception {
        fakeAuthService.setDeleteUserBehavior(TestHelper.TestBehaviorEnum.SUCCEEDS);

        mockMvc.perform(delete(API_AUTH_USER + "/user1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATOR")
    void updateUserRole_withAuthorizedUser_succeeds() throws Exception {
        fakeAuthService.setUpdateUserRoleBehavior(TestHelper.TestBehaviorEnum.SUCCEEDS);
        var payload = new UserRoleRequest("user1", Enum.SecurityRole.MERCHANT);
        var request = testHelper.createPutRequest(API_AUTH_ROLE, payload);

        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATOR")
    void updateUserLockStatus_withAuthorizedUser_succeeds() throws Exception {
        fakeAuthService.setUpdateUserLockStatusBehavior(TestHelper.TestBehaviorEnum.SUCCEEDS);
        var payload = new LockStatusRequest("user1", Enum.LockOperation.UNLOCK);
        var request = testHelper.createPutRequest(API_AUTH_ACCESS, payload);

        mockMvc.perform(request)
                .andExpect(status().isOk());
    }
}