package antifraud.security.service;

import antifraud.error.ErrorEnum;
import antifraud.error.Result;
import antifraud.security.LockOperationEnum;
import antifraud.security.datastore.IUserProfileStore;
import antifraud.security.datastore.SecurityRoleEnum;
import antifraud.security.datastore.UserProfile;
import antifraud.security.datastore.UserProfileFactory;
import io.vavr.control.Either;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static antifraud.error.ErrorEnum.*;
import static antifraud.security.datastore.SecurityRoleEnum.MERCHANT;
import static antifraud.security.datastore.SecurityRoleEnum.SUPPORT;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService extends IAuthService {

    public static final Set<SecurityRoleEnum> ALLOWED_ROLES = Set.of(SUPPORT, MERCHANT);
    private final IUserProfileStore userProfileStore;
    private final PasswordEncoder passwordEncoder;
    private boolean isFirstUserCreation;

    @PostConstruct
    void init() {
        isFirstUserCreation = userProfileStore.count() == 0;
    }

    @Override
    public Either<ErrorEnum, UserProfile> createUser(String name, String username, String password) {
        if (userProfileStore.existsByUsernameIgnoreCase(username)) {
            return Result.error(ENTITY_ALREADY_EXISTS);
        }

        String encodedPassword = passwordEncoder.encode(password);
        UserProfile userProfile;
        if (isFirstUserCreation) {
            userProfile = UserProfileFactory.newAdmin(name, username, encodedPassword);
            isFirstUserCreation = false;
        } else {
            userProfile = UserProfileFactory.newMerchant(name, username, encodedPassword);
        }

        UserProfile saved = userProfileStore.save(userProfile);
        return Result.success(saved);
    }

    @Override
    public List<UserProfile> listUsers() {
        return userProfileStore.findAllByOrderByIdAsc();
    }

    @Override
    public Either<ErrorEnum, UserProfile> deleteUser(String username) {
        if (!userProfileStore.existsByUsernameIgnoreCase(username)) {
            return Result.error(ENTITY_NOT_FOUND);
        }
        var deleted = userProfileStore.deleteByUsernameIgnoreCase(username).orElseThrow();
        return Result.success(deleted);
    }

    @Override
    public Either<ErrorEnum, UserProfile> updateUserRole(String username, SecurityRoleEnum role) {
        Either<ErrorEnum, UserProfile> either = validateAndGetUser(username, role);
        return either.map(user -> {
            user.setRole(role);
            return userProfileStore.save(user);
        });
    }

    @Override
    public Either<ErrorEnum, LockOperationEnum> updateUserLockStatus(String username, LockOperationEnum operation) {
        Optional<UserProfile> userOpt = userProfileStore.findByUsernameIgnoreCase(username);
        if (userOpt.isEmpty()) {
            return Result.error(ENTITY_NOT_FOUND);
        }
        UserProfile user = userOpt.get();
        if (SecurityRoleEnum.ADMINISTRATOR.equals(user.getRole())) {
            return Result.error(INVALID_ARGUMENT);
        }
        user.setAccountNonLocked(LockOperationEnum.toIsAccountUnlocked(operation));
        UserProfile saved = userProfileStore.save(user);
        return Result.success(LockOperationEnum.fromIsAccountUnlocked(saved.isAccountNonLocked()));
    }

    private Either<ErrorEnum, UserProfile> validateAndGetUser(String username, SecurityRoleEnum role) {
        if (!ALLOWED_ROLES.contains(role)) {
            // TODO: add error msg to Result
//            return Result.error(ARGUMENT_NOT_VALID, "Allowed roles: " + ALLOWED_ROLES);
            return Result.error(INVALID_ARGUMENT);
        }
        Optional<UserProfile> userOpt = userProfileStore.findByUsernameIgnoreCase(username);
        if (userOpt.isEmpty()) {
            return Result.error(ENTITY_NOT_FOUND);
        }
        UserProfile user = userOpt.get();
        if (user.getRole().equals(role)) {
            return Result.error(STATE_ALREADY_EXISTS);
        }
        return Result.success(user);
    }

}
