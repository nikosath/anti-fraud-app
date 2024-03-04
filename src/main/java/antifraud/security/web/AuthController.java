package antifraud.security.web;

import antifraud.common.Uri;
import antifraud.error.ErrorEnum;
import antifraud.security.LockOperationEnum;
import antifraud.security.service.IAuthService;
import antifraud.security.storage.SecurityRoleEnum;
import antifraud.security.storage.UserProfile;
import io.vavr.control.Either;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static antifraud.error.ErrorEnum.toHttpStatus;

@RequiredArgsConstructor
@RestController
//@RequestMapping(Uri.API_AUTH)
public class AuthController {

    private final IAuthService authService;

    @PostMapping(Uri.API_AUTH_USER)
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest req) {
        Either<ErrorEnum, UserProfile> userProfile = authService.createUser(req.name(), req.username(), req.password());
        return userProfile.map(profile -> UserResponse.fromUserProfile(profile))
                .map(userResponse -> ResponseEntity.status(HttpStatus.CREATED).body(userResponse))
                .getOrElseGet(error -> toResponseEntity(error));
    }

    @GetMapping(Uri.API_AUTH_LIST)
    public List<UserResponse> listUsers() {
        List<UserProfile> userProfiles = authService.listUsers();
        return userProfiles.stream()
                .map(userProfile -> UserResponse.fromUserProfile(userProfile))
                .toList();
    }

    @DeleteMapping(Uri.API_AUTH_USER + Uri.USERNAME)
    public ResponseEntity<DeleteResponse> deleteUser(@PathVariable String username) {
        Either<ErrorEnum, UserProfile> userProfile = authService.deleteUser(username);
        return userProfile.map(profile -> new DeleteResponse(profile.getUsername(), "Deleted successfully!"))
                .map(resp -> ResponseEntity.ok(resp))
                .getOrElseGet(error -> toResponseEntity(error));
    }

    @PutMapping(Uri.API_AUTH_ROLE)
    public ResponseEntity<UserResponse> updateUserRole(String username, SecurityRoleEnum role) {
        Either<ErrorEnum, UserProfile> userProfile = authService.updateUserRole(username, role);
        return userProfile.map(profile -> UserResponse.fromUserProfile(profile))
                .map(userResponse -> ResponseEntity.status(HttpStatus.OK).body(userResponse))
                .getOrElseGet(error -> toResponseEntity(error));
    }

    @PutMapping(Uri.API_AUTH_ACCESS)
    public ResponseEntity<LockStatusResponse> updateUserLockStatus(String username, LockOperationEnum operation) {
        Either<ErrorEnum, LockOperationEnum> result = authService.updateUserLockStatus(username, operation);
        return result.map(lockOperation -> new LockStatusResponse(lockOperation))
                .map(lockStatusResponse -> ResponseEntity.status(HttpStatus.OK).body(lockStatusResponse))
                .getOrElseGet(error -> toResponseEntity(error));
    }

    public record UserRequest(String name, @NotBlank String username, @NotBlank String password) {
//        public static UserProfile toUserProfile(UserRequest req) {
//            return new UserProfileFactory().admin(req.name(), req.username(), req.password());
//        }
    }

    public record UserResponse(long id, String name, String username, SecurityRoleEnum role) {
        public static UserResponse fromUserProfile(UserProfile user) {
            return new UserResponse(user.getId(), user.getName(), user.getUsername(), user.getRole());
        }
    }

    public record DeleteResponse(String username, String status) {
    }

    public record LockStatusResponse(LockOperationEnum status) {
    }

    private static <R> ResponseEntity<R> toResponseEntity(ErrorEnum error) {
        return ResponseEntity.status(toHttpStatus(error)).build();
    }
}
