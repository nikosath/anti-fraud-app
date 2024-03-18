package antifraud.security.web;

import antifraud.common.Uri;
import antifraud.common.WebUtils;
import antifraud.error.ErrorEnum;
import antifraud.error.Result;
import antifraud.security.Dto;
import antifraud.security.Enum;
import antifraud.security.service.IAuthService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@Slf4j
public class AuthController {

    private final IAuthService authService;

    @PostMapping(Uri.API_AUTH_USER)
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest req) {
        log.debug("createUser req = " + req);
        Result<ErrorEnum, Dto.UserProfile> result = authService.createUser(req.name(), req.username(), req.password());
        if (result.isSuccess()) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(UserResponse.fromUserProfile(result.getSuccess()));
        }
        return WebUtils.errorToResponseEntity(result.getError());
    }

    @GetMapping(Uri.API_AUTH_LIST)
    public List<UserResponse> listUsers() {
        log.debug("listUsers");
        List<Dto.UserProfile> userProfiles = authService.listUsers();
        return userProfiles.stream()
                .map(UserResponse::fromUserProfile)
                .toList();
    }

    @DeleteMapping(Uri.API_AUTH_USER + Uri.USERNAME)
    public ResponseEntity<DeleteResponse> deleteUser(@PathVariable String username) {
        log.debug("deleteUser username = " + username);
        Result<ErrorEnum, Dto.UserProfile> result = authService.deleteUser(username);
        if (result.isSuccess()) {
            return ResponseEntity.ok(
                    new DeleteResponse(result.getSuccess().username(), "Deleted successfully!"));
        }
        return WebUtils.errorToResponseEntity(result.getError());
    }

    @PutMapping(Uri.API_AUTH_ROLE)
    public ResponseEntity<UserResponse> updateUserRole(@Valid @RequestBody UserRoleRequest req) {
        log.debug("updateUserRole req = " + req);
        Result<ErrorEnum, Dto.UserProfile> result = authService.updateUserRole(req.username(), req.role());
        if (result.isSuccess()) {
            return ResponseEntity.ok(UserResponse.fromUserProfile(result.getSuccess()));
        }
        return WebUtils.errorToResponseEntity(result.getError());
    }

    @PutMapping(Uri.API_AUTH_ACCESS)
    public ResponseEntity<LockStatusResponse> updateUserLockStatus(@Valid @RequestBody LockStatusRequest req) {
        log.debug("updateUserLockStatus req = " + req);
        Result<ErrorEnum, Enum.LockOperation> result = authService.updateUserLockStatus(req.username(), req.operation());
        if (result.isSuccess()) {
            return ResponseEntity.ok(
                    new LockStatusResponse(req.username(), result.getSuccess()));
        }
        return WebUtils.errorToResponseEntity(result.getError());
    }

    public record UserRequest(String name, @NotBlank String username, @NotBlank String password) {
    }

    public record UserResponse(Long id, String name, String username, Enum.SecurityRole role) {
        static UserResponse fromUserProfile(Dto.UserProfile user) {
            return new UserResponse(user.id(), user.name(), user.username(), user.role());
        }
    }

    public record DeleteResponse(String username, String status) {
    }

    public record UserRoleRequest(@NotBlank String username, @NotNull Enum.SecurityRole role) {}

    public record LockStatusRequest(@NotBlank String username, @NotNull Enum.LockOperation operation) {}

    public record LockStatusResponse(String status) {
        LockStatusResponse(String username, Enum.LockOperation operation) {
            this(toStatus(username, operation));
        }

        private static String toStatus(String username, Enum.LockOperation operation) {
            String lockStatus = Enum.LockOperation.LOCK.equals(operation) ? "locked" : "unlocked";
            return String.format("User %s %s!", username, lockStatus);
        }
    }

}
