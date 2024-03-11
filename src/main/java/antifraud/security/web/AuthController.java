package antifraud.security.web;

import antifraud.common.Uri;
import antifraud.common.WebUtils;
import antifraud.error.ErrorEnum;
import antifraud.error.Result;
import antifraud.security.LockOperationEnum;
import antifraud.security.datastore.SecurityRoleEnum;
import antifraud.security.datastore.UserProfile;
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
        Result<ErrorEnum, UserProfile> result = authService.createUser(req.name(), req.username(), req.password());
        if (result.isSuccess()) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(UserResponse.fromUserProfile(result.getSuccess()));
        }
        return WebUtils.errorToResponseEntity(result.getError());
    }

    @GetMapping(Uri.API_AUTH_LIST)
    public List<UserResponse> listUsers() {
        log.debug("listUsers");
        List<UserProfile> userProfiles = authService.listUsers();
        return userProfiles.stream()
                .map(UserResponse::fromUserProfile)
                .toList();
    }

    @DeleteMapping(Uri.API_AUTH_USER + Uri.USERNAME)
    public ResponseEntity<DeleteResponse> deleteUser(@PathVariable String username) {
        log.debug("deleteUser username = " + username);
        Result<ErrorEnum, UserProfile> result = authService.deleteUser(username);
        if (result.isSuccess()) {
            return ResponseEntity.ok(
                    new DeleteResponse(result.getSuccess().getUsername(), "Deleted successfully!"));
        }
        return WebUtils.errorToResponseEntity(result.getError());
    }

    @PutMapping(Uri.API_AUTH_ROLE)
    public ResponseEntity<UserResponse> updateUserRole(@Valid @RequestBody UserRoleRequest req) {
        log.debug("updateUserRole req = " + req);
        Result<ErrorEnum, UserProfile> result = authService.updateUserRole(req.username(), req.role());
        if (result.isSuccess()) {
            return ResponseEntity.ok(UserResponse.fromUserProfile(result.getSuccess()));
        }
        return WebUtils.errorToResponseEntity(result.getError());
    }

    @PutMapping(Uri.API_AUTH_ACCESS)
    public ResponseEntity<LockStatusResponse> updateUserLockStatus(@Valid @RequestBody LockStatusRequest req) {
        log.debug("updateUserLockStatus req = " + req);
        Result<ErrorEnum, LockOperationEnum> result = authService.updateUserLockStatus(req.username(), req.operation());
        if (result.isSuccess()) {
            return ResponseEntity.ok(
                    new LockStatusResponse(req.username(), result.getSuccess()));
        }
        return WebUtils.errorToResponseEntity(result.getError());
    }

    public record UserRequest(String name, @NotBlank String username, @NotBlank String password) {
    }

    public record UserResponse(Long id, String name, String username, SecurityRoleEnum role) {
        static UserResponse fromUserProfile(UserProfile user) {
            return new UserResponse(user.getId(), user.getName(), user.getUsername(), user.getRole());
        }
    }

    public record DeleteResponse(String username, String status) {
    }

    public record UserRoleRequest(@NotBlank String username, @NotNull SecurityRoleEnum role) {}

    public record LockStatusRequest(@NotBlank String username, @NotNull LockOperationEnum operation) {}

    public record LockStatusResponse(String status) {
        LockStatusResponse(String username, LockOperationEnum operation) {
            this(toStatus(username, operation));
        }

        private static String toStatus(String username, LockOperationEnum operation) {
            String lockStatus = LockOperationEnum.LOCK.equals(operation) ? "locked" : "unlocked";
            return String.format("User %s %s!", username, lockStatus);
        }
    }

}
