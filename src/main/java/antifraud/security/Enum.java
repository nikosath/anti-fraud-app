package antifraud.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class Enum {
    public enum LockOperation {
        LOCK, UNLOCK;

        public static LockOperation fromIsAccountUnlocked(boolean isAccountUnlocked) {
            return isAccountUnlocked ? UNLOCK : LOCK;
        }

        public static boolean toIsAccountUnlocked(LockOperation operation) {
            return LockOperation.UNLOCK.equals(operation);
        }
    }

    //@RequiredArgsConstructor
    @Getter
    public enum SecurityRole {
        ADMINISTRATOR("ROLE_ADMINISTRATOR"), MERCHANT("ROLE_MERCHANT"), SUPPORT("ROLE_SUPPORT");

        private final GrantedAuthority authority;
        private final String prefixedRole;

        SecurityRole(String prefixedRole) {
            this.authority = new SimpleGrantedAuthority(prefixedRole);
            this.prefixedRole = prefixedRole;
        }
    }
}
