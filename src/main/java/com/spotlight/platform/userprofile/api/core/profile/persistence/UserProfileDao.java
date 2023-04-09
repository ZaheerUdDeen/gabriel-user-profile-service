package com.spotlight.platform.userprofile.api.core.profile.persistence;

import com.spotlight.platform.userprofile.api.model.profile.UserProfile;
import com.spotlight.platform.userprofile.api.model.profile.primitives.UserId;
import com.spotlight.platform.userprofile.api.web.dto.UserCommandDto;

import java.util.Optional;

public interface UserProfileDao {
    Optional<UserProfile> get(UserId userId);

    void put(UserProfile userProfile);

    void update(UserProfile userProfile);
}
