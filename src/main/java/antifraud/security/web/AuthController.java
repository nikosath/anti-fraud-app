package antifraud.security.web;

import antifraud.error.Error;
import antifraud.security.service.AuthService;
import antifraud.security.storage.UserProfile;
import io.vavr.control.Either;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static antifraud.error.Error.toHttpStatus;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/user")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest req) {
        Either<Error, UserProfile> userProfile = authService.createUser(UserRequest.toUserProfile(req));
        return userProfile.map(profile -> UserResponse.fromUserProfile(profile))
                .map(userResponse -> ResponseEntity.status(HttpStatus.CREATED).body(userResponse))
                .getOrElseGet(error -> ResponseEntity.status(toHttpStatus(error)).build());
    }

    @GetMapping("/list")
    public List<UserResponse> listUsers() {
        return authService.listUsers().stream()
                .map(userProfile -> UserResponse.fromUserProfile(userProfile))
                .toList();
    }

    @DeleteMapping("/user/{username}")
    public ResponseEntity<DeleteResponse> deleteUser(@PathVariable String username) {
        Either<Error, UserProfile> userProfile = authService.deleteUser(username);
        return userProfile.map(profile -> new DeleteResponse(profile.getUsername(), "Deleted successfully!"))
                .map(resp -> ResponseEntity.ok(resp))
                .getOrElseGet(error -> ResponseEntity.status(toHttpStatus(error)).build());

    }

    public record UserRequest(String name, @NotBlank String username, @NotBlank String password) {
        public static UserProfile toUserProfile(UserRequest req) {
            return new UserProfile(req.name(), req.username(), req.password());
        }
    }

    public record UserResponse(long id, String name, String username) {
        public static UserResponse fromUserProfile(UserProfile userProfile) {
            return new UserResponse(userProfile.getId(), userProfile.getName(), userProfile.getUsername());
        }
    }

    public record DeleteResponse(String username, String status) {
    }
}
