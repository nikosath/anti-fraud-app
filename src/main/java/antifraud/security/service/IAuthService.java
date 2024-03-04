package antifraud.security.service;

import antifraud.error.ErrorEnum;
import antifraud.security.LockOperationEnum;
import antifraud.security.storage.SecurityRoleEnum;
import antifraud.security.storage.UserProfile;
import io.vavr.control.Either;

import java.util.List;

// TODO: convert to interface
public abstract class IAuthService {
    public abstract Either<ErrorEnum, UserProfile> createUser(String name, String username, String password);

    public abstract List<UserProfile> listUsers();

    public abstract Either<ErrorEnum, UserProfile> deleteUser(String username);

    public abstract Either<ErrorEnum, UserProfile> updateUserRole(String username, SecurityRoleEnum role);

    public abstract Either<ErrorEnum, LockOperationEnum> updateUserLockStatus(String username, LockOperationEnum operation);

}
