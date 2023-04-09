package com.spotlight.platform.userprofile.api.web.resources;

import com.spotlight.platform.userprofile.api.core.profile.persistence.UserProfileDao;
import com.spotlight.platform.userprofile.api.model.profile.UserProfile;
import com.spotlight.platform.userprofile.api.model.profile.primitives.UserProfileFixtures;
import com.spotlight.platform.userprofile.api.web.UserProfileApiApplication;

import com.spotlight.platform.userprofile.api.web.dto.UserCommandDto;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import ru.vyarus.dropwizard.guice.test.ClientSupport;
import ru.vyarus.dropwizard.guice.test.jupiter.TestDropwizardApp;

import javax.ws.rs.client.Entity;

import java.util.HashMap;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;

@TestDropwizardApp(value = UserProfileApiApplication.class, randomPorts = true)
class UserResourceTestIntegrationTest {

    @Nested
    @DisplayName("getUserProfile")
    class GetUserProfile {

        private static final String USER_ID_PATH_PARAM = "userId";
        private static final String URL = "/users/{%s}/profile".formatted(USER_ID_PATH_PARAM);

        @Test
        void nonExistingUser_returns404(ClientSupport client) {
            var response = client.targetRest()
                    .path(URL)
                    .resolveTemplate(USER_ID_PATH_PARAM, UserProfileFixtures.NON_EXISTING_USER_ID)
                    .request()
                    .get();
            assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND_404);
        }

        @Test
        void existingUser_correctObjectIsReturned(ClientSupport client, UserProfileDao userProfileDao) {
            userProfileDao.put(UserProfileFixtures.USER_PROFILE);
            var response = client.targetRest().path(URL).resolveTemplate(USER_ID_PATH_PARAM, UserProfileFixtures.USER_ID).request().get();

            assertThat(response.getStatus()).isEqualTo(HttpStatus.OK_200);
            assertThatJson(response.readEntity(UserProfile.class)).isEqualTo(UserProfileFixtures.SERIALIZED_USER_PROFILE);
        }
    }

    @Nested
    @DisplayName("updateUserProfile")
    class UpdateUserProfile {
        private static final String USER_ID = "de4310e5-b139-441a-99db-77c9c4a5fada";

        private static final String URL = "/users/%s/profile/command".formatted(USER_ID);;

        @Test
        void validInput_updatesUserProfile(ClientSupport client, UserProfileDao userProfileDao) {
            userProfileDao.put(UserProfileFixtures.USER_PROFILE);

            UserCommandDto command = new UserCommandDto();
            // Set valid properties for the command object
            command.setType("increment");
            command.setUserId(USER_ID);
            HashMap<String, Object> properties = new HashMap<>();
            properties.put("battleFought", 10);
            properties.put("questsNotCompleted", -1);
            command.setProperties(properties);

            var response = client.targetRest()
                    .path(URL)
                    .request()
                    .post(Entity.json(command));

            assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT_204);

            // Verify the updated UserProfile in the UserProfileDao
        }

        @Test
        void invalidCommandInput_returns422(ClientSupport client) {
            UserCommandDto command = new UserCommandDto();

            // Set valid properties for the command object
            command.setType("inrement");
            command.setUserId("de4310e5-b139-441a-99db-77c9c4a5fada");
            HashMap<String, Object> properties = new HashMap<>();
            properties.put("battleFought", 10);
            properties.put("questsNotCompleted", -1);
            command.setProperties(properties);


            var response = client.targetRest()
                    .path(URL)
                    .request()
                    .post(Entity.json(command));

            assertThat(response.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY_422);
        }


        @Test
        void invalidCommandFormatInput_returns400(ClientSupport client) {
            UserCommandDto command = new UserCommandDto();

            // Set valid properties for the command object
            command.setType("increment");
            command.setUserId("de4310e5-b139-441a-99db-77c9c4a5fada");
            HashMap<String, Object> properties = new HashMap<>();
            properties.put("battleFought", "ten");
            properties.put("questsNotCompleted", -1);
            command.setProperties(properties);


            var response = client.targetRest()
                    .path(URL)
                    .request()
                    .post(Entity.json(command));

            assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST_400);
        }
    }
}