package antifraud.security.service;

import antifraud.error.Error;
import antifraud.error.Result;
import antifraud.security.storage.UserProfile;
import io.vavr.control.Either;
import lombok.Setter;

import java.util.List;


@Setter
public class FakeAuthService extends IAuthService {

    private Behavior createUserBehavior;
    private Behavior listUsersBehavior;
    private Behavior deleteUserBehavior;

    @Override
    public Either<Error, UserProfile> createUser(UserProfile userProfile) {
        if (createUserBehavior == Behavior.SUCCEEDS) {
            userProfile.setId(1L);
            return Result.success(userProfile);
        }
        return Result.error(Error.ENTITY_ALREADY_EXISTS);
    }

    @Override
    public List<UserProfile> listUsers() {
        if (listUsersBehavior == Behavior.RETURNS_2_ENTITIES) {
            var user1 = UserProfile.with("Name1", "user1", "pass1");
            user1.setId(1L);
            var user2 = UserProfile.with("Name2", "user2", "pass2");
            user2.setId(2L);
            return List.of(user1, user2);
        }
        return List.of();
    }

    @Override
    public Either<Error, UserProfile> deleteUser(String username) {
        if (deleteUserBehavior == Behavior.SUCCEEDS) {
            var user1 = UserProfile.with("Name1", "user1", "pass1");
            return Result.success(user1);
        }
        return Result.error(Error.ENTITY_NOT_FOUND);
    }

    public enum Behavior {
        SUCCEEDS, RETURNS_2_ENTITIES
    }
}
