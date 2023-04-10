package com.spotlight.platform.userprofile.api.core.profile;

import com.spotlight.platform.userprofile.api.core.exceptions.EntityNotFoundException;
import com.spotlight.platform.userprofile.api.core.profile.persistence.UserProfileDao;
import com.spotlight.platform.userprofile.api.model.profile.UserProfile;
import com.spotlight.platform.userprofile.api.model.profile.primitives.UserId;
import com.spotlight.platform.userprofile.api.model.profile.primitives.UserProfilePropertyName;
import com.spotlight.platform.userprofile.api.model.profile.primitives.UserProfilePropertyValue;
import com.spotlight.platform.userprofile.api.web.dto.UserCommandDto;
import com.spotlight.platform.userprofile.api.web.validation.InputCommandValidator;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class UserProfileService {
    private final UserProfileDao userProfileDao;

    @Inject
    public UserProfileService(UserProfileDao userProfileDao) {
        this.userProfileDao = userProfileDao;
    }

    public UserProfile get(UserId userId) {
        return userProfileDao.get(userId).orElseThrow(EntityNotFoundException::new);
    }

    public void update(UserCommandDto userProfileCommand) {
        if(!InputCommandValidator.validateInputCommand(userProfileCommand)) {
            throw new BadRequestException("Invalid input command property format for %s".formatted(userProfileCommand.getType()));
        }

        Optional<UserProfile> userProfile = userProfileDao.get(UserId.valueOf(userProfileCommand.getUserId()));

        if(userProfile.isPresent()) {
            UserProfile updatedUserProfile = runCommandOnUserProfile(userProfile.get(), userProfileCommand);
            userProfileDao.update(updatedUserProfile);
        } else {
            createNewUserProfile(userProfileCommand);
        }

    }

    private void createNewUserProfile(UserCommandDto userCommandDto) {
        UserId userId1 = UserId.valueOf(userCommandDto.getUserId());
        Map<UserProfilePropertyName, UserProfilePropertyValue> userProfilePropertyNameUserProfilePropertyMap = new ConcurrentHashMap<>();

        userCommandDto.getProperties().forEach((userProfileKey, useProfilePropertyValue) -> {
            userProfilePropertyNameUserProfilePropertyMap.put(UserProfilePropertyName.valueOf(userProfileKey), UserProfilePropertyValue.valueOf(useProfilePropertyValue));
        });
        UserProfile userNewProfile = new UserProfile(userId1, LocalDateTime.now().toInstant(ZoneOffset.UTC), userProfilePropertyNameUserProfilePropertyMap);

        userProfileDao.put(userNewProfile);
    }


    public UserProfile runCommandOnUserProfile(UserProfile userProfile, UserCommandDto userCommandDto) {

        switch (UserCommandDto.CommandType.valueOf(userCommandDto.getType())) {
            case replace -> {

                userCommandDto.getProperties().forEach((userProfileKey, useProfilePropertyValue) -> {

                    UserProfilePropertyName userProfilePropertyName = UserProfilePropertyName.valueOf(userProfileKey);
                    UserProfilePropertyValue userProfilePropertyValue = UserProfilePropertyValue.valueOf(useProfilePropertyValue);

                    userProfile.userProfileProperties().replace(userProfilePropertyName, userProfilePropertyValue);
                });

                return  userProfile;
            } case increment -> {

                userCommandDto.getProperties().forEach((userProfileKey, useProfileNewPropertyValue) -> {

                    UserProfilePropertyName userProfilePropertyName = UserProfilePropertyName.valueOf(userProfileKey);

                    UserProfilePropertyValue currentValue = userProfile.userProfileProperties().get(userProfilePropertyName);

                    //if new property is not yet available in user profile & if the value is greater than 0 then add it to user profile
                    //if it has minus value then ignoring it considering it make no sense to create a property with minus value
                    if(currentValue == null && ((Integer) useProfileNewPropertyValue >= 0 )) {
                         userProfile.userProfileProperties().put(userProfilePropertyName, UserProfilePropertyValue.valueOf(useProfileNewPropertyValue));
                    } else {

                        Integer newValue = (Integer)currentValue.getValue() + (Integer) useProfileNewPropertyValue;

                        UserProfilePropertyValue userProfilePropertyValue = UserProfilePropertyValue.valueOf(newValue);

                        userProfile.userProfileProperties().replace(userProfilePropertyName, userProfilePropertyValue);
                    }

                });

                return  userProfile;
            } case collect -> {
                userCommandDto.getProperties().forEach((userProfileKey, useProfilePropertyValue) -> {

                    UserProfilePropertyName userProfilePropertyName = UserProfilePropertyName.valueOf(userProfileKey);

                    UserProfilePropertyValue currentPropertyValue = userProfile.userProfileProperties().get(userProfilePropertyName);

                    if (currentPropertyValue == null ) {
                        userProfile.userProfileProperties().put(userProfilePropertyName, UserProfilePropertyValue.valueOf(useProfilePropertyValue));
                    } else {

                        @SuppressWarnings("unchecked")
                        List<String> currentPropertyValueList = (List<String>) currentPropertyValue.getValue();

                        @SuppressWarnings("unchecked")
                        List<String> newValues = (List<String>)useProfilePropertyValue;


                        currentPropertyValueList.addAll(newValues);
                        userProfile.userProfileProperties().put(userProfilePropertyName, UserProfilePropertyValue.valueOf(currentPropertyValueList));
                    }

                });

                return  userProfile;
            }

        }
        return null;
    }
}
