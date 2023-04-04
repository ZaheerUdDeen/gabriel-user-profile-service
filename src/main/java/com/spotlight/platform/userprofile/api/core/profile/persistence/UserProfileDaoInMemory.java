package com.spotlight.platform.userprofile.api.core.profile.persistence;

import com.spotlight.platform.userprofile.api.model.profile.UserProfile;
import com.spotlight.platform.userprofile.api.model.profile.primitives.UserId;
import com.spotlight.platform.userprofile.api.model.profile.primitives.UserProfilePropertyName;
import com.spotlight.platform.userprofile.api.model.profile.primitives.UserProfilePropertyValue;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class UserProfileDaoInMemory implements UserProfileDao {
    private final Map<UserId, UserProfile> storage = new ConcurrentHashMap<>();

    @Override
    public Optional<UserProfile> get(UserId userId) {

        UserId userId1 = UserId.valueOf("1");
        Map<UserProfilePropertyName, UserProfilePropertyValue> userProfilePropertyNameUserProfilePropertyMap = new ConcurrentHashMap<>();
        userProfilePropertyNameUserProfilePropertyMap.put(UserProfilePropertyName.valueOf("id"), UserProfilePropertyValue.valueOf("1"));
        userProfilePropertyNameUserProfilePropertyMap.put(UserProfilePropertyName.valueOf("name"), UserProfilePropertyValue.valueOf("Zaheer"));

        UserProfile userProfile = new UserProfile(userId1, LocalDateTime.now().toInstant(ZoneOffset.UTC), userProfilePropertyNameUserProfilePropertyMap);
        storage.put(userId1, userProfile);

        return Optional.ofNullable(storage.get(userId));
    }

    @Override
    public void put(UserProfile userProfile) {
        storage.put(userProfile.userId(), userProfile);
    }
}
