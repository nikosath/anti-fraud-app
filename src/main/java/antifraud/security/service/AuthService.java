package antifraud.security.service;

import antifraud.error.ErrorEnum;
import antifraud.error.Result;
import antifraud.security.Dto;
import antifraud.security.Enum;
import antifraud.security.datastore.IUserProfileStore;
import antifraud.security.datastore.UserProfileEntity;
import antifraud.security.datastore.UserProfileFactory;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static antifraud.error.ErrorEnum.*;
import static antifraud.security.Enum.SecurityRole.MERCHANT;
import static antifraud.security.Enum.SecurityRole.SUPPORT;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
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
    public Result<ErrorEnum, Dto.UserProfile> createUser(String name, String username, String password) {
        if (userProfileStore.existsByUsernameIgnoreCase(username)) {
            return Result.error(ENTITY_ALREADY_EXISTS);
        }

        String encodedPassword = passwordEncoder.encode(password);
        UserProfileEntity userProfile;
        if (isFirstUserCreation) {
            userProfile = UserProfileFactory.newAdmin(name, username, encodedPassword);
            isFirstUserCreation = false;
        } else {
            userProfile = UserProfileFactory.newMerchant(name, username, encodedPassword);
        }

        UserProfileEntity saved = userProfileStore.save(userProfile);

        return Result.success(toDto(saved));
    }

    private Dto.UserProfile toDto(UserProfileEntity saved) {
        return Dto.UserProfile.builder()
                .id(saved.getId())
                .name(saved.getName())
                .username(saved.getUsername())
                .role(saved.getRole())
                .build();
    }

    @Override
    public List<Dto.UserProfile> listUsers() {
        return toDtoList(userProfileStore.findAllByOrderByIdAsc());
    }

    private List<Dto.UserProfile> toDtoList(List<UserProfileEntity> allByOrderByIdAsc) {
        return allByOrderByIdAsc.stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public Result<ErrorEnum, Dto.UserProfile> deleteUser(String username) {
        if (!userProfileStore.existsByUsernameIgnoreCase(username)) {
            return Result.error(ENTITY_NOT_FOUND);
        }
        var deleted = userProfileStore.deleteByUsernameIgnoreCase(username).orElseThrow();
        return Result.success(toDto(deleted));
    }

    @Override
    public Result<ErrorEnum, Dto.UserProfile> updateUserRole(String username, Enum.SecurityRole role) {
        Result<ErrorEnum, UserProfileEntity> result = validateAndGetUser(username, role);
        if (result.isSuccess()) {
            UserProfileEntity user = result.getSuccess();
            user.setRole(role);
            return Result.success(toDto(userProfileStore.save(user)));
        }
        return Result.error(result.getError());
    }

    @Override
    public Result<ErrorEnum, Enum.LockOperation> updateUserLockStatus(String username, Enum.LockOperation operation) {
        Optional<UserProfileEntity> userOpt = userProfileStore.findByUsernameIgnoreCase(username);
        if (userOpt.isEmpty()) {
            return Result.error(ENTITY_NOT_FOUND);
        }
        UserProfileEntity user = userOpt.get();
        if (Enum.SecurityRole.ADMINISTRATOR.equals(user.getRole())) {
            return Result.error(INVALID_ARGUMENT);
        }
        user.setAccountNonLocked(Enum.LockOperation.toIsAccountUnlocked(operation));
        UserProfileEntity saved = userProfileStore.save(user);
        return Result.success(Enum.LockOperation.fromIsAccountUnlocked(saved.isAccountNonLocked()));
    }

    private Result<ErrorEnum, UserProfileEntity> validateAndGetUser(String username, Enum.SecurityRole role) {
        if (!ALLOWED_ROLES.contains(role)) {
            // TODO: add error msg to Result
//            return Result.error(ARGUMENT_NOT_VALID, "Allowed roles: " + ALLOWED_ROLES);
            return Result.error(INVALID_ARGUMENT);
        }
        Optional<UserProfileEntity> userOpt = userProfileStore.findByUsernameIgnoreCase(username);
        if (userOpt.isEmpty()) {
            return Result.error(ENTITY_NOT_FOUND);
        }
        UserProfileEntity user = userOpt.get();
        if (user.getRole().equals(role)) {
            return Result.error(STATE_ALREADY_EXISTS);
        }
        return Result.success(user);
    }

}
