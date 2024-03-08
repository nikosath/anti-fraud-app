package antifraud.security.config;

import antifraud.security.storage.IUserProfileStore;
import antifraud.security.storage.UserProfile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Slf4j
@Configuration
public class UserDetailsServiceConfig {

    @Bean
    public UserDetailsService userDetailsService(IUserProfileStore store) {
//        var adminUser = UserProfile.with("Admin1", "admin1", passwordEncoder().encode("admin-pass1"),
//                SecurityRoleEnum.ADMINISTRATOR, true);
//        var supportUser = UserProfile.with("Support1", "support1", passwordEncoder().encode("support-pass1"),
//                SecurityRoleEnum.SUPPORT, true);
//        store.save(adminUser);
//        store.save(supportUser);

        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                log.debug("loadUserByUsername username = " + username);
                Optional<UserProfile> userOpt = store.findByUsernameIgnoreCase(username);
                userOpt.ifPresent(user -> log.debug("user = " + user));
                return userOpt.orElseThrow(() -> new UsernameNotFoundException("username not found"));
            }
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new UserPasswordEncoder();
    }

//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
//    }

//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return NoOpPasswordEncoder.getInstance();
//    }

}

