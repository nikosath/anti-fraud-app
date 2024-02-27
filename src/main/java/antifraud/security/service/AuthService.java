package antifraud.security.service;

import antifraud.error.ErrorEnum;
import antifraud.error.Result;
import antifraud.security.storage.IUserProfileStore;
import antifraud.security.storage.UserProfile;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService extends IAuthService {

    private final IUserProfileStore store;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Either<ErrorEnum, UserProfile> createUser(UserProfile userProfile) {
//        log.info("user = " + user);
        if (store.existsByUsernameIgnoreCase(userProfile.getUsername())) {
            return Result.error(ErrorEnum.ENTITY_ALREADY_EXISTS);
        }
        String encodedPassword = passwordEncoder.encode(userProfile.getPassword());
        UserProfile saved = store.save(userProfile.getName(), userProfile.getUsername(), encodedPassword);
        return Result.success(saved);
    }

    @Override
    public List<UserProfile> listUsers() {
        return store.findAllByOrderByIdAsc();
    }

    @Override
    public Either<ErrorEnum, UserProfile> deleteUser(String username) {
        if (!store.existsByUsernameIgnoreCase(username)) {
            return Result.error(ErrorEnum.ENTITY_NOT_FOUND);
        }
        var deleted = store.deleteByUsernameIgnoreCase(username).get(0);
        return Result.success(deleted);
    }

}
