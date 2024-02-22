package antifraud.security.storage;

import java.util.List;
import java.util.Optional;

/**
 * Storage for UserProfile, e.g. DB, in-memory
 */
public interface UserProfileStore {

    UserProfile save(UserProfile userProfile);

    UserProfile save(String name, String username, String rawPassword);

    Optional<UserProfile> findByUsernameIgnoreCase(String username);

    boolean existsByUsernameIgnoreCase(String username);

    List<UserProfile> findAllByOrderByIdAsc();

    List<UserProfile> deleteByUsernameIgnoreCase(String username);
}
