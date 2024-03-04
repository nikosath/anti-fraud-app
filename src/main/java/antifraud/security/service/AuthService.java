package antifraud.security.service;

import antifraud.error.ErrorEnum;
import antifraud.error.Result;
import antifraud.security.LockOperationEnum;
import antifraud.security.storage.IUserProfileStore;
import antifraud.security.storage.SecurityRoleEnum;
import antifraud.security.storage.UserProfile;
import antifraud.security.storage.UserProfileFactory;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static antifraud.error.ErrorEnum.*;
import static antifraud.security.storage.SecurityRoleEnum.MERCHANT;
import static antifraud.security.storage.SecurityRoleEnum.SUPPORT;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService extends IAuthService {

    public static final Set<SecurityRoleEnum> ALLOWED_ROLES = Set.of(SUPPORT, MERCHANT);
    private final IUserProfileStore userProfileStore;
    private final PasswordEncoder passwordEncoder;
    private boolean isFirstCreation = true;

    @Override
    public Either<ErrorEnum, UserProfile> createUser(String name, String username, String password) {
        if (userProfileStore.existsByUsernameIgnoreCase(username)) {
            return Result.error(ENTITY_ALREADY_EXISTS);
        }

        String encodedPassword = passwordEncoder.encode(password);
        UserProfile userProfile;
        if (isFirstCreation) {
            userProfile = UserProfileFactory.newAdmin(name, username, encodedPassword);
            isFirstCreation = false;
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
        var deleted = userProfileStore.deleteByUsernameIgnoreCase(username).get(0);
        return Result.success(deleted);
    }

    @Override
    public Either<ErrorEnum, UserProfile> updateUserRole(String username, SecurityRoleEnum role) {
        return validateAndGetUser(username, role)
                .map(user -> {
                    user.setRole(role);
                    return userProfileStore.save(user);
                });

//        if (result.isEmpty()) {
//            return result;
//        }
//        UserProfile user = result.get();
//        user.setRole(role);
//        UserProfile saved = userProfileStore.save(user);
//        return Result.success(saved);
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
