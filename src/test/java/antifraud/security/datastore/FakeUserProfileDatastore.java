package antifraud.security.datastore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class FakeUserProfileDatastore implements IUserProfileStore {

    long idSequence = 0L;
    Map<Long, UserProfileEntity> idToUserProfile = new HashMap<>();
    Map<String, UserProfileEntity> usernameToUserProfile = new HashMap<>();

    @Override
    public synchronized UserProfileEntity save(UserProfileEntity userProfile) {
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
    public synchronized Optional<UserProfileEntity> findByUsernameIgnoreCase(String username) {
        return Optional.of(usernameToUserProfile.get(username));
    }

    @Override
    public synchronized boolean existsByUsernameIgnoreCase(String username) {
        return usernameToUserProfile.containsKey(username);
    }

    @Override
    public synchronized List<UserProfileEntity> findAllByOrderByIdAsc() {
        return idToUserProfile.values().stream().sorted().toList();
    }

    // TODO: refactor to use Stream api
    @Override
    public synchronized Optional<UserProfileEntity> deleteByUsernameIgnoreCase(String username) {
        Optional<UserProfileEntity> userOpt = findByUsernameIgnoreCase(username);
        if (userOpt.isEmpty()) {
            return Optional.empty();
        }
        UserProfileEntity user = userOpt.get();
        idToUserProfile.remove(user.getId());
        usernameToUserProfile.remove(username);
        return userOpt;
    }

    @Override
    public synchronized long count() {
        return idToUserProfile.size();
    }
}
