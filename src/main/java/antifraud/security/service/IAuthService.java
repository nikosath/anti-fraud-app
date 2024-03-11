package antifraud.security.service;

import antifraud.error.ErrorEnum;
import antifraud.error.Result;
import antifraud.security.LockOperationEnum;
import antifraud.security.datastore.SecurityRoleEnum;
import antifraud.security.datastore.UserProfile;

import java.util.List;

// TODO: convert to interface
public abstract class IAuthService {
    public abstract Result<ErrorEnum, UserProfile> createUser(String name, String username, String password);

    public abstract List<UserProfile> listUsers();

    public abstract Result<ErrorEnum, UserProfile> deleteUser(String username);

    public abstract Result<ErrorEnum, UserProfile> updateUserRole(String username, SecurityRoleEnum role);

    public abstract Result<ErrorEnum, LockOperationEnum> updateUserLockStatus(String username, LockOperationEnum operation);

}
