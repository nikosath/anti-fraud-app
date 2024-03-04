package antifraud.security.storage;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserProfileFactory {

    // TODO: refactor usages like 'new UserProfileFactory().admin'
    public static UserProfile newAdmin(String name, String username, String password) {
        return UserProfile.with(name, username, password, SecurityRoleEnum.ADMINISTRATOR, true);
    }

    public static UserProfile newMerchant(String name, String username, String password) {
        return UserProfile.with(name, username, password, SecurityRoleEnum.MERCHANT, false);
    }

//    private Set<SecurityRoleEnum> getSecurityRoles(SecurityRoleEnum role) {
//        return Set.of(securityRoleFactory.getSecurityRole(role));
//    }
}
