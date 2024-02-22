package antifraud.security.web;

import antifraud.security.service.AuthService;
import antifraud.security.storage.UserProfile;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/user")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest request) {
        return authService.createUser(request.name(), request.username(), request.password());
    }

    @GetMapping("/list")
    public List<UserResponse> listUsers() {
        return authService.listUsers();
    }

    @DeleteMapping("/user/{username}")
    public ResponseEntity<DeleteResponse> deleteUser(@PathVariable String username) {
        return authService.deleteUser(username);
    }

    public record UserRequest(String name, @NotBlank String username, @NotBlank String password) {
    }

    public record UserResponse(long id, String name, String username) {
        public static UserResponse fromUserProfile(UserProfile userProfile) {
            return new UserResponse(userProfile.getId(), userProfile.getName(), userProfile.getUsername());
        }
    }

    public record DeleteResponse(String username, String status) {
    }
}
