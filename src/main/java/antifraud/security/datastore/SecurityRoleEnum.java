package antifraud.security.datastore;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

//@RequiredArgsConstructor
@Getter
public enum SecurityRoleEnum {
    ADMINISTRATOR("ROLE_ADMINISTRATOR"), MERCHANT("ROLE_MERCHANT"), SUPPORT("ROLE_SUPPORT");

    private final GrantedAuthority authority;
    private final String prefixedRole;

    SecurityRoleEnum(String prefixedRole) {
        this.authority = new SimpleGrantedAuthority(prefixedRole);
        this.prefixedRole = prefixedRole;
    }
}


