package antifraud.security.config;

import antifraud.security.datastore.IUserProfileStore;
import antifraud.security.datastore.UserProfileEntity;
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
        // TODO: remove createUsersForTesting once active development is finished
//        createUsersForTesting(store);

        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                log.debug("loadUserByUsername username = " + username);
                Optional<UserProfileEntity> userOpt = store.findByUsernameIgnoreCase(username);
                userOpt.ifPresent(user -> log.debug("user = " + user));
                return userOpt.orElseThrow(() -> new UsernameNotFoundException("username not found"));
            }
        };
    }

//    private void createUsersForTesting(IUserProfileStore store) {
//        var adminUser = UserProfileEntity.with("Admin1", "admin1", passwordEncoder().encode("admin-pass1"),
//                Enum.SecurityRole.ADMINISTRATOR, true);
//        var supportUser = UserProfileEntity.with("Support1", "support1", passwordEncoder().encode("support-pass1"),
//                Enum.SecurityRole.SUPPORT, true);
//        store.save(adminUser);
//        store.save(supportUser);
//    }

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

