package antifraud.security.storage;

import java.util.List;
import java.util.Optional;

/**
 * Storage for UserProfile, e.g. DB, in-memory
 */
public interface IUserProfileStore {

    /**
     * @param userProfile with password that should be already encoded/encrypted
     */
    UserProfile save(UserProfile userProfile);

    Optional<UserProfile> findByUsernameIgnoreCase(String username);

    boolean existsByUsernameIgnoreCase(String username);

    // TODO: consider using pagination with PagingAndSortingRepository or ListPagingAndSortingRepository
    List<UserProfile> findAllByOrderByIdAsc();

    Optional<UserProfile> deleteByUsernameIgnoreCase(String username);

    long count();
}
