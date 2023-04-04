package com.spotlight.platform.userprofile.api.web.resources;

import com.spotlight.platform.userprofile.api.core.profile.persistence.UserProfileDao;
import com.spotlight.platform.userprofile.api.model.profile.UserProfile;
import com.spotlight.platform.userprofile.api.model.profile.primitives.UserProfileFixtures;
import com.spotlight.platform.userprofile.api.web.UserProfileApiApplication;

import org.eclipse.jetty.http.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import ru.vyarus.dropwizard.guice.test.ClientSupport;
import ru.vyarus.dropwizard.guice.test.jupiter.TestDropwizardApp;

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
}