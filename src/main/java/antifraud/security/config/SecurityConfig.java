package antifraud.security.config;

import antifraud.security.storage.UserProfileStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Slf4j
@Configuration
public class SecurityConfig {

    @Autowired
    RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @Bean
    public UserDetailsService jpaUserDetailsService(UserProfileStore store) {
//        var user1 = new UserProfile("Name1", "user1", passwordEncoder().encode("pass1"));
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

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(CsrfConfigurer::disable) // For modifying requests via Postman
                .exceptionHandling(
                        handing -> handing.authenticationEntryPoint(restAuthenticationEntryPoint)) // Handles auth error
                .headers(headers -> headers.frameOptions().disable()) // for Postman, the H2 console
                .authorizeHttpRequests(requests -> requests // manage access
                        .requestMatchers("/h2-console/*").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/user").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/auth/list").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/auth/user/*").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/antifraud/transaction").authenticated()
                        .requestMatchers("/actuator/shutdown").permitAll() // needs to run test
                        .anyRequest().denyAll()
                ).sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // no session
//                .formLogin(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults())
                .build();
    }

}

