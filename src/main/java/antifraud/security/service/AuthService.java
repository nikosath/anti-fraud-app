package antifraud.security.service;

import antifraud.error.ErrorEnum;
import antifraud.error.Result;
import antifraud.security.Enum;
import antifraud.security.datastore.IUserProfileStore;
import antifraud.security.datastore.UserProfile;
import antifraud.security.datastore.UserProfileFactory;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static antifraud.error.ErrorEnum.*;
import static antifraud.security.Enum.SecurityRole.MERCHANT;
import static antifraud.security.Enum.SecurityRole.SUPPORT;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService extends IAuthService {

    public static final Set<Enum.SecurityRole> ALLOWED_ROLES = Set.of(SUPPORT, MERCHANT);
    private final IUserProfileStore userProfileStore;
    private final PasswordEncoder passwordEncoder;
    private boolean isFirstUserCreation;

    @PostConstruct
    void init() {
        isFirstUserCreation = userProfileStore.count() == 0;
    }

    @Override
    public Result<ErrorEnum, UserProfile> createUser(String name, String username, String password) {
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
    public Result<ErrorEnum, UserProfile> deleteUser(String username) {
        if (!userProfileStore.existsByUsernameIgnoreCase(username)) {
            return Result.error(ENTITY_NOT_FOUND);
        }
        var deleted = userProfileStore.deleteByUsernameIgnoreCase(username).orElseThrow();
        return Result.success(deleted);
    }

    @Override
    public Result<ErrorEnum, UserProfile> updateUserRole(String username, Enum.SecurityRole role) {
        Result<ErrorEnum, UserProfile> result = validateAndGetUser(username, role);
        if (result.isSuccess()) {
            UserProfile user = result.getSuccess();
            user.setRole(role);
            return Result.success(userProfileStore.save(user));
        }
        return result;
    }

    @Override
    public Result<ErrorEnum, Enum.LockOperation> updateUserLockStatus(String username, Enum.LockOperation operation) {
        Optional<UserProfile> userOpt = userProfileStore.findByUsernameIgnoreCase(username);
        if (userOpt.isEmpty()) {
            return Result.error(ENTITY_NOT_FOUND);
        }
        UserProfile user = userOpt.get();
        if (Enum.SecurityRole.ADMINISTRATOR.equals(user.getRole())) {
            return Result.error(INVALID_ARGUMENT);
        }
        user.setAccountNonLocked(Enum.LockOperation.toIsAccountUnlocked(operation));
        UserProfile saved = userProfileStore.save(user);
        return Result.success(Enum.LockOperation.fromIsAccountUnlocked(saved.isAccountNonLocked()));
    }

    private Result<ErrorEnum, UserProfile> validateAndGetUser(String username, Enum.SecurityRole role) {
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
