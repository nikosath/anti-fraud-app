package antifraud.security.service;

import antifraud.error.ErrorEnum;
import antifraud.error.Result;
import antifraud.security.Dto.UserProfile;
import antifraud.security.Enum;

import java.util.List;

// TODO: convert to interface
public abstract class IAuthService {
    public abstract Result<ErrorEnum, UserProfile> createUser(String name, String username, String password);

    public abstract List<UserProfile> listUsers();

    public abstract Result<ErrorEnum, UserProfile> deleteUser(String username);

    public abstract Result<ErrorEnum, UserProfile> updateUserRole(String username, Enum.SecurityRole role);

    public abstract Result<ErrorEnum, Enum.LockOperation> updateUserLockStatus(String username, Enum.LockOperation operation);

}
