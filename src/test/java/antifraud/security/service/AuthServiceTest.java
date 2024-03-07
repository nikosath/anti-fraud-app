package antifraud.security.service;

import antifraud.error.ErrorEnum;
import antifraud.security.config.UserPasswordEncoder;
import antifraud.security.storage.InMemoryUserStore;
import antifraud.security.storage.SecurityRoleEnum;
import antifraud.security.storage.UserProfile;
import io.vavr.control.Either;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AuthServiceTest {

    private AuthService authService;

    @BeforeEach
    void beforeEach() {
        authService = new AuthService(new InMemoryUserStore(), new UserPasswordEncoder());
    }

    @Test
    void updateUserRole_succeeds() {
        authService.createUser("Name0", "user0", "pass0"); // create ADMINISTRATOR
        authService.createUser("Name1", "user1", "pass1"); // create MERCHANT

        SecurityRoleEnum role = SecurityRoleEnum.SUPPORT;
        Either<ErrorEnum, UserProfile> either = authService.updateUserRole("user1", role);

        assertEquals(role, either.get().getRole());
    }

}