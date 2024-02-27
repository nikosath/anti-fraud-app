package antifraud.security.service;

import antifraud.error.ErrorEnum;
import antifraud.security.storage.UserProfile;
import io.vavr.control.Either;

import java.util.List;

public abstract class IAuthService {
    public Either<ErrorEnum, UserProfile> createUser(String name, String username, String password) {
        return createUser(UserProfile.with(name, username, password));
    }

    public abstract Either<ErrorEnum, UserProfile> createUser(UserProfile userProfile);

    public abstract List<UserProfile> listUsers();

    public abstract Either<ErrorEnum, UserProfile> deleteUser(String username);
}
