package com.spotlight.platform.userprofile.api.web.resources.userprofile;

import com.spotlight.platform.userprofile.api.core.profile.UserProfileService;
import com.spotlight.platform.userprofile.api.core.profile.persistence.UserProfileDao;
import com.spotlight.platform.userprofile.api.model.profile.UserProfile;
import com.spotlight.platform.userprofile.api.model.profile.primitives.UserId;
import com.spotlight.platform.userprofile.api.model.profile.primitives.UserProfileFixtures;
import com.spotlight.platform.userprofile.api.model.profile.primitives.UserProfilePropertyName;
import com.spotlight.platform.userprofile.api.model.profile.primitives.UserProfilePropertyValue;
import com.spotlight.platform.userprofile.api.web.UserProfileApiApplication;
import com.spotlight.platform.userprofile.api.web.dto.UserCommandDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import ru.vyarus.dropwizard.guice.test.ClientSupport;
import ru.vyarus.dropwizard.guice.test.jupiter.TestDropwizardApp;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestDropwizardApp(value = UserProfileApiApplication.class, randomPorts = true)

class UserProfileServiceTest {

//    @Mock
//    UserProfileDao userProfileDao;
//
//    @InjectMocks
//    UserProfileService userProfileService;

    @Test
    void runCommandOnUserProfile_replaceCommand(UserProfileService userProfileService, UserProfileDao userProfileDao) {
        String USER_ID = "de4310e5-b139-441a-99db-77c9c4a5fada";
        Map<UserProfilePropertyName, UserProfilePropertyValue> userProperties = new HashMap<>();
        userProperties.put(UserProfilePropertyName.valueOf("currentGold"), UserProfilePropertyValue.valueOf(200));
        UserProfile newUserProfile  = new UserProfile(UserId.valueOf(USER_ID), LocalDateTime.MAX.toInstant(ZoneOffset.UTC), userProperties);

        userProfileDao.put(newUserProfile);


        UserCommandDto userCommandDto = new UserCommandDto();
        // Set valid properties for the command object
        userCommandDto.setType("replace");
        userCommandDto.setUserId(USER_ID);
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("currentGold", 500);
        userCommandDto.setProperties(properties);

        UserProfile userProfile = userProfileService.get(UserId.valueOf(USER_ID));

        UserProfile updatedUserProfile = userProfileService.runCommandOnUserProfile(userProfile, userCommandDto);

        assertEquals(UserProfilePropertyValue.valueOf(500), updatedUserProfile.userProfileProperties().get(UserProfilePropertyName.valueOf("currentGold")));
    }

    @Test
    void runCommandOnUserProfile_incrementCommand(UserProfileService userProfileService, UserProfileDao userProfileDao) {
        String USER_ID = "de4310e5-b139-441a-99db-77c9c4a5fada";
        Map<UserProfilePropertyName, UserProfilePropertyValue> userProperties = new HashMap<>();
        userProperties.put(UserProfilePropertyName.valueOf("battleFought"), UserProfilePropertyValue.valueOf(10));
        userProperties.put(UserProfilePropertyName.valueOf("questsNotCompleted"), UserProfilePropertyValue.valueOf(1));
        UserProfile newUserProfile  = new UserProfile(UserId.valueOf(USER_ID), LocalDateTime.MAX.toInstant(ZoneOffset.UTC), userProperties);

        userProfileDao.put(newUserProfile);


        UserCommandDto userCommandDto = new UserCommandDto();
        // Set valid properties for the command object
        userCommandDto.setType("increment");
        userCommandDto.setUserId(USER_ID);
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("battleFought", 10);
        properties.put("questsNotCompleted", -1);
        userCommandDto.setProperties(properties);

        UserProfile userProfile = userProfileService.get(UserId.valueOf(USER_ID));

        UserProfile updatedUserProfile = userProfileService.runCommandOnUserProfile(userProfile, userCommandDto);

        assertEquals(UserProfilePropertyValue.valueOf(20), updatedUserProfile.userProfileProperties().get(UserProfilePropertyName.valueOf("battleFought")));
    }

    @Test
    void runCommandOnUserProfile_collectCommand(UserProfileService userProfileService, UserProfileDao userProfileDao) {
        String USER_ID = "de4310e5-b139-441a-99db-77c9c4a5fada";
        Map<UserProfilePropertyName, UserProfilePropertyValue> userProperties = new HashMap<>();
        List<String> inventory = new ArrayList<>();
        inventory.add("sword1");
        inventory.add("sword2");
        inventory.add("shield1");

        userProperties.put(UserProfilePropertyName.valueOf("inventory"), UserProfilePropertyValue.valueOf(inventory));
        UserProfile newUserProfile  = new UserProfile(UserId.valueOf(USER_ID), LocalDateTime.MAX.toInstant(ZoneOffset.UTC), userProperties);

        userProfileDao.put(newUserProfile);


        UserCommandDto userCommandDto = new UserCommandDto();
        // Set valid properties for the command object
        userCommandDto.setType("collect");
        userCommandDto.setUserId(USER_ID);
        HashMap<String, Object> properties = new HashMap<>();
        List<String> updatedInventory = new ArrayList<>();
        inventory.add("sword3");
        properties.put("inventory", updatedInventory);
        userCommandDto.setProperties(properties);

        UserProfile userProfile = userProfileService.get(UserId.valueOf(USER_ID));

        UserProfile updatedUserProfile = userProfileService.runCommandOnUserProfile(userProfile, userCommandDto);
        UserProfilePropertyValue updateInventory = updatedUserProfile.userProfileProperties().get(UserProfilePropertyName.valueOf("inventory"));
        List<String> invenotryList = (List<String>) updateInventory.getValue();

        assertEquals(Boolean.TRUE, invenotryList.contains("sword3") );
    }
//
//    @Test
//    void runCommandOnUserProfile_collectCommand() {
//        UserProfile userProfile = /* Initialize a UserProfile instance with somePropertyName having a list value */;
//        UserCommandDto userCommandDto = new UserCommandDto();
//        userCommandDto.setType("collect");
//        Map<String, Object> properties = new HashMap<>();
//        properties.put("somePropertyName", List.of("newValue1", "newValue2"));
//        userCommandDto.setProperties(properties);
//
//        UserProfile updatedUserProfile = userProfileService.runCommandOnUserProfile(userProfile, userCommandDto);
//
//        assertEquals(/* The updated list */, updatedUserProfile.userProfileProperties().get(/* The key corresponding to somePropertyName */));
//    }
//
//    @Test
//    void runCommandOnUserProfile_invalidCommand() {
//        UserProfile userProfile = /* Initialize a UserProfile instance */;
//        UserCommandDto userCommandDto = new UserCommandDto();
//        userCommandDto.setType("invalidCommand");
//        Map<String, Object> properties = new HashMap<>();
//        properties.put("somePropertyName", "newValue");
//        userCommandDto.setProperties(properties);
//
//        assertThrows(IllegalStateException.class, () -> userProfileService.runCommandOnUserProfile(userProfile, userCommandDto));
//    }
}
