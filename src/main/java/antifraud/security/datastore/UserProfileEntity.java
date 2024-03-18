package antifraud.security.datastore;

import antifraud.security.Enum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Set;

// TODO: Add bean validation annotations to all Entities
@Entity
@Getter
@Setter
@ToString
//@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class UserProfileEntity implements UserDetails, Comparable<UserProfileEntity> {
    @Id
//    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // TODO: create a separate User entity. User 1to1 UserProfileEntity
    private String name;
    @Column(unique = true)
    private String username;
    private String password;
//    @Transient
    @Enumerated(EnumType.STRING)
    private Enum.SecurityRole role;
//    private Set<SecurityPermissionEnum> permissions;
    @Transient
    private transient Set<GrantedAuthority> authorities;
//    private boolean accountNonExpired = true;
    private boolean accountNonLocked;
//    private boolean credentialsNonExpired = true;
//    private boolean enabled = true;

    public static UserProfileEntity with(String name, String username, String password, Enum.SecurityRole role, boolean accountNonLocked) {
        return new UserProfileEntity(name, username, password, role, accountNonLocked);
    }

    private UserProfileEntity(String name, String username, String password, Enum.SecurityRole role, boolean accountNonLocked) {
        this.name = name;
        setUsername(username);
        this.password = password;
        setRole(role);
//        this.role = getRoles(role);
        // TODO: create a Set/SimpleGrantedAuthority/GrantedAuthority object pool
//        setAuthorities(role);
//        this.authority = new SimpleGrantedAuthority(role.name());
        this.accountNonLocked = accountNonLocked;
    }

    public void setRole(Enum.SecurityRole role) {
        this.role = role;
        setAuthoritiesFromRole(role);
    }

    private void setAuthoritiesFromRole(Enum.SecurityRole role) {
        this.authorities = Set.of(role.getAuthority());
    }

    public Set<GrantedAuthority> getAuthorities() {
        if (authorities == null) {
            setAuthoritiesFromRole(role);
        }
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // TODO: Consider applying toLowerCase elsewhere, not here
    public void setUsername(String username) {
        this.username = username.toLowerCase();
    }

    @Override
    public int compareTo(UserProfileEntity other) {
        return Long.compare(this.getId(), other.getId());
    }
}
