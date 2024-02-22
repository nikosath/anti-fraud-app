package antifraud.security.service;

import antifraud.error.Error;
import antifraud.error.Result;
import antifraud.security.storage.UserProfile;
import antifraud.security.storage.UserProfileStore;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

    private final UserProfileStore store;
    private final PasswordEncoder passwordEncoder;

    public Either<Error, UserProfile> createUser(String name, String username, String password) {
        return createUser(new UserProfile(name, username, password));
    }

    public Either<Error, UserProfile> createUser(UserProfile user) {
//        log.info("user = " + user);
        if (store.existsByUsernameIgnoreCase(user.getUsername())) {
            return Result.error(Error.ENTITY_EXISTS);
        }
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        UserProfile saved = store.save(user.getName(), user.getUsername(), encodedPassword);
        return Result.success(saved);
    }

    public List<UserProfile> listUsers() {
        return store.findAllByOrderByIdAsc();
    }

    public Either<Error, UserProfile> deleteUser(String username) {
        if (!store.existsByUsernameIgnoreCase(username)) {
            return Result.error(Error.ENTITY_NOT_FOUND);
        }
        var deleted = store.deleteByUsernameIgnoreCase(username).get(0);
        return Result.success(deleted);
    }

}
