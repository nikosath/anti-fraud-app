package antifraud.security.config;

import antifraud.common.Uri;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;

import java.io.IOException;

import static antifraud.common.Uri.ASTERISK;
import static antifraud.security.Enum.SecurityRole.*;

@Slf4j
@Configuration
public class SecurityFilterChainConfig {

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new AuthenticationEntryPoint() {
            @Override
            public void commence(HttpServletRequest request,
                                 HttpServletResponse response,
                                 AuthenticationException authException) throws IOException {

//                response.sendError(HttpServletResponse.SC_MOVED_PERMANENTLY, authException.getMessage());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
            }
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(CsrfConfigurer::disable) // For modifying requests via Postman
                .exceptionHandling(
                        handing -> handing.authenticationEntryPoint(authenticationEntryPoint())) // Handles auth error
                .headers(headers -> headers.frameOptions().disable()) // for Postman, the H2 console
                .authorizeHttpRequests(registry -> registry // manage access
                        // auth
                        .requestMatchers(HttpMethod.POST, Uri.API_AUTH_USER).permitAll()
                        .requestMatchers(HttpMethod.DELETE, Uri.API_AUTH_USER + ASTERISK).hasRole(ADMINISTRATOR.name())
                        .requestMatchers(HttpMethod.GET, Uri.API_AUTH_LIST).hasAnyRole(ADMINISTRATOR.name(), SUPPORT.name())
                        .requestMatchers(HttpMethod.PUT, Uri.API_AUTH_ACCESS).hasRole(ADMINISTRATOR.name())
                        .requestMatchers(HttpMethod.PUT, Uri.API_AUTH_ROLE).hasRole(ADMINISTRATOR.name())
                        // API_ANTIFRAUD_TRANSACTION
                        .requestMatchers(HttpMethod.POST, Uri.API_ANTIFRAUD_TRANSACTION).hasRole(MERCHANT.name())
                        .requestMatchers(HttpMethod.PUT, Uri.API_ANTIFRAUD_TRANSACTION).hasRole(SUPPORT.name())
                        // API_ANTIFRAUD_HISTORY
                        .requestMatchers(HttpMethod.GET, Uri.API_ANTIFRAUD_HISTORY + ASTERISK).hasRole(SUPPORT.name())
                        // API_ANTIFRAUD_SUSPICIOUS_IP
                        .requestMatchers(HttpMethod.POST, Uri.API_ANTIFRAUD_SUSPICIOUS_IP).hasRole(SUPPORT.name())
                        .requestMatchers(HttpMethod.DELETE, Uri.API_ANTIFRAUD_SUSPICIOUS_IP + ASTERISK).hasRole(SUPPORT.name())
                        .requestMatchers(HttpMethod.GET, Uri.API_ANTIFRAUD_SUSPICIOUS_IP).hasRole(SUPPORT.name())
                        // API_ANTIFRAUD_STOLENCARD
                        .requestMatchers(HttpMethod.POST, Uri.API_ANTIFRAUD_STOLENCARD).hasRole(SUPPORT.name())
                        .requestMatchers(HttpMethod.DELETE, Uri.API_ANTIFRAUD_STOLENCARD + ASTERISK).hasRole(SUPPORT.name())
                        .requestMatchers(HttpMethod.GET, Uri.API_ANTIFRAUD_STOLENCARD).hasRole(SUPPORT.name())
                        // other
                        .requestMatchers(Uri.ERROR).permitAll()
                        .requestMatchers(Uri.H2_CONSOLE).permitAll()
                        .requestMatchers(Uri.ACTUATOR_SHUTDOWN).permitAll() // needs to run test
        //                                .anyRequest().permitAll()
                        .anyRequest().denyAll()
                ).sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // no session
//                .formLogin(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults())
                .build();
    }

}

