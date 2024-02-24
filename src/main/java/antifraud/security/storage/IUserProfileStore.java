package antifraud.security.storage;

import java.util.List;
import java.util.Optional;

/**
 * Storage for UserProfile, e.g. DB, in-memory
 */
public interface IUserProfileStore {

    UserProfile save(UserProfile userProfile);

    /**
     * @param encodedPassword should be encoded, i.e. encrypted
     */
    UserProfile save(String name, String username, String encodedPassword);

    Optional<UserProfile> findByUsernameIgnoreCase(String username);

    boolean existsByUsernameIgnoreCase(String username);

    List<UserProfile> findAllByOrderByIdAsc();

    List<UserProfile> deleteByUsernameIgnoreCase(String username);
}
