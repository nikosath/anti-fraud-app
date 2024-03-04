package antifraud.security.service;

import antifraud.error.ErrorEnum;
import antifraud.error.Result;
import antifraud.security.LockOperationEnum;
import antifraud.security.storage.SecurityRoleEnum;
import antifraud.security.storage.UserProfile;
import antifraud.security.storage.UserProfileFactory;
import io.vavr.control.Either;
import lombok.Setter;

import java.util.List;


@Setter
public class FakeAuthService extends IAuthService {

    private BehaviorEnum createUserBehavior;
    private BehaviorEnum listUsersBehavior;
    private BehaviorEnum deleteUserBehavior;
    private BehaviorEnum updateUserRoleBehavior;
    private BehaviorEnum updateUserLockStatusBehavior;

    @Override
    public Either<ErrorEnum, UserProfile> createUser(String name, String username, String password) {
        if (createUserBehavior == BehaviorEnum.SUCCEEDS) {
            UserProfile userProfile = UserProfile.with(name, username, password, SecurityRoleEnum.MERCHANT, true);
            userProfile.setId(1L);
            return Result.success(userProfile);
        }
        return Result.error(ErrorEnum.ENTITY_ALREADY_EXISTS);
    }

    @Override
    public List<UserProfile> listUsers() {
        if (listUsersBehavior == BehaviorEnum.RETURNS_2_ENTITIES) {
            var user1 = UserProfileFactory.newAdmin("Name1", "user1", "pass1");
            user1.setId(1L);
            var user2 = UserProfileFactory.newAdmin("Name2", "user2", "pass2");
            user2.setId(2L);
            return List.of(user1, user2);
        }
        return List.of();
    }

    @Override
    public Either<ErrorEnum, UserProfile> deleteUser(String username) {
        if (deleteUserBehavior == BehaviorEnum.SUCCEEDS) {
            var user1 = UserProfileFactory.newAdmin("Name1", "user1", "pass1");
            return Result.success(user1);
        }
        return Result.error(ErrorEnum.ENTITY_NOT_FOUND);
    }

    @Override
    public Either<ErrorEnum, UserProfile> updateUserRole(String username, SecurityRoleEnum role) {
        if (updateUserRoleBehavior == BehaviorEnum.SUCCEEDS) {
            var user1 = UserProfileFactory.newAdmin("Name1", "user1", "pass1");
            return Result.success(user1);
        }
        return Result.error(ErrorEnum.ENTITY_NOT_FOUND);
    }

    @Override
    public Either<ErrorEnum, LockOperationEnum> updateUserLockStatus(String username, LockOperationEnum operation) {
        if (updateUserLockStatusBehavior == BehaviorEnum.SUCCEEDS) {
            return Result.success(operation);
        }
        return Result.error(ErrorEnum.ENTITY_NOT_FOUND);
    }

    public enum BehaviorEnum {
        SUCCEEDS, RETURNS_2_ENTITIES
    }
}
