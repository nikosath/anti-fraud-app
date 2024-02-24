package antifraud.security.service;

import antifraud.error.Error;
import antifraud.security.storage.UserProfile;
import io.vavr.control.Either;

import java.util.List;

public abstract class IAuthService {
    public Either<Error, UserProfile> createUser(String name, String username, String password) {
        return createUser(UserProfile.with(name, username, password));
    }

    public abstract Either<Error, UserProfile> createUser(UserProfile userProfile);

    public abstract List<UserProfile> listUsers();

    public abstract Either<Error, UserProfile> deleteUser(String username);
}
