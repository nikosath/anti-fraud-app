package antifraud.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Uri {
    // * matches zero or more characters. For example, requestMatchers("/*") matches /, /abc, and /defg but not /ab/cd
    public static final String ASTERISK = "/*";
    // ** matches zero or more directories in a path. For example, requestMatchers("/**") matches /, /ab, /cd, and /a/b/c
    public static final String DOUBLE_ASTERISK = "/**";
    public static final String API_AUTH_LIST = "/api/auth/list";
    public static final String API_AUTH_USER = "/api/auth/user";
    public static final String API_AUTH_ROLE = "/api/auth/role";
    public static final String API_AUTH_ACCESS = "/api/auth/access";
    public static final String USERNAME = "/{username}";

    public static final String API_ANTIFRAUD = "/api/antifraud";
    public static final String API_ANTIFRAUD_TRANSACTION = "/api/antifraud/transaction";
    public static final String API_ANTIFRAUD_HISTORY = "/api/antifraud/history";
    public static final String API_ANTIFRAUD_SUSPICIOUS_IP = "/api/antifraud/suspicious-ip";
    public static final String IP = "/{ip}";
    public static final String API_ANTIFRAUD_STOLENCARD = "/api/antifraud/stolencard";
    public static final String CARD_NUMBER = "/{cardNumber}" ;

    public static final String H2_CONSOLE = "/h2-console/*";
    public static final String ACTUATOR_SHUTDOWN = "/actuator/shutdown";
    public static final String ERROR = "/error";

}
