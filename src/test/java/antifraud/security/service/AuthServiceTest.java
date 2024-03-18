package antifraud.security.service;

import antifraud.error.ErrorEnum;
import antifraud.error.Result;
import antifraud.security.Enum;
import antifraud.security.config.UserPasswordEncoder;
import antifraud.security.datastore.FakeUserProfileDatastore;
import antifraud.security.datastore.UserProfileEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AuthServiceTest {

    private AuthService authService;

    @BeforeEach
    void beforeEach() {
        authService = new AuthService(new FakeUserProfileDatastore(), new UserPasswordEncoder());
    }

    @Test
    void updateUserRole_succeeds() {
        authService.createUser("Name0", "user0", "pass0"); // create ADMINISTRATOR
        authService.createUser("Name1", "user1", "pass1"); // create MERCHANT

        Enum.SecurityRole role = Enum.SecurityRole.SUPPORT;
        var result = authService.updateUserRole("user1", role);

        assertEquals(role, result.getSuccess().role());
    }

}