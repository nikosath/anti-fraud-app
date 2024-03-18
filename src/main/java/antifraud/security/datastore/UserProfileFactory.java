package antifraud.security.datastore;

import antifraud.security.Enum;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserProfileFactory {

    // TODO: refactor usages like 'new UserProfileFactory().admin'
    public static UserProfile newAdmin(String name, String username, String password) {
        return UserProfile.with(name, username, password, Enum.SecurityRole.ADMINISTRATOR, true);
    }

    public static UserProfile newMerchant(String name, String username, String password) {
        return UserProfile.with(name, username, password, Enum.SecurityRole.MERCHANT, false);
    }

}
