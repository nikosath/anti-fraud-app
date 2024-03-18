package antifraud.security.datastore;

import antifraud.security.Enum;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserProfileFactory {

    // TODO: refactor usages like 'new UserProfileFactory().admin'
    public static UserProfileEntity newAdmin(String name, String username, String password) {
        return UserProfileEntity.with(name, username, password, Enum.SecurityRole.ADMINISTRATOR, true);
    }

    public static UserProfileEntity newMerchant(String name, String username, String password) {
        return UserProfileEntity.with(name, username, password, Enum.SecurityRole.MERCHANT, false);
    }

}
