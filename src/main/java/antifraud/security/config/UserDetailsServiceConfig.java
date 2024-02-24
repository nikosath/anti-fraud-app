package antifraud.security.config;

import antifraud.security.storage.IUserProfileStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@Configuration
public class UserDetailsServiceConfig {

    @Bean
    public UserDetailsService jpaUserDetailsService(IUserProfileStore store) {
//        var user1 = UserProfile.with("Name1", "user1", passwordEncoder().encode("pass1"));
//        store.save(user1);

        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                log.info("username = " + username);
                return store.findByUsernameIgnoreCase(username)
                        .orElseThrow(() -> new UsernameNotFoundException("username not found"));
            }
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder() {
            @Override
            public String encode(CharSequence rawPassword) {
                String encoded = super.encode(rawPassword);
//                log.info("rawPassword = " + rawPassword + " encoded = " + encoded);
                return encoded;
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
//                log.info("rawPassword = " + rawPassword + " encoded = " + encodedPassword);
                return super.matches(rawPassword, encodedPassword);
            }
        };
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

