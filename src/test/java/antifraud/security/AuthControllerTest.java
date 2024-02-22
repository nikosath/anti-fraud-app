package antifraud.security;

import antifraud.security.config.RestAuthenticationEntryPoint;
import antifraud.security.config.SecurityConfig;
import antifraud.security.storage.UserProfileStore;
import antifraud.security.web.AuthController;
import antifraud.security.web.AuthController.UserRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static antifraud.TestUtils.createPostRequest;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Import({SecurityConfig.class, RestAuthenticationEntryPoint.class})
@Disabled
@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    UserProfileStore store;

    // TODO: fix and enable
    @Test
//    @WithMockUser
    @WithAnonymousUser
    void createUser() throws Exception {
        var userRequest = new UserRequest("Name2", "user2", "pass2");

        var request = createPostRequest("/api/auth/user", userRequest, objectMapper);

        mockMvc.perform(request)
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser
    void listUsers() throws Exception {
        mockMvc.perform(get("/api/auth/list"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteUser() {
    }
}