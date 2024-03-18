package antifraud.security;

import lombok.Builder;

public class Dto {
    @Builder
    public record UserProfile(Long id, String name, String username, String password, Enum.SecurityRole role,
                              boolean accountNonLocked) {

    }

}
