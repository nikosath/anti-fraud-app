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
//        var user1 = UserProfile.with("Name1", "user1", passwordEncoder().encode("pass1"));
//        store.save(user1);

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

