package antifraud.security.service;

import antifraud.security.storage.UserProfile;
import antifraud.security.storage.UserProfileStore;
import antifraud.security.web.AuthController.DeleteResponse;
import antifraud.security.web.AuthController.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

    private final UserProfileStore store;
    private final PasswordEncoder passwordEncoder;

    public ResponseEntity<UserResponse> createUser(String name, String username, String password) {
        return createUser(new UserProfile(name, username, password));
    }

    public ResponseEntity<UserResponse> createUser(UserProfile user) {
        log.info("user = " + user);
        if (store.existsByUsernameIgnoreCase(user.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        UserProfile savedProfile = store.save(user.getName(), user.getUsername(), encodedPassword);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(UserResponse.fromUserProfile(savedProfile));
    }

    public List<UserResponse> listUsers() {
        return store.findAllByOrderByIdAsc().stream()
                .map(UserResponse::fromUserProfile)
                .toList();
    }

    public ResponseEntity<DeleteResponse> deleteUser(@PathVariable String username) {
        if (!store.existsByUsernameIgnoreCase(username)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        var deleted = store.deleteByUsernameIgnoreCase(username).get(0);
        return ResponseEntity.ok(new DeleteResponse(deleted.getUsername(), "Deleted successfully!"));
    }

}
