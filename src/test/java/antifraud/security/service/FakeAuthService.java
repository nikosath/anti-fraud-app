package antifraud.security.service;

import antifraud.TestHelper;
import antifraud.error.ErrorEnum;
import antifraud.error.Result;
import antifraud.security.Enum;
import antifraud.security.datastore.UserProfile;
import antifraud.security.datastore.UserProfileFactory;
import lombok.Setter;

import java.util.List;


@Setter
public class FakeAuthService extends IAuthService {

    private TestHelper.TestBehaviorEnum createUserBehavior;
    private TestHelper.TestBehaviorEnum listUsersBehavior;
    private TestHelper.TestBehaviorEnum deleteUserBehavior;
    private TestHelper.TestBehaviorEnum updateUserRoleBehavior;
    private TestHelper.TestBehaviorEnum updateUserLockStatusBehavior;

    @Override
    public Result<ErrorEnum, UserProfile> createUser(String name, String username, String password) {
        if (createUserBehavior == TestHelper.TestBehaviorEnum.SUCCEEDS) {
            UserProfile userProfile = UserProfile.with(name, username, password, Enum.SecurityRole.MERCHANT, true);
            userProfile.setId(1L);
            return Result.success(userProfile);
        }
        return Result.error(ErrorEnum.ENTITY_ALREADY_EXISTS);
    }

    @Override
    public List<UserProfile> listUsers() {
        if (listUsersBehavior == TestHelper.TestBehaviorEnum.RETURNS_2_ENTITIES) {
            var user1 = UserProfileFactory.newAdmin("Name1", "user1", "pass1");
            user1.setId(1L);
            var user2 = UserProfileFactory.newAdmin("Name2", "user2", "pass2");
            user2.setId(2L);
            return List.of(user1, user2);
        }
        return List.of();
    }

    @Override
    public Result<ErrorEnum, UserProfile> deleteUser(String username) {
        if (deleteUserBehavior == TestHelper.TestBehaviorEnum.SUCCEEDS) {
            var user1 = UserProfileFactory.newAdmin("Name1", "user1", "pass1");
            return Result.success(user1);
        }
        return Result.error(ErrorEnum.ENTITY_NOT_FOUND);
    }

    @Override
    public Result<ErrorEnum, UserProfile> updateUserRole(String username, Enum.SecurityRole role) {
        if (updateUserRoleBehavior == TestHelper.TestBehaviorEnum.SUCCEEDS) {
            var user1 = UserProfileFactory.newAdmin("Name1", "user1", "pass1");
            user1.setId(1L);
            return Result.success(user1);
        }
        return Result.error(ErrorEnum.ENTITY_NOT_FOUND);
    }

    @Override
    public Result<ErrorEnum, Enum.LockOperation> updateUserLockStatus(String username, Enum.LockOperation operation) {
        if (updateUserLockStatusBehavior == TestHelper.TestBehaviorEnum.SUCCEEDS) {
            return Result.success(operation);
        }
        return Result.error(ErrorEnum.ENTITY_NOT_FOUND);
    }

}
