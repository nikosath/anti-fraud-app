package antifraud.security.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Slf4j
public class UserPasswordEncoder extends BCryptPasswordEncoder {
    @Override
    public String encode(CharSequence rawPassword) {
        String encodedPassword = super.encode(rawPassword);
        log.debug("rawPassword = " + rawPassword + " encodedPassword = " + encodedPassword);
        return encodedPassword;
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        log.debug("rawPassword = " + rawPassword + " encoded = " + encodedPassword);
        return super.matches(rawPassword, encodedPassword);
    }
}