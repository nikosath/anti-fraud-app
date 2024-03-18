package antifraud.security.datastore;

import java.util.List;
import java.util.Optional;

// TODO: rename to IUserProfileDatastore
/**
 * Storage for UserProfileEntity, e.g. DB, in-memory
 */
public interface IUserProfileStore {

    /**
     * @param userProfile with password that should be already encoded/encrypted
     */
    UserProfileEntity save(UserProfileEntity userProfile);

    Optional<UserProfileEntity> findByUsernameIgnoreCase(String username);

    boolean existsByUsernameIgnoreCase(String username);

    // TODO: consider using pagination with PagingAndSortingRepository or ListPagingAndSortingRepository
    List<UserProfileEntity> findAllByOrderByIdAsc();

    Optional<UserProfileEntity> deleteByUsernameIgnoreCase(String username);

    long count();
}
