package antifraud.security.datastore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// TODO: rename to FakeUserStore
public class InMemoryUserStore implements IUserProfileStore {

    long idSequence = 0L;
    Map<Long, UserProfile> idToUserProfile = new HashMap<>();
    Map<String, UserProfile> usernameToUserProfile = new HashMap<>();

    @Override
    public synchronized UserProfile save(UserProfile userProfile) {
        if (userProfile.getId() == null) {
            idSequence += 1;
            userProfile.setId(idSequence);
        }
        userProfile.setUsername(userProfile.getUsername().toLowerCase());
        idToUserProfile.put(userProfile.getId(), userProfile);
        usernameToUserProfile.put(userProfile.getUsername(), userProfile);
        return userProfile;
    }

    @Override
    public Optional<UserProfile> findByUsernameIgnoreCase(String username) {
        return Optional.of(usernameToUserProfile.get(username));
    }

    @Override
    public boolean existsByUsernameIgnoreCase(String username) {
        return usernameToUserProfile.containsKey(username);
    }

    @Override
    public List<UserProfile> findAllByOrderByIdAsc() {
        return idToUserProfile.values().stream().sorted().toList();
    }

    // TODO: refactor to use Stream api
    @Override
    public synchronized Optional<UserProfile> deleteByUsernameIgnoreCase(String username) {
        Optional<UserProfile> userOpt = findByUsernameIgnoreCase(username);
        if (userOpt.isEmpty()) {
            return Optional.empty();
        }
        UserProfile user = userOpt.get();
        idToUserProfile.remove(user.getId());
        usernameToUserProfile.remove(username);
        return Optional.of(user);
    }

    @Override
    public long count() {
        return idToUserProfile.size();
    }
}
